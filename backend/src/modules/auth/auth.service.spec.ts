import { Test, TestingModule } from '@nestjs/testing';
import { AuthService } from './auth.service';
import { JwtService } from '@nestjs/jwt';
import { ConfigService } from '@nestjs/config';
import { SupabaseService } from '../../common/supabase.service';
import { UnauthorizedException } from '@nestjs/common';
import * as bcrypt from 'bcrypt';

describe('AuthService', () => {
  let service: AuthService;
  let supabaseMock: any;
  let jwtMock: any;

  beforeEach(async () => {
    supabaseMock = {
      getClient: jest.fn().mockReturnValue({
        from: jest.fn(),
      }),
    };

    jwtMock = {
      sign: jest.fn().mockReturnValue('mock_token'),
    };

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        AuthService,
        { provide: SupabaseService, useValue: supabaseMock },
        { provide: JwtService, useValue: jwtMock },
        {
          provide: ConfigService,
          useValue: {
            get: jest.fn().mockImplementation((key: string) => {
              if (key === 'SUPABASE_JWT_SECRET') return 'test_secret';
              return null;
            }),
          },
        },
      ],
    }).compile();

    service = module.get<AuthService>(AuthService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  it('should throw UnauthorizedException if driver phone does not exist', async () => {
    supabaseMock.getClient().from.mockReturnValue({
      select: jest.fn().mockReturnValue({
        eq: jest.fn().mockReturnValue({
          single: jest.fn().mockResolvedValue({ data: null, error: { message: 'Not found' } }),
        }),
      }),
    });

    await expect(
      service.loginDriver({ phone: '+919847000000', pin: '1234' }),
    ).rejects.toThrow(UnauthorizedException);
  });

  it('should throw UnauthorizedException if driver is pending verification', async () => {
    supabaseMock.getClient().from.mockReturnValue({
      select: jest.fn().mockReturnValue({
        eq: jest.fn().mockReturnValue({
          single: jest.fn().mockResolvedValue({
            data: {
              id: '1',
              role: 'driver',
              phone: '+919847000000',
              status: 'pending_verification',
            },
            error: null,
          }),
        }),
      }),
    });

    await expect(
      service.loginDriver({ phone: '+919847000000', pin: '1234' }),
    ).rejects.toThrow(UnauthorizedException);
  });
});
