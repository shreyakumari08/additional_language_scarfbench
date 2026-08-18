# TypeScript Benchmark

Directory tree: `benchmark-ts/{business_domain,dependency_injection,infrastructure,persistence,presentation,whole_applications}/<app>/<framework>/`.

## Frameworks

| Framework | Version |
|---|---|
| Express | 4.19 |
| Fastify | 4.28 |
| NestJS | 10.4 |

## Migration paths (examples)

- `express ⇄ fastify`
- `express → nestjs`, `nestjs → fastify`, …

## Run a task

From the repo root:

```bash
./run-task.sh counter express fastify
./run-task.sh standalone express nestjs business_domain
```

## Prereqs

- Node.js 18+ (LTS)
- Docker
- Each app declares its own `package.json`; the harness containerizes it.
