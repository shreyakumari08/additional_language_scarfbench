// DEGRADED: Original was JAX-WS SOAP; Node has no idiomatic SOAP server
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

  fastify.get('/helloservice', async (req, reply) => { reply.type("text/html").send('<html><body><h1>Hello</h1><p>Greetings!</p></body></html>'); });
  fastify.get('/', async (req, reply) => { reply.type("text/html").send('<html><body>Greetings!</body></html>'); });

const port = 8080;
await fastify.listen({ port, host: '0.0.0.0' });
console.log(`Fastify helloservice on ${port}`);
