import {
  Injectable,
  NestInterceptor,
  ExecutionContext,
  CallHandler,
  Logger,
} from '@nestjs/common';
import { Observable, of } from 'rxjs';
import { tap } from 'rxjs/operators';

// In-memory cache for idempotency during free tier
const idempotencyCache = new Map<string, { statusCode: number; data: any; timestamp: number }>();

@Injectable()
export class IdempotencyInterceptor implements NestInterceptor {
  private readonly logger = new Logger(IdempotencyInterceptor.name);
  private readonly TTL_MS = 24 * 60 * 60 * 1000; // 24 hours

  intercept(context: ExecutionContext, next: CallHandler): Observable<any> {
    const request = context.switchToHttp().getRequest();
    const requestId = request.headers['client-request-id'] || request.headers['x-request-id'];

    if (!requestId || ['GET', 'HEAD', 'OPTIONS'].includes(request.method)) {
      return next.handle();
    }

    const cached = idempotencyCache.get(requestId);
    if (cached && Date.now() - cached.timestamp < this.TTL_MS) {
      this.logger.log(`Idempotent hit for request ID: ${requestId}`);
      const response = context.switchToHttp().getResponse();
      response.status(cached.statusCode);
      return of(cached.data);
    }

    return next.handle().pipe(
      tap((data) => {
        const response = context.switchToHttp().getResponse();
        idempotencyCache.set(requestId, {
          statusCode: response.statusCode || 200,
          data,
          timestamp: Date.now(),
        });
      }),
    );
  }
}
