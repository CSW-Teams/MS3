# Results of tests
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
