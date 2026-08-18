# Adapter Review: scarfbench

## Structural Validation

**30 passed | 3 errors | 1 warnings** [FAIL]

### Errors (must fix)

- [ ] **Parity PR link empty**: Entry 0: `adapter_pr` is an empty list. (`adapters/scarfbench/parity_experiment.json:12`)
- [ ] **Parity PR link empty**: Entry 0: `dataset_pr` is an empty list. (`adapters/scarfbench/parity_experiment.json:13`)
- [ ] **Parity PR link empty**: Entry 0: `parity_pr` is an empty list. (`adapters/scarfbench/parity_experiment.json:14`)

### Warnings (recommended)

- [ ] **Metadata: parity_costs**: `parity_costs` is null — consider filling in the cost estimate.

### Passed

- [x] `README.md` exists
- [x] `parity_experiment.json` exists
- [x] `adapter_metadata.json` exists
- [x] `src/scarfbench/` package exists
- [x] `src/scarfbench/adapter.py` exists
- [x] `src/scarfbench/main.py` exists
- [x] `src/scarfbench/task-template/` directory exists
- [x] `src/scarfbench/task-template/task.toml` exists
- [x] `src/scarfbench/task-template/instruction.md` exists
- [x] `src/scarfbench/task-template/environment/Dockerfile` exists
- [x] `src/scarfbench/task-template/tests/test.sh` exists
- [x] `src/scarfbench/task-template/solution/solve.sh` exists
- [x] Template `[task].name` present
- [x] Template `[task].authors` present
- [x] `parity_experiment.json` is valid JSON array
- [x] `adapter_metadata.json` is valid JSON array
- [x] README section `Overview` present
- [x] README section `What is` present
- [x] README section `Adapter Features` present
- [x] README section `Generated Task Structure` present
- [x] README section `Run Evaluation` present
- [x] README section `Usage` present
- [x] README section `Parity` present
- [x] README section `Notes & Caveats` present
- [x] README section `Installation / Prerequisites` present
- [x] README section `Citation` present
- [x] README section `Authors & Contributions` present
- [x] Parity table column count correct
- [x] `test.sh` writes to reward path
- [x] No canary strings found
