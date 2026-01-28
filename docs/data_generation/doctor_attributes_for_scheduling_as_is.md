# Doctor Attributes Impacting Scheduling (As-Is)

## 1. Overview

This report enumerates doctor (“medico”) attributes and related reference data that affect scheduling generation and validation in MS3 as implemented today. The focus is on where doctor data is loaded, how it is checked by the scheduling engine, and what must exist to avoid runtime errors when generating schedules.

**Scheduling entry points & workflow touchpoints**

* REST entry points
  * `ScheduleRestEndpoint#createSchedule` and `#recreateSchedule` trigger generation/regeneration of schedules. (`src/main/java/org/cswteams/ms3/rest/ScheduleRestEndpoint.java`)
  * `ConcreteShiftRestEndpoint#addConcreteShift` and `#modifyConcreteShift` allow manual shift insertion and validation through the same constraint pipeline. (`src/main/java/org/cswteams/ms3/rest/ConcreteShiftRestEndpoint.java`)
* Service / orchestration
  * `SchedulerController#createSchedule` and `#addConcreteShift` assemble data (doctors, holidays, priorities, constraints) and invoke `ScheduleBuilder`. (`src/main/java/org/cswteams/ms3/control/scheduler/SchedulerController.java`)
* Core algorithm
  * `ScheduleBuilder#build` iterates over generated `ConcreteShift` instances and allocates doctors based on constraints and seniority requirements. (`src/main/java/org/cswteams/ms3/control/scheduler/ScheduleBuilder.java`)
* Constraint validators
  * Constraint classes under `src/main/java/org/cswteams/ms3/entity/constraint/` enforce hard/soft rules. They access doctor attributes via `ContextConstraint` and `DoctorUffaPriority`.

**Data flow into the scheduler**

The scheduler loads data at runtime (multi-tenant schema) via DAOs:

* `doctorDAO.findAll()` supplies all `Doctor` entities. (`SchedulerController#createSchedule`)
* `doctorUffaPriorityDAO.findAll()` supplies the per-doctor priority state. (`SchedulerController#createSchedule`)
* `doctorUffaPrioritySnapshotDAO.findAll()` supplies snapshot values used during generation/regeneration. (`SchedulerController#createSchedule`)
* `doctorHolidaysDAO.findAll()` supplies holiday associations used by holiday constraints. (`SchedulerController#createSchedule`)
* `holidayDAO.findAll()` supplies holiday definitions. (`SchedulerController#createSchedule`)
* `constraintDAO.findAll()` supplies constraint definitions. (`SchedulerController#createSchedule`)

The `ScheduleBuilder` builds assignments by:

* Creating `ConcreteShift` entries from `Shift` definitions.
* Using `QuantityShiftSeniority` in each `Shift` to drive how many doctors of each `Seniority` are required per task/time slot.
* Evaluating constraints against `ContextConstraint` for each candidate doctor.

## 2. Doctor Data Model Inventory

**Core entities / tables**

| Layer | Entity / Table | Notes / Relationships |
| --- | --- | --- |
| JPA | `Doctor` extends `TenantUser` | `Doctor` adds `seniority` + links to conditions, preferences, and specializations. (`src/main/java/org/cswteams/ms3/entity/Doctor.java`, `TenantUser.java`) |
| DB | `doctor` | Doctor table contains base identity fields + `seniority`. (`src/main/resources/db/tenant/tables/create_doctor_tables.sql`) |
| DB | `ms3_tenant_user` | Base user table (also used by non-doctors). (`src/main/resources/db/tenant/tables/create_tenant_user_tables.sql`) |

**Doctor-linked entities**

