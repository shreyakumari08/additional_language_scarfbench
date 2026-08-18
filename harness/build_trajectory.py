#!/usr/bin/env python3
"""Consolidate task runs under generated-trajectory/ into per-task trajectory.json.

Output format: ATIF-v1.7 (single session per task).

For each task directory the PRIMARY run (run_1, else the first run_*) is emitted
as one ATIF-v1.7 document at <task>/trajectory.json.

Step data is built from the run's agent-transcript.log when present (rich: real
tool_calls, observations and token metrics). When no transcript exists it falls
back to output/CHANGELOG.md (thin: message text only, no tool calls/metrics).
"""
import json, os, re, glob, datetime

ROOT = os.path.dirname(os.path.abspath(__file__))

STEP_RE = re.compile(r'^##\s*\[(?P<ts>[^\]]+)\]\s*\[(?P<level>[^\]]+)\]\s*(?P<title>.+)$')


# --------------------------------------------------------------------------- #
# shared helpers
# --------------------------------------------------------------------------- #
def model_id(name):
    if not name:
        return None
    return name if "/" in name else "anthropic/" + name


def read_instruction(run_dir):
    """Best-effort fetch of the task prompt for ATIF step 1 (source=user)."""
    patterns = [
        os.path.join(run_dir, "harbor", "task-package", "instruction.md"),
        os.path.join(run_dir, "harbor", "jobs", "*", "task-package__*", "instruction.md"),
        os.path.join(run_dir, "input", "instruction.md"),
        os.path.join(run_dir, "instruction.md"),
    ]
    for pat in patterns:
        for p in sorted(glob.glob(pat)):
            if os.path.isfile(p):
                return open(p, encoding="utf-8", errors="replace").read().strip()
    return None


# --------------------------------------------------------------------------- #
# rich path: agent-transcript.log  ->  ATIF
# --------------------------------------------------------------------------- #
def load_jsonl(path):
    with open(path, encoding="utf-8", errors="replace") as f:
        return [json.loads(line) for line in f if line.strip()]


def _text_of(content):
    if isinstance(content, str):
        return content
    parts = [b.get("text", "") for b in (content or [])
             if isinstance(b, dict) and b.get("type") == "text"]
    return "\n".join(p for p in parts if p).strip()


def _prompt_tokens(usage):
    if not usage:
        return 0
    return (usage.get("input_tokens", 0)
            + usage.get("cache_read_input_tokens", 0)
            + usage.get("cache_creation_input_tokens", 0))


def _stringify_result(content):
    if isinstance(content, str):
        return content
    if isinstance(content, list):
        out = []
        for b in content:
            if isinstance(b, dict):
                out.append(b.get("text", "") if b.get("type") == "text" else json.dumps(b))
            else:
                out.append(str(b))
        return "\n".join(out)
    return "" if content is None else str(content)


def atif_from_transcript(run_dir, transcript):
    lines = load_jsonl(transcript)

    session_id = next((l.get("session_id") for l in lines if l.get("session_id")), None)
    version = next((l.get("version") for l in lines if l.get("version")), None)
    model = next((l["message"].get("model") for l in lines
                  if l.get("type") == "assistant" and l.get("message", {}).get("model")), None)

    # index tool_results by tool_use_id (they arrive in user messages)
    results = {}
    for l in lines:
        if l.get("type") != "user":
            continue
        content = l.get("message", {}).get("content")
        if isinstance(content, list):
            for b in content:
                if isinstance(b, dict) and b.get("type") == "tool_result":
                    results[b.get("tool_use_id")] = _stringify_result(b.get("content"))

    steps = []
    instruction = read_instruction(run_dir)
    if instruction:
        steps.append({"step_id": 1, "source": "user", "message": instruction})

    for l in lines:
        if l.get("type") != "assistant":
            continue
        msg = l.get("message", {})
        content = msg.get("content", [])
        tool_calls = [{
            "tool_call_id": b.get("id"),
            "function_name": b.get("name"),
            "arguments": b.get("input", {}),
        } for b in content if isinstance(b, dict) and b.get("type") == "tool_use"]

        step = {
            "step_id": len(steps) + 1,
            "source": "agent",
            "model_name": model_id(msg.get("model") or model),
            "message": _text_of(content),
        }
        if l.get("timestamp"):
            step["timestamp"] = l["timestamp"]
        if tool_calls:
            step["tool_calls"] = tool_calls
            step["observation"] = {"results": [
                {"source_call_id": tc["tool_call_id"],
                 "content": results.get(tc["tool_call_id"], "")}
                for tc in tool_calls
            ]}
        usage = msg.get("usage") or {}
        step["metrics"] = {"prompt_tokens": _prompt_tokens(usage),
                           "completion_tokens": usage.get("output_tokens", 0)}
        steps.append(step)

    # authoritative totals come from the result line (per-message usage is truncated)
    result_line = next((l for l in lines if l.get("type") == "result"), {})
    ru = result_line.get("usage") or {}
    total_p = _prompt_tokens(ru) or sum(s["metrics"]["prompt_tokens"] for s in steps if "metrics" in s)
    total_c = ru.get("output_tokens") or sum(s["metrics"]["completion_tokens"] for s in steps if "metrics" in s)

    return {
        "schema_version": "ATIF-v1.7",
        "session_id": session_id,
        "agent": {"name": "claude-code", "version": version, "model_name": model_id(model)},
        "steps": steps,
        "final_metrics": {
            "total_prompt_tokens": total_p,
            "total_completion_tokens": total_c,
            "total_steps": len(steps),
            "total_cost_usd": result_line.get("total_cost_usd"),
            "duration_ms": result_line.get("duration_ms"),
            "num_turns": result_line.get("num_turns"),
        },
    }


