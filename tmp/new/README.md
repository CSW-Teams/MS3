# Modifiche da apportare
## ApplicationStartup.registerScocciature()
* Inserire la registrazione delle festività all'interno del sistema:
```
private List<Holiday> registerHolidays() {
  CalendarSetting setting = new CalendarSetting("https://date.nager.at/api/v3/PublicHolidays");
  List<Holiday> holidays = holidayController.readHolidays();  //holidayController e calendarServiceManager sono due controller da annotare come @Autowired.
  
  if(holidays.isEmpty()) {
    setting.setYear(String.valueOf(LocalDate.now().getYear()));
    setting.setCountry("IT");
    calendarServiceManager.init(setting);
  
    try {
      holidays = calendarServiceManager.getHolidays();
    } catch (CalendarServiceException e) {
      e.printStackTrace();
    }
    holidayController.registerHoliday(holidays);

  }
  return holidays;

}
```

* Inserire le scocciature per le festività e convertire tutte le scocciature da Uffa point a variazioni di livelli di priorità:
```
private void registerScocciature() {  //propongo di rinominare il metodo in registerAnnoyances().
  List<Holiday> holidays = registerHolidays();

  int uffaPriorityPreference = 20;    //propongo 40 livelli di priorità totali: da 0 a 39 dove la base è 0. Il livello 0 è il più prioritario e viceversa.

  int uffaPrioritySundayAfternoon = 4;
  int uffaPrioritySundayMorning = 4;
  int uffaPrioritySaturdayNight = 4;

  int uffaPrioritySaturdayAfternoon = 3;
  int uffaPrioritySaturdayMorning = 3;
  int uffaPriorityFridayNight = 3;
  int uffaPrioritySundayNight = 3;

  int uffaPriorityFridayAfternoon = 2;

  int uffaPrioritySimple = 1;
  int uffaPriorityNight = 2;

  int uffaPriorityHoliday = 5;
  int uffaPriorityHolidayNight = 6;

  //istanziazione e salvataggio nel DB degli oggetti di tipo Scocciatura (propongo di ridenominare Scocciatura in Annoyance e così via)
  for(Holiday holiday: holidays) {
    Annoyance annoyanceHolidayMorning = new HolidayAnnoyance(uffaPriorityHoliday, holiday, TimeSlot.MORNING);
    Annoyance annoyanceHolidayAfternoon = new HolidayAnnoyance(uffaPriorityHoliday, holiday, TimeSlot.AFTERNOON);
    Annoyance annoyanceHolidayNight = new HolidayAnnoyance(uffaPriorityHolidayNight, holiday, TimeSlot.NIGHT);

    annoyanceDao.save(annoyanceHolidayMorning);
    annoyanceDao.save(annoyanceHolidayAfternoon);
    annoyanceDao.save(annoyanceHolidayNight);

  }

}
```

