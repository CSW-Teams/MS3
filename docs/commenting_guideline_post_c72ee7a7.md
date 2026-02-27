# MS3 Commenting Guideline (post c72ee7a7)

## Purpose
This guideline defines how we write comments in MS3 code after commit `c72ee7a7`.
It helps backend, frontend, QA, and product readers understand code quickly.

## 1) Mandatory language rule
- Write comments in **English only**.
- Use **very simple wording**.
- Use **short sentences**.
- Prefer one idea per sentence.

### Quick rule
If a teammate with basic technical English cannot read it fast, rewrite it.

## 2) Required comment types per code block
For each meaningful code block, include the right comment type(s):

1. **File summary**
   - At the top of the file.
   - Explain what this file is responsible for.

2. **Method intent**
   - Above methods or functions with business behavior.
   - Explain the method goal in plain language.

3. **Non-obvious decision**
   - Near logic that is not immediately clear.
   - Explain *why* we chose this approach.

4. **Edge-case behavior**
   - Near conditionals for unusual inputs or fallback paths.
   - Explain what happens and why.

## 3) Presentation readability rule
Each key comment must be understandable in **under 10–15 seconds** by a mixed audience.

Mixed audience means:
- Java/Spring developers
- React developers
- QA engineers
- Product stakeholders with light technical context

### Practical check
- Keep comments short (usually 1–3 lines).
- Put the most important point first.
- Avoid nested explanations in one block.

## 4) Forbidden patterns
Do **not** write comments like these:

- **Obvious comments**
  - Bad: `// increment i`
  - Reason: reader can already see this.

- **Stale comments**
  - Comments that no longer match code behavior.
  - Rule: update or delete during code changes.

- **Long paragraphs**
  - Avoid large text blocks in code.
  - Move detailed reasoning to docs if needed.

- **Domain jargon without short clarification**
  - If using terms like "tenant rollover" or "doctor slot freeze", add a short plain explanation.

## 5) Small examples (MS3-like naming)

### Java (Spring service)
```java
/**
 * Handles doctor profile updates from the admin workflow.
 */
@Service
public class DoctorProfileService {

    /**
     * Updates editable profile fields for one doctor.
     */
    public DoctorProfile updateProfile(Long doctorId, UpdateDoctorProfileRequest request) {
        // We reload from DB to avoid updating a stale detached entity.
        DoctorProfile profile = doctorProfileRepository.findByDoctorId(doctorId)
            .orElseThrow(() -> new NotFoundException("Doctor not found"));

        // Edge case: empty phone means the user removed it intentionally.
        if (request.getPhone() != null && request.getPhone().trim().isEmpty()) {
            profile.setPhone(null);
        }

        return doctorProfileRepository.save(profile);
    }
}
```

### React / JavaScript
```javascript
// Shows doctor cards in the admin list page.
export function DoctorListPage({ doctors, onSelectDoctor }) {
  // We keep client-side filtering for instant UX on small pages.
  const activeDoctors = doctors.filter((d) => d.status === 'ACTIVE');

  // Edge case: show empty state when API returns no active doctors.
  if (activeDoctors.length === 0) {
    return <EmptyState message="No active doctors found" />;
  }

  return <DoctorTable rows={activeDoctors} onRowClick={onSelectDoctor} />;
}
```

### SQL (PostgreSQL)
```sql
-- Returns upcoming appointments for one doctor in dashboard order.
SELECT a.id, a.patient_name, a.start_time
FROM appointment a
WHERE a.doctor_id = :doctorId
  -- Edge case: canceled appointments must stay hidden in UI counters.
  AND a.status <> 'CANCELED'
ORDER BY a.start_time ASC;
```

### Tests (JUnit)
```java
@Test
void shouldReturnEmptySlotsWhenDoctorHasNoSchedule() {
    // Intent: API should respond with an empty list, not an error.
    when(scheduleRepository.findByDoctorId(42L)).thenReturn(Collections.emptyList());

    List<TimeSlotDto> result = availabilityService.getAvailableSlots(42L, LocalDate.now());

    // Edge case behavior: empty input schedule still means 200 + empty data.
    assertThat(result).isEmpty();
}
```

## Review checklist
Before merging, verify:
- Comments are English only.
- Comments are short and simple.
- All required comment types are present where needed.
- Non-obvious and edge-case paths are explained.
- No forbidden patterns remain.