# --------------------------------------------------------------------------- #
# thin path: output/CHANGELOG.md  ->  ATIF (no transcript available)
# --------------------------------------------------------------------------- #
def parse_changelog(path):
    if not os.path.isfile(path):
        return []
    steps, cur = [], None
    with open(path, encoding="utf-8", errors="replace") as fh:
        for line in fh:
            m = STEP_RE.match(line.strip())
            if m:
                if cur:
                    cur["details"] = [d for d in cur["details"] if d]
                    steps.append(cur)
                cur = {"timestamp": m["ts"], "level": m["level"],
                       "title": m["title"].strip(), "details": []}
            elif cur is not None:
                t = line.strip().lstrip("-* ").strip()
                if t and not t.startswith("---"):
                    cur["details"].append(t)
    if cur:
        cur["details"] = [d for d in cur["details"] if d]
        steps.append(cur)
    return steps


def parse_usage(run_dir):
    """Pull modelUsage/cost from validation/agent.err (contains JSON fragments)."""
    err = os.path.join(run_dir, "validation", "agent.err")
    if not os.path.isfile(err):
        return None
    txt = open(err, encoding="utf-8", errors="replace").read()
    usage = {}
    for m in re.finditer(r'"(claude-[^"]+)":\{([^}]*)\}', txt):
        fields = {}
        for fm in re.finditer(r'"(\w+)":([\d.]+)', m[2]):
            fields[fm[1]] = float(fm[2]) if "." in fm[2] else int(fm[2])
        if fields:
            usage[m[1]] = fields
    return usage or None


def atif_from_changelog(run_dir, meta):
    model = model_id(meta.get("model"))
    steps = []
    instruction = read_instruction(run_dir)
    if instruction:
        steps.append({"step_id": 1, "source": "user", "message": instruction})

    for cl in parse_changelog(os.path.join(run_dir, "output", "CHANGELOG.md")):
        body = cl["title"]
        if cl["details"]:
            body += "\n" + "\n".join("- " + d for d in cl["details"])
        step = {"step_id": len(steps) + 1, "source": "agent",
                "model_name": model, "message": body}
        if cl.get("timestamp"):
            step["timestamp"] = cl["timestamp"]
        steps.append(step)

    usage = parse_usage(run_dir) or {}
    agg = next(iter(usage.values()), {})  # first model's fields, if any
    return {
        "schema_version": "ATIF-v1.7",
        "session_id": None,
        "agent": {"name": meta.get("agent", "claude-code"), "version": None, "model_name": model},
        "steps": steps,
        "final_metrics": {
            "total_prompt_tokens": agg.get("inputTokens") or agg.get("input_tokens"),
            "total_completion_tokens": agg.get("outputTokens") or agg.get("output_tokens"),
            "total_steps": len(steps),
            "total_cost_usd": agg.get("costUSD") or agg.get("cost_usd"),
            "source": "changelog-fallback (no agent-transcript.log)",
        },
    }


# --------------------------------------------------------------------------- #
# driver
# --------------------------------------------------------------------------- #
def primary_run(task_dir):
    runs = sorted(glob.glob(os.path.join(task_dir, "run_*")))
    runs = [r for r in runs if os.path.isdir(r)]
    if not runs:
        return None
    for r in runs:
        if os.path.basename(r) == "run_1":
            return r
    return runs[0]


def build_task(task_dir):
    run_dir = primary_run(task_dir)
    if not run_dir:
        return None
    meta_path = os.path.join(run_dir, "metadata.json")
    meta = json.load(open(meta_path)) if os.path.isfile(meta_path) else {}

    transcript = os.path.join(run_dir, "agent-transcript.log")
    if os.path.isfile(transcript):
        return atif_from_transcript(run_dir, transcript)
    return atif_from_changelog(run_dir, meta)


def main():
    for task_dir in sorted(glob.glob(os.path.join(ROOT, "*"))):
        if not os.path.isdir(task_dir):
            continue
        doc = build_task(task_dir)
        if not doc:
            continue
        out = os.path.join(task_dir, "trajectory.json")
        with open(out, "w") as fh:
            json.dump(doc, fh, indent=2)
        # keep exactly one trajectory file per task
        legacy = os.path.join(task_dir, "trajectory.atif.json")
        if os.path.isfile(legacy):
            os.remove(legacy)
        kind = "thin/changelog" if doc["final_metrics"].get("source") else "rich/transcript"
        print(f"Wrote {out}  ({doc['final_metrics']['total_steps']} steps, {kind})")


if __name__ == "__main__":
    main()
