// DEGRADED: EJB @Schedule with persistence; Node uses setInterval (no persistence, no cluster leader)
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
  handler0(@Res() res: Response) { res.type("text/html").send('<html><body><h1>Timer Session</h1><p>Last programmatic timeout: never</p><p>Last automatic timeout: never</p></body></html>'); }
}