| Purpose | Entity / Table | Relationship to Doctor |
| --- | --- | --- |
| Conditions (permanent) | `PermanentCondition` / `permanent_condition` | Many-to-many via `doctor_permanent_conditions`. (`Doctor.java`, `create_other_doctor_tables.sql`) |
| Conditions (temporary) | `TemporaryCondition` / `temporary_condition` | Many-to-many via `doctor_temporary_conditions`. (`Doctor.java`, `create_other_doctor_tables.sql`) |
| Preferences | `Preference` / `preference` | Many-to-many via `preference_doctors`. (`Preference.java`, `create_preference_tables.sql`) |
| Specializations | `Specialization` / `specialization` | Many-to-many via `doctor_specializations`. (`Specialization.java`, `create_specialization_table.sql`) |
| Holiday history | `DoctorHolidays` / `doctor_holidays` | One-to-one mapping with `holiday_map` blob. (`DoctorHolidays.java`, `create_doctor_tables.sql`) |
| Scheduling priorities | `DoctorUffaPriority` / `doctor_uffa_priority` | One-to-one per doctor; used during generation. (`DoctorUffaPriority.java`, `create_other_doctor_tables.sql`) |
| Priority snapshot | `DoctorUffaPrioritySnapshot` / `doctor_uffa_priority_snapshot` | One-to-one per doctor; used for regenerate. (`DoctorUffaPrioritySnapshot.java`, `create_other_doctor_tables.sql`) |

**DTOs / REST payloads involving doctor attributes**

| DTO | Fields (doctor attributes only) | Usage |
| --- | --- | --- |
| `MedicalDoctorInfoDTO` | `id`, `name`, `lastname`, `seniority`, `task` | Returned by `/api/doctors` and used in schedule views. (`src/main/java/org/cswteams/ms3/dto/medicalDoctor/MedicalDoctorInfoDTO.java`) |
| `SingleUserProfileDTO` | `name`, `lastname`, `email`, `birthday`, `seniority`, `specializations`, `systemActors`, `permanentConditions`, `temporaryConditions` | Profile and update views. (`src/main/java/org/cswteams/ms3/dto/userprofile/SingleUserProfileDTO.java`) |
| `UserCreationDTO` | `name`, `lastname`, `birthday`, `taxCode`, `email`, `password`, `systemActors`, `seniority` | Used in user registration. (`src/main/java/org/cswteams/ms3/dto/user/UserCreationDTO.java`) |
| `PreferenceDTOIn`/`PreferenceDTOOut` | `day`, `month`, `year`, `turnKinds` | Preference input/output. (`src/main/java/org/cswteams/ms3/dto/preferences/PreferenceDTOIn.java`) |
| `UpdateConditionsDTO` | `doctorID`, `condition` (label + date bounds) | Adds permanent/temporary conditions to doctors. (`src/main/java/org/cswteams/ms3/dto/condition/UpdateConditionsDTO.java`) |
| `DoctorSpecializationDTO` | `doctorID`, `specializations` | Adds specializations. (`src/main/java/org/cswteams/ms3/dto/specialization/DoctorSpecializationDTO.java`) |

**Frontend representations**

* `frontend/src/entity/Doctor.js` models `id`, `name`, `lastname`, `seniority`, and `task` used by UI selection lists.
* UI forms read/write `seniority`, `specializations`, and `conditions` through profile and configurator screens (e.g., `ModifyUserProfileView`, `SingleUserProfileView`, `RegistraUtenteView`).

## 3. Attribute Catalog (Authoritative)

> **Legend**
> * **Scheduling impact**: “Yes” means directly used by scheduling, constraints, or scocciatura. “Indirect” means used by validation or other flows that affect whether a doctor can be scheduled, but not by the core assignment algorithm. “No” means the attribute is not referenced in scheduling logic as-is.
> * **Layer names**: JPA field / DB column / JSON name.

### 3.1 Core identity (Doctor / TenantUser)

