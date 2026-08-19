## ScarfBench Migration Task: persistence__roster__spring__quarkus

You are an expert Java engineer. You are given the **roster**
application implemented with the **Spring** framework. Your job is to
**migrate it to Quarkus** so that it compiles, deploys, and passes the
application's smoke tests — while preserving the original behaviour and HTTP
endpoints.

### Working Directory

The source application is provided at **`/workspace/app`**. Edit it in place so
that it becomes a working **Quarkus** application.

```
/workspace/app/        # the Spring application you must migrate to Quarkus
```

### What to Do

1. Convert the build / dependency configuration (the project's manifest — e.g.
   `pom.xml`, `package.json`, `Cargo.toml`, or `requirements.txt`) to the
   equivalent for **Quarkus**.
2. Port the application code — controllers/resources, persistence, configuration,
   dependency injection, and templates/static assets — to **Quarkus** idioms.
3. Keep every user-facing route and response behaviour identical to the original;
   the smoke tests exercise the running application through its web UI and HTTP
   endpoints.
4. Ensure the app **builds** and **starts up cleanly** (it must begin serving on
   port `8080`).

### Requirements

- **Same behaviour**: preserve all endpoints, page content, and semantics of the
  original Spring application.
- **Idiomatic Quarkus**: use the target framework's conventions, not a thin
  shim over the old one.
- **Complete migration**: do not leave the app half-ported; it must compile and
  run end-to-end.

### Evaluation

The verifier reproduces ScarfBench's own grading pipeline against your migrated
app:

1. ✅ **Compile** — the application builds successfully.
2. ✅ **Deploy** — the application starts and begins listening on port `8080`.
3. ✅ **Smoke tests** — the app's Playwright/`requests` smoke suite passes.

Your reward is the fraction of smoke tests that pass (compile/deploy failures
score 0). Aim for **all** smoke tests passing.
