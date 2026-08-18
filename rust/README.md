# Rust Benchmark

Directory tree: `benchmark-rs/{business_domain,dependency_injection,infrastructure,persistence,presentation,whole_applications}/<app>/<framework>/`.

## Frameworks

| Framework | Version |
|---|---|
| Axum | 0.7 |
| Actix Web | 4 |
| Rocket | 0.5 |

## Migration paths (examples)

- `axum ⇄ actix`
- `axum → rocket`, `rocket → actix`, …

## Run a task

From the repo root:

```bash
./run-task.sh counter axum rocket
./run-task.sh standalone axum actix business_domain
```

## Prereqs

- Rust (stable, 2021+)
- Docker
- Each app has its own `Cargo.toml`; the harness containerizes it.
