# ScarfBench Harbor task image (runtime-clone flow).
#
# The Harbor task's `environment/` contains ONLY this Dockerfile. All
# application source is cloned from a public mirror of the ScarfBench
# benchmark at image-build time, and staged into three paths inside the
# container:
#
#   /workspace/app  -- SOURCE-framework app (agent-visible, harness stripped)
#   /verifier       -- TARGET-framework held-out grader (smoke.py, smoke/, test.sh)
#   /reference      -- TARGET-framework full app (oracle-only, harness stripped)
#
# Source repository: https://github.com/Ethara-Ai/scarfbench-dataset.git
# Source commit:     d332316acbec6e26e601050c298e87ca77af7f48  (all 26 apps / 58 framework dirs, junk-free)
#
# The verifier (tests/test.sh) builds the migrated app natively with Maven,
# runs it with `java -jar`, and grades it by running /verifier/smoke.py with
# pytest. No Docker daemon is required inside this container.
#
# Pin to the Ubuntu 22.04 (jammy) variant: it ships Python 3.10, for which
# playwright 1.47.0 + greenlet 3.0.3 have prebuilt wheels. The default (Debian
# trixie) image ships Python 3.14, which has no greenlet wheel.
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /workspace

# ---- Per-task ARGs (filled in by adapter.py at generation time) ------------
ARG SOURCE_REPO=https://github.com/Ethara-Ai/scarfbench-dataset.git
ARG APP_ID=business_domain__piomin
ARG SOURCE_FW=spring
ARG TARGET_FW=quarkus

RUN apt-get update && apt-get install -y --no-install-recommends \
        ca-certificates \
        curl \
        git \
        make \
        maven \
        util-linux \
        procps \
        psmisc \
        python3 \
        python3-pip \
    && rm -rf /var/lib/apt/lists/*

# Python test deps for the smoke.py grader (pytest suite; stdlib + requests +
# Playwright + aiohttp cover every smoke.py grader across the benchmark).
# Pin Playwright to the version the benchmark's smoke suites target (1.47.0);
# leaving it unpinned makes pip backtrack into ancient releases with an
# incompatible greenlet pin.
RUN pip3 install --no-cache-dir --timeout 600 --retries 10 \
        pytest requests aiohttp playwright==1.47.0 pytest-playwright==0.5.2
RUN python3 -m playwright install --with-deps chromium

# ---- Maven Central mirror (avoid HTTP 429 from repo.maven.apache.org) ------
ENV MVNW_REPOURL=https://maven-central.storage-download.googleapis.com/maven2
RUN mkdir -p /root/.m2 && printf '%s\n' \
  '<settings>' \
  '  <mirrors>' \
  '    <mirror>' \
  '      <id>gcs-central</id>' \
  '      <name>GCS Maven Central</name>' \
  '      <url>https://maven-central.storage-download.googleapis.com/maven2</url>' \
  '      <mirrorOf>central</mirrorOf>' \
  '    </mirror>' \
  '  </mirrors>' \
  '</settings>' > /root/.m2/settings.xml

# ---- Clone the mirror, stage /workspace/app + /verifier + /reference -------
RUN git clone --depth 1 "${SOURCE_REPO}" /tmp/scarf-src \
 && mkdir -p /workspace/app /verifier /reference \
 # 1. Agent workspace: SOURCE framework app, harness stripped.
 && cp -a /tmp/scarf-src/apps/${APP_ID}/${SOURCE_FW}/. /workspace/app/ \
 && rm -f  /workspace/app/Dockerfile /workspace/app/Makefile \
           /workspace/app/test.sh    /workspace/app/smoke.py \
 && rm -rf /workspace/app/smoke \
 # 2. Held-out verifier: TARGET framework's grader files only.
 && for f in smoke.py test.sh; do \
        if [ -f /tmp/scarf-src/apps/${APP_ID}/${TARGET_FW}/$f ]; then \
            cp -a /tmp/scarf-src/apps/${APP_ID}/${TARGET_FW}/$f /verifier/; \
        fi; \
    done \
 && if [ -d /tmp/scarf-src/apps/${APP_ID}/${TARGET_FW}/smoke ]; then \
        cp -a /tmp/scarf-src/apps/${APP_ID}/${TARGET_FW}/smoke /verifier/; \
    fi \
 # 3. Oracle reference: TARGET framework's full app, harness stripped.
 && cp -a /tmp/scarf-src/apps/${APP_ID}/${TARGET_FW}/. /reference/ \
 && rm -f  /reference/Dockerfile /reference/Makefile \
           /reference/test.sh    /reference/smoke.py \
 && rm -rf /reference/smoke \
 && find /workspace/app /reference -type d \( -name __pycache__ -o -name .pytest_cache -o -name node_modules -o -name target -o -name dist -o -name build -o -name .venv -o -name venv \) -exec rm -rf {} + 2>/dev/null || true \
 && rm -rf /tmp/scarf-src

CMD ["/bin/bash"]