| Attribute | Layer names | Type | Allowed values / format | Nullability / defaults | Validation | Scheduling impact |
| --- | --- | --- | --- | --- | --- | --- |
| Doctor ID | `Doctor.id` / `doctor.ms3_tenant_user_id` / `id` | long | DB generated | Not null | JPA `@Id` | **Yes** (used in assignments, Uffa priorities, lookups) |
| Name | `TenantUser.name` / `doctor.name` / `name` | string | Free text | Not null | `@NotNull` | No (identity only) |
| Lastname | `TenantUser.lastname` / `doctor.lastname` / `lastname` | string | Free text | Not null | `@NotNull` | No (identity only) |
| Birthday | `TenantUser.birthday` / `doctor.birthday` / `birthday` | date | `yyyy-MM-dd` | Not null | `@NotNull` | Indirect (used for demographics, not scheduling logic) |
| Tax code | `TenantUser.taxCode` / `doctor.tax_code` / `taxCode` | string | Italian “Codice Fiscale” | Not null | `@NotNull` | No |
| Email | `TenantUser.email` / `doctor.email` / `email` | string | Email format | Not null | `@Email`, `@NotNull` | No |
| Password | `TenantUser.password` / `doctor.password` / `password` | string | hashed string | Not null | `@NotNull` | No |
| System roles | `TenantUser.systemActors` / `tenantuser_systemactors.role` / `systemActors` | enum set | `DOCTOR`, `PLANNER`, `CONFIGURATOR` | Not null | `@Enumerated` | Indirect (access control; not used by scheduler which queries `Doctor` table directly) |

### 3.2 Scheduling-critical attributes

| Attribute | Layer names | Type | Allowed values / format | Nullability / defaults | Validation | Scheduling impact |
| --- | --- | --- | --- | --- | --- | --- |
| Seniority | `Doctor.seniority` / `doctor.seniority` / `seniority` | enum | `STRUCTURED`, `SPECIALIST_JUNIOR`, `SPECIALIST_SENIOR` | Not null | `@NotNull` | **Yes**. Used to match required counts in `QuantityShiftSeniority` and constraints (`ConstraintNumeroDiRuoloTurno`). |
| Permanent conditions | `Doctor.permanentConditions` / `doctor_permanent_conditions` | list of `PermanentCondition` | types in `condition.type` | Not null list | JPA ManyToMany | **Yes**. `ConstraintMaxPeriodoConsecutivo` checks condition types; e.g., “OVER 62”. |
| Temporary conditions | `Doctor.temporaryConditions` / `doctor_temporary_conditions` | list of `TemporaryCondition` | types in `condition.type` with start/end epoch days | Not null list | JPA ManyToMany | **Yes (indirect)**. Constraint configuration supports temporary conditions (e.g., “INCINTA”) and used to create `ConstraintMaxPeriodoConsecutivo` per condition. Current constraint check only scans *permanent* conditions, so temporary conditions are not enforced by `ConstraintMaxPeriodoConsecutivo` as-is. |
| Preferences | `Doctor.preferenceList` / `doctor_preference_list` | list of `Preference` | date + time slots | Not null list | JPA ManyToMany | **Yes (soft)**. Used by scocciatura: scheduling weight for assigning on requested-off days (`ScocciaturaDesiderata`). |
| Holiday history | `DoctorHolidays.holidayMap` / `doctor_holidays.holiday_map` | map of `Holiday`→boolean | serialized (LOB) | Not null | `@NotNull` | **Yes**. `ConstraintHoliday` checks if a doctor previously worked a holiday. Missing entry can trigger NPE if `DoctorHolidays` is absent. |
| Uffa priority state | `DoctorUffaPriority` / `doctor_uffa_priority` | integers | priorities and partial priorities | Not null (defaults to 0) | JPA fields default 0 | **Yes**. Used by scocciatura weighting and by `ScheduleBuilder` to select candidates and update priority queues. |
| Uffa priority snapshot | `DoctorUffaPrioritySnapshot` / `doctor_uffa_priority_snapshot` | integers | priorities | Not null (defaults to 0) | JPA fields default 0 | **Yes** for schedule regeneration (restore priorities). |

### 3.3 Optional / currently unused for scheduling

