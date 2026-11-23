import { Controller, Get, Post, Query, Body, UseGuards, Request } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiResponse, ApiBearerAuth } from '@nestjs/swagger';
import { ReleasesService } from './releases.service';
import { AuthGuard } from '@nestjs/passport';

@ApiTags('App Updates')
@Controller('app')
export class ReleasesController {
  constructor(private readonly releasesService: ReleasesService) {}

  @Get('version')
  @ApiOperation({ summary: 'Check latest app version and update requirements' })
  @ApiResponse({ status: 200, description: 'Version metadata and update gate flags' })
  async checkVersion(
    @Query('flavor') flavor: string,
    @Query('versionCode') versionCode?: string,
  ) {
    const code = versionCode ? parseInt(versionCode, 10) : 0;
    return this.releasesService.getLatestVersion(flavor, code);
  }

  @Post('installed-version')
  @UseGuards(AuthGuard('jwt'))
  @ApiBearerAuth()
  @ApiOperation({ summary: 'Report successfully installed app version' })
  async reportVersion(
    @Request() req: any,
    @Body() body: { versionCode: number; versionName: string },
  ) {
    return this.releasesService.reportInstalledVersion(
      req.user.id,
      body.versionCode,
      body.versionName,
    );
  }
}
