# syntax=docker/dockerfile:1
#
# ScarfBench TypeScript variant image (Express / Fastify / NestJS).
#
# Philosophy mirrors the original ScarfBench Java images: the container is the
# unit of "build + deploy". `docker build` installs deps and compiles/prepares
# the app; the container's CMD *starts the server* (it does NOT assume anything
# is already listening). The behavioral oracle (test.sh) is executed against the
# running server afterwards by the Makefile / verify-task.sh runner.
#
# All three TS frameworks share one image because every variant exposes the same
# `npm start` entrypoint (`npx tsx src/app.ts`) and binds 0.0.0.0:8080.
FROM node:22-alpine

# bash + curl are used by the ScarfBench oracle, which is run inside the
# container via `docker exec` so no host port needs to be published.
RUN apk add --no-cache bash curl

WORKDIR /app

# Install dependencies first so Docker can cache this layer independently of
# source edits. package-lock.json is copied when present for reproducibility.
COPY package*.json ./
RUN npm install --no-audit --no-fund --loglevel=error

# Application sources (and test.sh, tsconfig.json, etc.).
COPY . .

ENV NODE_ENV=production \
    HOST=0.0.0.0
# Informational; a handful of apps bind 9080/8081 instead. Readiness is probed
# by the Makefile/runner on the app's actual port, so this is not relied upon.
EXPOSE 8080

# Start the server (behavior-preserving deploy step).
CMD ["npm", "start"]
