// DEGRADED: JSR-356 WebSocket push; Node provides HTTP polling fallback
import { Controller, Get, Req, Res, Query } from '@nestjs/common';
import type { Response } from 'express';

@Controller()
export class AppController {
  private counterValue = 0;
  private readonly CIPHER_IN  = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private readonly CIPHER_OUT = "bcdefghijklmnopqrstuvwxyzaBCDEFGHIJKLMNOPQRSTUVWXYZA";
  private shift(s: string): string {
    let out = '';
    for (const ch of s) {
      const i = this.CIPHER_IN.indexOf(ch);
      out += i >= 0 ? this.CIPHER_OUT[i] : ch;
    }
    return out;
  }

  @Get('/')
  tick(@Res() res: Response) { const tick = (99 + Math.random() * 2).toFixed(2); const vol = Math.floor(100000 + Math.random() * 900000); res.type("text/plain").send(`Current tick: ${tick} / ${vol}`); }
}
