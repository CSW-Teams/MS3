# Results of tests
## Sprint 0

### ScheduleBuilder
- Refactor? of the class RuoliNumero to take trace of how many "SPECIALIZZANDI" there are in a shift
- AssegnazioneTurno.getUtenti mismatch tra list e set
- [BUG] Add constraints over date selection of the starting of the schedule
- [BUG] We can build a schedule with an empty user list, but with non empty shifts list (incoherency with the actual state of the shift)
- [BUG] No check on adding violated constraints

In total we have the 40,15% of test passing (102/254)

### ControllerGiustificaForzatura
- [BUG] We can add a forced constraint of a turn that doesn't exist (Check on date insertion)

In total we have the 79,17% of test passing (19/24)

### ControllerVincolo
- It has been noticed that a null constraint has been added into the DB: it is sufficent to sanitize the DB

In total we have the 100% of test passing (1/1)

### ControllerDesiderata
- [BUG] If we add a desiderata for a user who shares email and password with other users, the system crashes. So, there is an unhandled exception plus there are multiple users with the same email in ApplicationStartup (which does not make sense)

In total we have the 83,33% of test passing (5/6)

## Sprint 1
### ScheduleBuilder
- Refactor? of the class RuoliNumero to take trace of how many "SPECIALIZZANDI" there are in a shift
- AssegnazioneTurno.getUtenti mismatch tra list e set
- [FIXED] [BUG] Add constraints over date selection of the starting of the schedule
- [BUG] We can build a schedule with an empty user list, but with non empty shifts list (incoherency with the actual state of the shift)
- [FIXED] [BUG] No check on adding violated constraints

In total we have the 94,44% of test passing (204/216)

### ControllerGiustificaForzatura
- [BUG] We can add a forced constraint of a turn that doesn't exist (Check on date insertion)

In total we have the --,--% of test passing (--/24)