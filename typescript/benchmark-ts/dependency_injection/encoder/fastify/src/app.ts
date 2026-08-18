// FULL-fidelity
import Fastify from 'fastify';
import formbody from '@fastify/formbody';
const fastify = Fastify({ logger: false });
await fastify.register(formbody);

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

  fastify.get('/encoder', async (req, reply) => { const b = req.body as any; const q = req.query as any; const s = String(b?.inputString ?? q?.inputString ?? ""); reply.type("text/plain").send("Coded: " + shift(s)); });
  fastify.post('/encoder', async (req, reply) => { const b = req.body as any; const q = req.query as any; const s = String(b?.inputString ?? q?.inputString ?? ""); reply.type("text/plain").send("Coded: " + shift(s)); });
  fastify.get('/', async (req, reply) => { reply.type("text/html").send('OK'); });

const port = 8080;
await fastify.listen({ port, host: '0.0.0.0' });
console.log(`Fastify encoder on ${port}`);
