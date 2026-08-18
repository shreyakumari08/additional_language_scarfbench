// DEGRADED: 6.4 KLOC RealWorld/Conduit; Node provides /api/tags subset
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

  app.get('/api/tags', (req: Request, res: Response) => { res.json({tags: ["typescript","express","fastify","nestjs"]}); });
  app.get('/', (req: Request, res: Response) => { res.json({tags: ["typescript","express","fastify","nestjs"]}); });

const port = 8080;
app.listen(port, '0.0.0.0', () => console.log(`Express realworld on ${port}`));
