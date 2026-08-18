# Python Benchmark

Directory tree: `benchmark-py/{business_domain,dependency_injection,infrastructure,persistence,presentation,whole_applications}/<app>/<framework>/`.

## Frameworks

| Framework | Version |
|---|---|
| Flask | 3.0 |
| FastAPI | 0.115 |
| Django | 4.2 LTS |

## Migration paths (examples)

- `flask ⇄ fastapi`
- `flask → django`, `django → fastapi`, …

## Run a task

From the repo root:

```bash
./run-task.sh counter flask fastapi
./run-task.sh standalone flask django business_domain
```

## Prereqs

- Python 3.11+
- Docker
- Each app manages its own dependencies (via `requirements.txt`,
  `pyproject.toml`, or `Pipfile`); the harness containerizes it.
