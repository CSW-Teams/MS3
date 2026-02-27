# Final Quality Gate: Comment Completeness and Readability (Post-`5c533ad4`)

## Purpose
This quality gate defines the final review needed to confirm that all code introduced or modified after commit `5c533ad4` has complete, clear, and accurate comments.

It is reusable for future comment hardening cycles by changing only the baseline commit and the scope inventory.

## Inputs Required Before Review
- **Baseline commit**: `5c533ad4` (or another baseline for future cycles).
- **Scope inventory**: list of post-baseline files included in the review.
- **Intentional exclusions list**: files that are not part of the cycle, with rationale.

---

## Pass/Fail Checklist (File-Level)
Evaluate every in-scope file against each criterion.

### Criterion 1 — Intent Comments Present Where Needed
- **Pass** if each class/component/file with non-trivial responsibility includes at least one clear intent or summary comment where needed.
- **Fail** if a file needs intent context but has no class/component intent comment.

### Criterion 2 — Complex Methods Include Rationale
- **Pass** if methods/functions with complex branches, constraints, or trade-offs include rationale comments explaining why the logic exists.
- **Fail** if complex logic has comments that only restate code behavior or has no rationale.

### Criterion 3 — Language Is Simple English
- **Pass** if comments are in simple English, with short, direct wording understandable by non-specialist reviewers.
- **Fail** if comments contain unclear jargon, mixed languages, or unnecessarily complex phrasing.

### Criterion 4 — No Stale or Contradictory Comments
- **Pass** if comments are aligned with current behavior and do not conflict with code or other comments.
- **Fail** if comments describe removed behavior, outdated assumptions, or contradictory statements.

### File Decision Rule
- **File PASS**: all four criteria pass.
- **File FAIL**: one or more criteria fail.

### Gate Decision Rule
- **Gate PASS**: all reviewed in-scope files pass.
- **Gate FAIL**: at least one reviewed in-scope file fails.

---

## Reviewer Workflow

### Step 1 — File-by-File Review Against Scope Inventory
1. Start from the approved scope inventory of post-baseline files.
2. Review each file sequentially and record pass/fail per criterion.
3. Add concise evidence notes for each fail to enable targeted fixes.

### Step 2 — Module Owner Spot-Check
Perform spot-checks after the file-by-file pass:
- **Backend owner** reviews a sample of Java/Spring files.
- **Frontend owner** reviews a sample of React/JavaScript files.
- **DB owner** reviews a sample of SQL/migration files.

Spot-check objective: verify consistency of comment quality across modules, not only per-file checklist completion.

### Step 3 — Final Presentation-Readability Pass
Run a final readability pass focused on external presentation quality:
- Comments are understandable when read aloud in review/demo contexts.
- Comments explain intent and rationale clearly for mixed technical/non-technical audiences.
- No wording that could create confusion during stakeholder presentations.

---

## Required Final Report (Mandatory Output)
Every cycle must end with a report containing:

1. **Files reviewed**
   - Full list of in-scope files evaluated by the checklist.
2. **Files intentionally excluded**
   - Explicit list of excluded files with reason for exclusion.
3. **Open comment debt**
   - Any remaining comment issues not fixed in the cycle.
   - For each debt item: file, issue summary, risk, and planned follow-up.

If no debt remains, report `Open comment debt: none`.

---

## Reusable Template for Future Comment Hardening Cycles

Use this section as a copy/paste template.

### A) Cycle Header
- Baseline commit: `<commit_hash>`
- Review date: `<YYYY-MM-DD>`
- Reviewers: `<names/roles>`

### B) Scope Inputs
- Scope inventory source: `<path/link>`
- Included file count: `<n>`
- Excluded file count: `<n>`

### C) File Review Table
| File | Intent comments present | Complex rationale comments | Simple English | No stale/contradictory comments | File result | Notes |
|---|---|---|---|---|---|---|
| `<path>` | Pass/Fail | Pass/Fail | Pass/Fail | Pass/Fail | Pass/Fail | `<evidence>` |

### D) Spot-Check Log
- Backend spot-check: `<files sampled + result>`
- Frontend spot-check: `<files sampled + result>`
- DB spot-check: `<files sampled + result>`

### E) Presentation-Readability Outcome
- Result: `Pass/Fail`
- Notes: `<short rationale>`

### F) Final Gate Decision
- Gate decision: `PASS/FAIL`
- Blocking issues (if any): `<list>`

### G) Final Report Summary
- Files reviewed: `<list or link>`
- Files intentionally excluded: `<list + reasons>`
- Open comment debt: `<none or list with follow-up>`
