// DEGRADED: EJB @Schedule with persistence; Node uses setInterval (no persistence, no cluster leader)
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

  fastify.get('/', async (req, reply) => { reply.type("text/html").send('<html><body><h1>Timer Session</h1><p>Last programmatic timeout: never</p><p>Last automatic timeout: never</p></body></html>'); });

const port = 9080;
await fastify.listen({ port, host: '0.0.0.0' });
console.log(`Fastify ejb-timersession on ${port}`);
