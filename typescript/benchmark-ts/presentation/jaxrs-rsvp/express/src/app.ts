// DEGRADED: JAX-RS + JPA event/RSVP tracker; Node uses in-memory events/persons
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

  app.get('/webapi', (req: Request, res: Response) => { res.type("text/html").send('<html><body><h1>jaxrs-rsvp</h1></body></html>'); });
  app.get('/', (req: Request, res: Response) => { res.type("text/html").send('<html><body>OK</body></html>'); });

const port = 8080;
app.listen(port, '0.0.0.0', () => console.log(`Express jaxrs-rsvp on ${port}`));
