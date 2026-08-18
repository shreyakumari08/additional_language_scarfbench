// DEGRADED: 61 KLOC Payara multi-microservice stack; Node provides single-app subset
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

  fastify.get('/', async (req, reply) => { reply.type("text/html").send('<html><body><h1>coffee-shop</h1></body></html>'); });

const port = 8080;
await fastify.listen({ port, host: '0.0.0.0' });
console.log(`Fastify coffee-shop on ${port}`);
