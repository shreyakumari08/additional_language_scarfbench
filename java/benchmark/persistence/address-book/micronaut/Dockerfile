FROM maven:3.9.12-ibm-semeru-21-noble

USER root
RUN apt-get update && apt-get install -y --no-install-recommends curl python3-venv python3-full     && curl -LsSf https://astral.sh/uv/install.sh | sh

ENV PATH="/root/.local/bin:$PATH"

ENV PLAYWRIGHT_BROWSERS_PATH=/ms-playwright
RUN mkdir -p /ms-playwright && chmod 755 /ms-playwright

RUN uv venv /opt/venv     && uv pip install --python /opt/venv/bin/python playwright==1.47.0 pytest

ENV PATH="/opt/venv/bin:$PATH"
RUN playwright install --with-deps chromium

WORKDIR /app

COPY . .

COPY test.sh .
RUN chmod +x test.sh

RUN mvn clean package -DskipTests

CMD ["java", "-jar", "target/address-book.jar"]
