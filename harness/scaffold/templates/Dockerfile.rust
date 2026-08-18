# syntax=docker/dockerfile:1
#
# ScarfBench Rust variant image (Axum / Actix Web / Rocket).
#
# Multi-stage: the builder compiles the single [[bin]] crate in release mode
# (using Cargo.lock when present for reproducibility); the runtime stage carries
# only the produced binary plus bash/curl for the oracle. As with the Java
# images, `docker build` performs the build and the container CMD *starts the
# server* on 0.0.0.0:8080 — it never assumes the app is already running.
#
# One image serves all three Rust frameworks: each crate produces exactly one
# executable, which we copy to a stable path so the runtime CMD is uniform.
FROM rust:1-slim AS build
WORKDIR /app
COPY . .
# Prefer a locked build for reproducibility; fall back if no Cargo.lock exists.
RUN cargo build --release --locked || cargo build --release
# Exactly one [[bin]] per crate — copy it to a predictable name.
RUN set -eux; \
    bin="$(find target/release -maxdepth 1 -type f -perm -u+x ! -name '*.d' | head -n1)"; \
    test -n "$bin"; \
    cp "$bin" /app/server

FROM debian:stable-slim
RUN apt-get update \
    && apt-get install -y --no-install-recommends bash curl ca-certificates \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /app/server /usr/local/bin/server
ENV HOST=0.0.0.0
# Informational; a handful of apps bind 9080/8081 instead. Readiness is probed
# by the Makefile/runner on the app's actual port, so this is not relied upon.
EXPOSE 8080

CMD ["server"]
