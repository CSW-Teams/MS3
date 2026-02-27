# Commenting Quality Gate (post_c72ee7a7)

## Mandatory Closure Checklist

> This checklist is mandatory for closing work items that affect comments or inline documentation.

- [ ] Quality gate run completed against the baseline and target commits.
- [ ] Inventory source used and archived with review notes.
- [ ] File-level pass/fail checks completed for all in-scope files.
- [ ] Spot-check matrix completed across backend, frontend, SQL, and tests.
- [ ] Final PASS/FAIL decision recorded with blockers and open comment debt.
- [ ] Presentation-readiness outcome recorded (speed/clarity goals explicitly assessed).

---

## 1) Baseline / Target and Inventory Source

- **Baseline hash:** `c72ee7a7`
- **Target hash:** `<fill-target-hash>`
- **Inventory source path:** `docs/comment_inventory_post_c72ee7a7.md`
- **Review window:** `<fill-date-range>`
- **Reviewer(s):** `<fill-names>`

**Acceptance rule:**
The gate is invalid unless baseline hash, target hash, and inventory source path are all present and traceable.

---

## 2) File-Level Pass/Fail Criteria (Mandatory)

For each reviewed file, mark **PASS** only if all checks below pass:

1. **Intent clarity**
   - Comments explain *why* (intent/rationale), not only *what* the code does.
2. **Simple English**
   - Wording is concise, plain, and understandable by cross-functional readers.
3. **Correctness**
   - Comment text matches real behavior, constraints, and current implementation.
4. **No stale/misleading comments**
   - Outdated, contradictory, or ambiguous comments are removed or corrected.

**Per-file result format:**
- `path/to/file.ext` — PASS/FAIL — notes

Any single failed criterion makes the file **FAIL**.

---

## 3) Spot-Check Matrix (Backend / Frontend / SQL / Tests)

| Area | Sampled Files (min 3 or all if fewer) | Checks Applied | Result | Notes |
|---|---|---|---|---|
| Backend (Java/Spring) | `<list files>` | Intent clarity, simple English, correctness, stale/misleading | PASS/FAIL | `<notes>` |
| Frontend (React/JS/TS/CSS) | `<list files>` | Intent clarity, simple English, correctness, stale/misleading | PASS/FAIL | `<notes>` |
| SQL (migrations/queries) | `<list files>` | Intent clarity, simple English, correctness, stale/misleading | PASS/FAIL | `<notes>` |
| Tests (unit/integration/e2e) | `<list files>` | Intent clarity, simple English, correctness, stale/misleading | PASS/FAIL | `<notes>` |

**Sampling rule:**
If a domain has more than 3 touched files, sample at least 3 high-impact files plus any file with previously known comment debt.

---

## 4) Final PASS/FAIL Decision

- **Quality Gate Outcome:** PASS / FAIL
- **Blocking issues (must be empty for PASS):**
  1. `<blocking issue or "None">`
  2. `<blocking issue or "None">`
- **Open comment debt (non-blocking, tracked):**
  - `<debt item + owner + due sprint/date>`
  - `<debt item + owner + due sprint/date>`

**Decision rule:**
- **PASS** only when no blocking issues remain and all mandatory sections are complete.
- **FAIL** if any blocking issue exists or any mandatory section is incomplete.

---

## 5) Presentation-Readiness Outcome (Speed/Clarity)

- **Outcome:** READY / NOT READY
- **Explanation speed goal met:** YES / NO
- **Explanation clarity goal met:** YES / NO
- **Rationale (short):**
  - `<1–3 bullets summarizing whether explanation speed/clarity goals are met and why>`

**Required explicit statement:**
`Presentation-readiness is [READY/NOT READY]; explanation speed goal is [met/not met], and explanation clarity goal is [met/not met].`
