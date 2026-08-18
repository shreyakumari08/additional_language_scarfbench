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

  fastify.get('/', async (req, reply) => { const tick = (99 + Math.random() * 2).toFixed(2); const vol = Math.floor(100000 + Math.random() * 900000); reply.type("text/plain").send(`Current tick: ${tick} / ${vol}`); });

const port = 8080;
await fastify.listen({ port, host: '0.0.0.0' });
console.log(`Fastify dukeetf on ${port}`);