| Attribute | Layer names | Type | Allowed values / format | Nullability / defaults | Validation | Scheduling impact |
| --- | --- | --- | --- | --- | --- | --- |
| Specializations | `Doctor.specializations` / `doctor_specializations` | list of `Specialization` | `Specialization.type` string | Not null list | JPA ManyToMany | **No (as-is)**. `ScheduleBuilder` has a TODO for specialization checks, but none implemented. |
| Max week schedulable hours | `Doctor.maxWeekSchedulableHours` | int | default `-1` | transient | none | **No (as-is)**. Not stored in DB; not referenced in scheduling logic. |

## 4. Enums and Controlled Vocabularies

**Backend enums**

| Enum | Members | Used for |
| --- | --- | --- |
| `Seniority` | `STRUCTURED`, `SPECIALIST_JUNIOR`, `SPECIALIST_SENIOR` | Doctor seniority. (`src/main/java/org/cswteams/ms3/enums/Seniority.java`) |
| `SystemActor` | `DOCTOR`, `PLANNER`, `CONFIGURATOR` | User roles. (`src/main/java/org/cswteams/ms3/enums/SystemActor.java`) |
| `TimeSlot` | `MORNING`, `AFTERNOON`, `NIGHT` | Preference time slots and shift categories. (`src/main/java/org/cswteams/ms3/enums/TimeSlot.java`) |
| `ConcreteShiftDoctorStatus` | `ON_DUTY`, `ON_CALL`, `REMOVED` | Assignment status. (`src/main/java/org/cswteams/ms3/enums/ConcreteShiftDoctorStatus.java`) |

**Condition type values (seeded in `ApplicationStartup`)**

* Permanent: `OVER 62`
* Temporary: `INCINTA`, `IN MATERNITA'`, `IN FERIE`, `IN MALATTIA`

These are stored as `condition.type` strings and referenced by configuration logic (`ConfigVincMaxPerCons`). (`src/main/java/org/cswteams/ms3/config/ApplicationStartup.java`)

**Specializations (seeded in `ApplicationStartup`)**

* `CARDIOLOGIA`
* `ONCOLOGIA`

**Preference time slots**

Preference input uses strings constrained to `MORNING`, `AFTERNOON`, `NIGHT` with validation annotations (`AdmissibleValues`, `@NotEmpty`). (`PreferenceDTOIn`)

## 5. Scheduling-Relevant Constraints Matrix

| Constraint / Rule | Doctor attributes referenced | Implementation | Hard / Soft | Failure mode |
| --- | --- | --- | --- | --- |
| Max consecutive minutes | `Doctor.permanentConditions` (condition type), current shifts | `ConstraintMaxPeriodoConsecutivo` | Hard | Throws `ViolatedVincoloAssegnazioneTurnoTurnoException` and blocks assignment. |
| Max minutes per period | Schedule history (per doctor) | `ConstraintMaxOrePeriodo` | Hard | Throws `ViolatedVincoloAssegnazioneTurnoTurnoException`. |
| Non-overlapping shifts | Schedule history (per doctor) | `ConstraintUbiquita` | Hard | Throws `ViolatedVincoloAssegnazioneTurnoTurnoException`. |
| Contiguous shift exclusion | Schedule history + time slot | `ConstraintTurniContigui` | Soft (violable true by default) | Throws `ViolatedVincoloAssegnazioneTurnoTurnoException` (but can be forced). |
| Holiday exclusion | `DoctorHolidays.holidayMap` | `ConstraintHoliday` | Soft (violable true by default) | Throws `ViolatedConstraintHolidayException`. |
| Seniority distribution | `Doctor.seniority` | `ConstraintNumeroDiRuoloTurno` | Hard (if enabled) | Throws `ViolatedVincoloRuoloNumeroException`. |
| Preference penalties | `Doctor.preferenceList` | `ScocciaturaDesiderata` (via `ControllerScocciatura`) | Soft (priority impact) | Changes UFFA priority weighting. |

**Note**: `ConstraintNumeroDiRuoloTurno` is currently not added to the default constraints in `ApplicationStartup` (commented out) but is implemented and can be enabled. (`ApplicationStartup#registerConstraints`, `ConstraintNumeroDiRuoloTurno`)

