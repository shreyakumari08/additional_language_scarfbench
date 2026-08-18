// DEGRADED: 14 KLOC IBM DayTrader (EJB, JMS, MDB); Node provides REST subset
import express, { Request, Response } from 'express';
const app = express();
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

let counterValue = 0;
const CIPHER_IN  = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
const CIPHER_OUT = "bcdefghijklmnopqrstuvwxyzaBCDEFGHIJKLMNOPQRSTUVWXYZA";
function shift(s: string): string {
  let out = '';
  for (const ch of s) {
    const i = CIPHER_IN.indexOf(ch);
    out += i >= 0 ? CIPHER_OUT[i] : ch;
  }
  return out;
}

  app.get('/daytrader/', (req: Request, res: Response) => { res.type("text/html").send('<html><body><h1>daytrader</h1></body></html>'); });
  app.get('/', (req: Request, res: Response) => { res.type("text/html").send('<html><body>OK</body></html>'); });

const port = 9080;
app.listen(port, '0.0.0.0', () => console.log(`Express daytrader on ${port}`));