## ApplicationStartup.populateDB()
* Ridefinire la classe UserScheduleState (che rinominerei DoctorUffaPriority) che lega ciascun utente a tre code di priorità separate: la prima è quella generale, la seconda è quella legata ai soli turni lunghi (con durata > 6h), mentre la terza è quella legata ai soli turni notturni. In particolare, la prima coda va sfruttata per le assegnazioni dei turni brevi mattutini e pomeridiani, la seconda coda va sfruttata per le assegnazioni dei turni lunghi mattutini e pomeridiani, mentre la terza coda va sfruttata per le assegnazioni dei turni notturni:
```
@Entity
@Getter
@Setter
public class DoctorUffaPriority {

  @Id
  @NotNull
  private Long userId;

  @NotNull
  private Map<Month, int> totalUffas;

  @NotNull
  private Map<Month, int> nightUffas;

  @NotNull
  private int generalPriority;

  @NotNull
  private int nightPriority;

  protected DoctorUffaPriority() {}

  public DoctorUffaPriority(long id) {
    this.userId = id;
    this.totalUffas = new HashMap<>();
    this.nightUffas = new HashMap<>();
    this.generalPriority = 0;  //si assume che le code di priorità abbiano 0 come priorità intermedia (e, quindi, MIN<0 && MAX>0).
    this.nightPriority = 0;

    for(Month month: Month.values()) {
      this.totalUffas.put(month, 0);
      this.nightUffas.put(month, 0);
    
    }    

  }

  public void updateUffa(int uffa, boolean isNight, Month currentMonth) {
    int oldUffa;

    for(Month month: Month.values()) {
      if(ChronoUnit.MONTHS.between(month, currentMonth) == 0) {

        if(isNight) {
          oldUffa = this.nightUffas.get(month);
          this.nightUffas.put(month, oldUffa+uffa);

        } else {
          oldUffa = this.totalUffas.get(month);
          this.totalUffas.put(month, oldUffa+uffa);

        }
 
      }

    }

  }

  private int computeWeightedUffa(int uffa, long monthsDistance) {

    switch(monthsDistance) {
      case 0:
        return uffa;

      case 1:
        return uffa*ALPHA;

      case 2:
        return uffa*BETA;

      case 3:
        return uffa*GAMMA;

      default:
        return 0;

    }

  }

  public void updatePriority(int uffa, boolean isNight, Month currentMonth) {
    int weightedUffas = uffa;

    if(isNight) {
       for(Month month: this.nightUffas.keySet()) {
          weightedUffas += computeWeightedUffa(this.nightUffas.get(month), ChronoUnit.MONTHS.between(month, currentMonth));
        }
        this.nightPriority = FUNC(weightedUffas);  //FUNC() è da stabilire bene almeno insieme a Staccone.

    } else {
      for(Month month: this.totaltUffas.keySet()) {
          weightedUffas += computeWeightedUffa(this.totalUffas.get(month), ChronoUnit.MONTHS.between(month, currentMonth));
        }
        this.generalPriority = FUNC(weightedUffas);  //FUNC() è da stabilire bene almeno insieme a Staccone.

    }

  }

}
```

* Definire una classe entity che lega ciascun utente con l'informazione su se lo scorso anno ha svolto il turno in ciascuna festività:
```
@Getter
@Setter
@EqualsAndHashCode
@JsonIgnorePoperties({"hibernateLazyInitializer", "handler"})
public class UserHolidays {

  @Id
  @NotNull
  private Long userId;

  @NotNull
  Map<Holiday, boolean> holidayMap;

  protected UserHolidays() {}

  public UserHolidays(long id) {
    this.userId = id;
    this.holidayMap = new HashMap<>();

    List<Holiday> holidays = registerHolidays();
    for(Holiday holiday: holidays) {
      this.holidayMap.put(holiday, false);  //si assume che, al momento dell'istanziazione dell'entità, il corrispettivo medico non abbia svolto alcun turno in passato.
  
    }
  
  }

}
```

* Istanziare e registrare nel DB gli oggetti UserUffaPriority e UserHolidays per tutti gli utenti:
```
private void populateDB() throws TurnoException {
  [...]
  //creazione e registrazione degli utenti nel DB
  List<Utente> allUsers = utenteDao.findAll();

  for(Utente user: allUsers) {
    UserUffaPriority uup = new UserUffaPriority(user.getId());
    UserHolidays uh = new UserHolidays(user.getId());

    daoUserUffaPriorityDao.save(uup);
    daoUserHolidaysDao.save(uh);

  }

  [...]

}
```

## ApplicationStartup.registerConstraints():
* Definire un nuovo vincolo (violabile): un medico che nell'anno precedente ha lavorato durante la festività X non potrebbe lavorare durante la medesima festività anche quest'anno:
```
@Entity
public class vincoloHoliday extends Vincolo {

  @Override
  public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {
    Utente user = contesto.getUserScheduleState().getUtente();
    UserHolidays uh = daoUserHolidays.findById(user.getId());
    AssegnazioneTurno at = contesto.getAssegnazioneTurno();
    List<Holiday> holidays = holidayController.readHolidays();

    for(Holiday holiday: holidays) {
      if(at.getDataEpochDay() >= holiday.getStartDateEpochDay() && at.getDataEpochDay() <= holiday.getEndDateEpochDay()) {  //is turno festivo?
        for(Holiday holidayUh: uh.getHolidayMap().keySet()) {
          if(holiday.getName().isEquals(holidayUh.getName()) && uh.getHolidayMap().get(holidayUh))
            throw new ViolatedVincoloHolidayException(at, user);
  
        }

      }

    }

  }

}
```