## 6. Minimum Viable Doctor Record (Scheduling-Safe)

To include a doctor in schedule generation without runtime errors, the following must exist for each doctor:

1. **Doctor row** with mandatory identity fields + `seniority`.
2. **DoctorUffaPriority row** linked to the doctor (with priority fields set, defaults to 0).
3. **DoctorUffaPrioritySnapshot row** linked to the doctor (defaults to 0).
4. **DoctorHolidays row** with non-null `holiday_map` (even if empty) to avoid NPE in `ConstraintHoliday`.
5. **SystemActor roles** (includes `DOCTOR`) to be consistent with UI and access control.
6. **Referenced seed data** in the tenant schema:
   * Conditions referenced by configuration (e.g., `OVER 62`, `INCINTA`) exist in `condition` table.
   * Holidays exist if `ConstraintHoliday` is enabled (it is enabled by default).
   * Shifts with `QuantityShiftSeniority` exist, otherwise no concrete shifts will be generated.

If any of the rows in (2)–(4) are missing, `ScheduleBuilder` or constraint checks can crash (e.g., `ConstraintHoliday` assumes `doctorHolidays` is non-null and has a `holidayMap`).

## 7. Implications for Synthetic Population Generation

**Attributes that must vary to exercise scheduling logic**

* **Seniority**: algorithm uses seniority counts per shift. Include a mix of `STRUCTURED`, `SPECIALIST_JUNIOR`, `SPECIALIST_SENIOR` or shifts will fail to allocate required counts.
* **Conditions**: to cover constraint variants, include at least:
  * A doctor with permanent condition `OVER 62`.
  * A doctor with temporary condition `INCINTA` (note: current constraint code only checks permanent conditions for max consecutive minutes).
* **Holiday history**: `DoctorHolidays.holidayMap` should include true/false mappings for relevant holidays if holiday constraints are enabled.
* **Preferences**: add a subset with `Preference` entries to cover scocciatura weighting and “undesired day” penalties.

**Attributes that can be fixed (unless UI flows are tested)**

* Identity fields (`name`, `lastname`, `taxCode`, `email`, `password`, `birthday`) are not used by the scheduler, but must be present per schema constraints.
* Specializations are not evaluated by scheduler as-is; a constant value is acceptable unless UI/role workflows rely on them.
* System actor roles can be a fixed set containing `DOCTOR` unless testing planner/configurator views.

**Distribution hints implied by code/config**

* Default constraint config (`configVincoliDefault.properties`) enforces:
  * Period length: 7 days
  * Max minutes per period: 20 hours
  * Night shift horizon: 4 hours
  * Max consecutive minutes: 16 hours for everyone; 6 hours for `OVER 62` and `INCINTA` (in minutes).
* If doctor pool lacks required seniority counts for a shift/task, `ScheduleBuilder` throws `NotEnoughFeasibleUsersException` and the schedule can be marked illegal.

## 8. Open Questions / Ambiguities

1. **Temporary conditions enforcement**: `ConstraintMaxPeriodoConsecutivo` checks only permanent conditions; temporary conditions are seeded but not consulted. Should temporary conditions (e.g., pregnancy) be considered in scheduling constraints?
2. **DoctorHolidays generation**: `holiday_map` is a serialized map; no explicit format is documented. Should a synthetic dataset include all holidays with boolean flags, or is an empty map acceptable if the holiday constraint is soft?
3. **Doctor table vs. tenant user table**: The schema includes both `doctor` and `ms3_tenant_user` tables with overlapping fields, while JPA uses table-per-class inheritance. Which table is authoritative for doctor identity in production data?
4. **Specialization usage**: `ScheduleBuilder` includes a TODO for specialization checks. If scheduling should consider specialization, what are the intended rules?
5. **QuantityShiftSeniority correctness**: Shift correctness relies on tasks in medical services; what minimum shift/service/task configuration is expected for synthetic data generation?

