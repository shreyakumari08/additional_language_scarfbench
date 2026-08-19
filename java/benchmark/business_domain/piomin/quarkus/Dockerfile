FROM maven:3.9.12-ibm-semeru-21-noble

USER root
# Python toolchain for the held-out behavioral oracle (smoke.py runs under pytest).
RUN apt-get update && apt-get install -y --no-install-recommends curl lsof python3-venv python3-full \
    && curl -LsSf https://astral.sh/uv/install.sh | sh

ENV PATH="/root/.local/bin:$PATH"

# Virtualenv with pytest so `python3 /app/smoke.py` resolves the grader deps.
RUN uv venv /opt/venv \
    && uv pip install --python /opt/venv/bin/python pytest
ENV PATH="/opt/venv/bin:$PATH"

WORKDIR /app

# Copy the whole app (source, pom, smoke.py, Makefile, test.sh)
COPY . .

# Build the application at image build time
RUN mvn clean install -DskipTests

RUN chmod +x test.sh || true

# Quarkus dev mode prints "Listening on:" -> the Makefile SUCCESS_PATTERN.
CMD ["mvn", "quarkus:run"]
