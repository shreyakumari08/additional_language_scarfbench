# =============================================================================
# ScarfBench containerized oracle for a single framework variant.  GENERATED —
# edit harness/scaffold/templates/Makefile.template + regenerate, not this file.
#
# `make test` performs the full ScarfBench execution philosophy and returns a
# DETERMINISTIC pass/fail:
#     build  -> docker build (compile + package the app)
#     up     -> docker run   (deploy the server; wait until it is actually ready)
#     test   -> run the behavioral oracle against the deployed server
#     down   -> tear the container down regardless of outcome
#
# It never assumes the app is already running: the container starts it. The
# oracle runs *inside* the container via `docker exec`, so no host port is
# published and many variants can be tested in parallel without port clashes.
# =============================================================================
SHELL       := /bin/bash
APP_NAME    := scarf-rust-presentation-mood-actix
IMAGE       := scarf-rust-presentation-mood-actix:test
APP_PORT    := 8080
READY_TRIES ?= 90
ORACLE_LIB  := oracle-lib.sh

.PHONY: build up test down clean logs

# Build the image (compile + package).
build:
	docker build -f Dockerfile -t $(IMAGE) .
	@echo "[INFO] built $(IMAGE)"

# Deploy: start the container and block until the app answers on its port.
up: build
	-@docker rm -f $(APP_NAME) >/dev/null 2>&1 || true
	docker run -d --name $(APP_NAME) $(IMAGE)
	@echo "[INFO] waiting for $(APP_NAME) on :$(APP_PORT) ..."
	@for i in $$(seq 1 $(READY_TRIES)); do \
	  if docker exec $(APP_NAME) curl -fsS -o /dev/null "http://localhost:$(APP_PORT)/" 2>/dev/null; then \
	    echo "[INFO] ready after $$i check(s)"; exit 0; \
	  fi; \
	  if ! docker inspect -f '{{.State.Running}}' $(APP_NAME) 2>/dev/null | grep -q true; then \
	    echo "[ERROR] container exited before becoming ready:"; docker logs $(APP_NAME) 2>&1 | tail -40; exit 1; \
	  fi; \
	  sleep 1; \
	done; \
	echo "[ERROR] readiness timeout for $(APP_NAME)"; docker logs $(APP_NAME) 2>&1 | tail -40; exit 1

# Run the behavioral oracle against the deployed server, then always tear down.
test: up
	@rc=0; \
	docker cp ./$(ORACLE_LIB) $(APP_NAME):/tmp/oracle-lib.sh && \
	docker cp ./test.sh       $(APP_NAME):/tmp/test.sh && \
	docker exec \
	   -e BASE_URL="http://localhost:$(APP_PORT)" \
	   -e ORACLE_LIB=/tmp/oracle-lib.sh \
	   $(APP_NAME) bash /tmp/test.sh || rc=$$?; \
	$(MAKE) --no-print-directory down >/dev/null 2>&1 || true; \
	if [ $$rc -eq 0 ]; then echo "[RESULT] PASS $(APP_NAME)"; else echo "[RESULT] FAIL $(APP_NAME) (rc=$$rc)"; fi; \
	exit $$rc

logs:
	docker logs $(APP_NAME)

down:
	-@docker rm -f $(APP_NAME) >/dev/null 2>&1 || true

clean: down
	-@docker rmi $(IMAGE) >/dev/null 2>&1 || true
	@echo "[INFO] cleaned $(IMAGE)"