* Definire quindi una nuova classe eccezione per questo nuovo vincolo:
```
public class ViolatedVincoloHolidayException extends ViolatedConstraintException {

  public ViolatedVincoloHolidayException(AssegnazioneTurno assegnazione, Utente user) {
    super(String.format("L'utente %s %s non rispetta il vincolo sulle vacanze" +
      " per il turno %s in %s. La violazione riguarda il giorno %s.",
      user.getNome(), user.getCognome(), assegnazione.getTurno().getTipologiaTurno(),
      assegnazione.getTurno().getServizio().getNome(), convertitoreData.daStandardVersoTestuale(assegnazione.getData().toString())));

  }

}
```

* Istanziare e registrare nel DB il nuovo vincolo sulle festività:
```
private void registerConstraints() {
  [...]
  //istanziazione dei vincoli
  Vincolo vincolo7 = new VincoloHoliday();
  vincolo7.setViolabile(true);

  //definizione della descrizione dei vincoli
  vincolo7.setDescrizione("Vincolo festività. Verifica che un medico che l'anno precedente ha lavorato durante una certa festività non venga assegnato a un turno corrispondente alla medesima festività.");

  //memorizzazione dei vincoli nel DB
  vincoloDao.saveAndFlush(vincolo7);

  List<Vincolo> vincoli = vincoloDao.findByType("VincoloMaxPeriodoConsecutivo");

}
```

## ScheduleBuilder.aggiungiUtenti():
* Migliorare l'algoritmo di schedulazione (e in particolare la gestione degli Uffa point) in modo tale che si tenga conto degli Uffa point accumulati nei vari mesi (dove i mesi più vicini hanno un peso maggiore e viceversa) e, nello specifico, delle scocciature dovute ai turni di notte e delle scocciature dovute alle festività:
```
private void aggiungiUtenti(AssegnazioneTurno assegnazione, int numUtenti, Set<Utente> utentiDaPopolare, List<UserUffaPriority> allUserUffaPriority) throws NotEnoughFeasibleUsersException {

  int selectedUsers = 0;
  List<UserScheduleState> allUserScheduleState = new ArrayList<>(allUserScheduleStates.values());

  if(controllerScocciatura != null) {
    controllerScocciatura.updatePriorityUsers(allUserScheduleState, assegnazione, allUserUffaPriority, false);
    controllerScocciatura.orderByPriority(allUserUffaPriority, false);  //TipologiaTurno.NOTTURNO: false

    if(assegnazione.getTurno().getTipologiaTurno() == TipologiaTurno.NOTTURNO) {
      controllerScocciatura.updatePriorityUsers(allUserScheduleState, assegnazione, allUserUffaPriority, true);
      controllerScocciatura.orderByPriority(allUserUffaPriority, true);  //TipologiaTurno.NOTTURNO: true

    }

  }

  for(UserUffaPriority uup: allUserUffaPriority) {
    UserScheduleState userScheduleState;  //ricerca del prossimo UserScheduleState che rispetta l'ordine dato dalle priorità
    for(UserScheduleState potentialUserScheduleState: allUserScheduleState) {
      if(potentialUserScheduleState.getUtente().getId() == uup.getUserId())
        userScheduleState = potentialUserScheduleState;

    }

    [...]

    if(contesto.getAssegnazioneTurno().getTurno().isReperibilitaAttiva() || contesto.getAssegnazioneTurno().getUtentiDiGuardia().size() < contesto.getAssegnazioneTurno().getTurno().getNumRequiredUsers()) {

      userScheduleState.saveUffaTemp();

      for(UserUffaPriority uup: allUserUffaPriority) {  //bisogna modificare solo la lista di uffa point dell'utente corretto.
        if(uup.getUserId() == userScheduleState.getUtente().getId())
          uup.updateUffa(userScheduleState.getUffaCumulativo(), assegnazione.getTurno().getTipologiaTurno()==TipologiaTurno.NOTTURNO, LocalDate.ofEpochDay(assegnazione.getDataEpochDay()).getMonth());

      }
      selectedUsers++;

    } 

  }

  [...]

}
```

