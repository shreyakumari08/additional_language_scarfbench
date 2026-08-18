// FULL-fidelity
import { Controller, Get, Post, Req, Res, Query } from '@nestjs/common';
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

  @Get('/response')
  handler0(@Req() req: any, @Res() res: Response) { const n = String(req.body?.name ?? req.query?.name ?? "world"); res.type("text/html").send("Hello, " + n.toLowerCase()); }

  @Post('/response')
  handler1(@Req() req: any, @Res() res: Response) { const n = String(req.body?.name ?? req.query?.name ?? "world"); res.type("text/html").send("Hello, " + n.toLowerCase()); }

  @Get('/')
  handler2(@Res() res: Response) { res.type("text/html").send('OK'); }
}