* Per far ciò, è necessario ritoccare anche il metodo ScheduleBuilder.build()...
```
public Schedule build() {
  schedule.purify();

  List<UserUffaPriority> allUserUffaPriority = daoUserUffaPriority.findAll();

  for(AssegnazioneTurno at: this.schedule.getAssegnazioniTurno()) {
    Month currentMonth = LocalDate.ofEpochDay(at.getDataEpochDay()).getMonth()

    for(UserUffaPriority uup: allUserUffaPriority) {  //clean-up delle informazioni sugli 8 mesi più 'vecchi'
      for(Month month: Month.values()) {
        if(ChronoUnit.MONTHS.between(month, currentMonth) > 3)
          uup.getTotalUffas().put(month, 0);

      }

    }

    try {  //prima pensiamo a riempire le allocazioni, che sono le più importanti
      for(RuoloNumero rn: at.getTurno().getRuoliNumero()) {
        this.aggiungiUtenti(at, rn.getNumero(), at.getUtentiDiGuardia(), allUserUffaPriority);
      }
    } catch(NotEnoughFeasibleUsersException e) {
      [...]
    }

    try {  //passo poi a riempire le riserve
      for(RuoloNumero rn: at.getTurno().getRuoliNumero()) {
        this.aggiungiUtenti(at, rn.getNumero(), at.getUtentiReperibili(), allUserUffaPriority);
      }
    } catch(NotEnoughFeasibleUsersException e) {
      [...]
    }

  }

  for(UserUffaPriority uup: allUserUffaPriority) {
    daoUserUffaPriority.save(uup);
  }

  return this.schedule;

}
```

* ... e il metodo controllerScocciatura.updatePriorityUsers() (che dovrebbe sostituire controllerScocciatura.addUffaTempUtenti())...
```
public void updatePriorityUsers(List<UserScheduleState> allUserState, AssegnazioneTurno assegnazione, List<UserUffaPriority> allUserUffaPriority, boolean isNight) {

  int uffa;
  ContestoScocciatura contestoScocciatura;

  for(UserScheduleState userScheduleState: allUserState){
    contestoScocciatura = new ContestoScocciatura(userScheduleState, assegnazione);
    uffa = this.calcolaUffaComplessivoUtenteAssegnazione(contestoScocciatura);
    userScheduleState.addUffaTemp(uffa);

    for(UserUffaPriority uup: allUserUffaPriority) {  //bisogna modificare solo la priorità dell'utente corretto.
      if(uup.getUserId() == userScheduleState.getUtente().getId())
        uup.updatePriority(uffa, isNight, LocalDate.ofEpochDay(assegnazione.getDataEpochDay()).getMonth());  //TipologiaTurno.NOTTURNO: isNight

    }

  }

}
```

* ... e il metodo controllerScocciatura.orderByPriority() (che dovrebbe sostituire controllerScocciatura.ordinaByUffa()):
```
public void orderByPriority(List<UserUffaPriority> uup, boolean isNight) {
  Collections.shuffle(uup);
  if(isNight)
    uup.sort((u1, u2) -> u1.getNightPriority() - u2.getNightPriority());
  else
    uup.sort((u1, u2) -> u1.getGeneralPriority() - u2.getGeneralPriority());

}
```

## Possibili miglioramenti rispetto alle modifiche proposte
* Accorpare la classe UserScheduleState con la classe UserUffaPriority (poi dipende anche dalle modifiche apportate da Staccone).
