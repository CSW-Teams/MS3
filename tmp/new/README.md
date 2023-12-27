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

  //istanziazione e salvataggio nel DB degli oggetti di tipo Scocciatura (propongo di ridenominare Scocciatura in Scocciatura e così via)
  for(Holiday holiday: holidays) {
    Scocciatura scocciaturaHolidayMorning = new HolidayScocciatura(uffaPriorityHoliday, holiday, TimeSlot.MORNING);
    Scocciatura scocciaturaHolidayAfternoon = new HolidayScocciatura(uffaPriorityHoliday, holiday, TimeSlot.AFTERNOON);
    Scocciatura scocciaturaHolidayNight = new HolidayScocciatura(uffaPriorityHolidayNight, holiday, TimeSlot.NIGHT);

    scocciaturaDAO.save(scocciaturaHolidayMorning);
    scocciaturaDAO.save(scocciaturaHolidayAfternoon);
    scocciaturaDAO.save(scocciaturaHolidayNight);

  }

}
```

## ApplicationStartup.populateDB()
* Ridefinire la classe DoctorScheduleState (che rinominerei DoctorUffaPriority) che lega ciascun Doctor a tre code di priorità separate: la prima è quella generale, la seconda è quella legata ai soli turni lunghi (con durata > 6h), mentre la terza è quella legata ai soli turni notturni. In particolare, la prima coda va sfruttata per le assegnazioni dei turni brevi mattutini e pomeridiani, la seconda coda va sfruttata per le assegnazioni dei turni lunghi mattutini e pomeridiani, mentre la terza coda va sfruttata per le assegnazioni dei turni notturni:
```
@Entity
@Getter
public class DoctorUffaPriority {  //propongo di rimuovere gli attributi schedule e assegnazioniTurnoCache.

  @Id
  @GeneratedValue
  private Long id;

  /** Doctor which the Uffa priority refers to */
  @OneToOne
  @NotNull
  private Doctor doctor;

  private int partialGeneralPriority = 0;
  private int generalPriority = 0;
  private int partialLongShiftPriority = 0;
  private int longShiftPriority = 0;
  private int partialNightPriority = 0;
  private int nightPriority = 0;

  protected DoctorUffaPriority() {}

  public DoctorUffaPriority(Doctor doctor) {
    this.doctor = doctor; 

  }

  public void updatePriority(PriorityQueue pq) {  //è la controparte di saveUffaTemp().

    switch(pq) {
      case GENERAL:
        this.generalPriority = this.partialGeneralPriority;

      case LONG_SHIFT:
        this.longShiftPriority = this.partialLongShiftPriority;

      case NIGHT:
        this.nightPriority = this.partialNightPriority;

    }

  }

  public void updatePartialPriority(int priorityDelta, PriorityQueue pq) {  //è la controparte di addUffaTemp().

    switch(pq) {
      case GENERAL:
        this.partialGeneralPriority = this.generalPriority + priorityDelta;

      case LONG_SHIFT:
        this.partialLongShiftPriority = this.longShiftPriority + priorityDelta;

      case NIGHT:
        this.partialNightPriority = this.nightPriority + priorityDelta;

    }

  }

}
```

* È chiaro che vada definita una nuova enum per indicare a quale coda di priorità facciamo riferimento:
```
public enum PriorityQueue {
  GENERAL,
  LONG_SHIFT,
  NIGHT,
}
```

* Definire una classe entity che lega ciascun Doctor con l'informazione su se lo scorso anno ha svolto il turno in ciascuna festività:
```
@Entity
@Getter
public class DoctorHolidays {

  @Id
  @GeneratedValue
  private Long id;

  /** Doctor which the information refers to */
  @OneToOne
  @NotNull
  private Doctor doctor;

  @NotNull
  Map<Holiday, boolean> holidayMap;

  protected DoctorHolidays() {}

  public DoctorHolidays(Doctor doctor) {
    this.doctor = doctor;
    this.holidayMap = new HashMap<>();

    List<Holiday> holidays = registerHolidays();
    for(Holiday holiday: holidays) {
      this.holidayMap.put(holiday, false);  //si assume che, al momento dell'istanziazione dell'entità, il corrispettivo medico non abbia svolto alcun turno in passato.
  
    }
  
  }

}
```

* Istanziare e registrare nel DB gli oggetti DoctorUffaPriority e DoctorHolidays per tutti i dottori:
```
private void populateDB() throws TurnoException {
  [...]
  //creazione e registrazione degli utenti nel DB
  List<Doctor> allDoctors = doctorDAO.findAll();

  for(Doctor doctor: allDoctors) {
    DoctorUffaPriority dup = new DoctorUffaPriority(doctor);
    DoctorHolidays dh = new DoctorHolidays(doctor);

    doctorUffaPriorityDao.save(dup);
    doctorHolidaysDao.save(dh);

  }

  [...]

}
```

## ApplicationStartup.registerConstraints():
* Definire un nuovo vincolo (violabile): un medico che nell'anno precedente ha lavorato durante la festività X non potrebbe lavorare durante la medesima festività anche quest'anno:
```
@Entity
public class ConstraintHoliday extends Constraint {  //verifyConstraint() == verificaVincolo(); ContextConstraint == contextVincolo

  @Override
  public void verifyConstraint(ContextConstraint context) throws ViolatedConstraintException {
    Doctor doctor = context.getDoctorUffaPriority().getDoctor();
    DoctorHolidays dh = doctorHolidaysDAO.findByDoctor(doctor);
    ConcreteShift concreteShift = context.getConcreteShift();
    List<Holiday> holidays = holidayController.readHolidays();

    for(Holiday holiday: holidays) {
      if(concreteShift.getDataEpochDay() >= holiday.getStartDateEpochDay() && concreteShift.getDataEpochDay() <= holiday.getEndDateEpochDay()) {  //is turno festivo?
        for(Holiday holidayDh: dh.getHolidayMap().keySet()) {
          if(holiday.getName().isEquals(holidayDh.getName()) && dh.getHolidayMap().get(holidayDh))
            throw new ViolatedVincoloHolidayException(concreteShift, doctor);
  
        }

      }

    }

  }

}
```

* Definire quindi una nuova classe eccezione per questo nuovo vincolo:
```
public class ViolatedConstraintHolidayException extends ViolatedConstraintException {

  public ViolatedConstraintHolidayException(ConcreteShift concreteShift, Doctor doctor) {
    super(String.format("Il dottor %s %s non rispetta il vincolo sulle vacanze" +
      " per il turno %s. La violazione riguarda il giorno %s.",
      doctor.getNome(), doctor.getCognome(), concreteShift.getShift().getTimeSlot(),
      ConvertitoreData.daStandardVersoTestuale(Instant.ofEpochMilli(concreteShift.getDate()).atZone(ZoneId.systemDefault()).toLocalDate())));

  }

}
```

* Istanziare e registrare nel DB il nuovo vincolo sulle festività:
```
private void registerConstraints() {
  [...]
  //istanziazione dei vincoli
  Constraint constraint7 = new ConstraintHoliday();
  constraint7.setViolable(true);  //l'attributo violabile di Constraint deve essere tradotto in violable.

  //definizione della descrizione dei vincoli
  constraint7.setDescription("Vincolo festività. Verifica che un medico che l'anno precedente ha lavorato durante una certa festività non venga assegnato a un turno corrispondente alla medesima festività.");  //l'attributo descrizione di Constraint deve essere tradotto in description.

  //memorizzazione dei vincoli nel DB
  constraintDAO.saveAndFlush(constraint7);

  List<Constraint> constraints = constraintDAO.findByType("ConstraintMaxPeriodoConsecutivo");

}
```

## ScheduleBuilder.aggiungiUtenti():
* Migliorare l'algoritmo di schedulazione in modo tale che si tenga conto dei livelli di priorità e, nello specifico, delle scocciature dovute ai turni lunghi e ai turni di notte e delle scocciature dovute alle festività:
```
private void addDoctors(ConcreteShift concreteShift, QuantityShiftSeniority qss, List<Doctor> newDoctors, List<DoctorUffaPriority> allDoctorUffaPriority) throws NotEnoughFeasibledoctorsException {

  int selectedUsers = 0;

  if(controllerScocciatura != null) {
    controllerScocciatura.updatePriorityDoctors(allDoctorUffaPriority, concreteShift, PriorityQueue.GENERAL);
    controllerScocciatura.orderByPriority(allDoctorUffaPriority, PriorityQueue.GENERAL);

    if(concreteShift.getShift().getDuration().toMinutes() > 360) {  //360 minuti == 6 ore
      controllerScocciatura.updatePriorityDoctors(allDoctorUffaPriority, concreteShift, PriorityQueue.LONG_SHIFT);
      controllerScocciatura.orderByPriority(allDoctorUffaPriority, PriorityQueue.LONG_SHIFT);

    }

    if(concreteShift.getShift().getTimeSlot() == TimeSlot.NIGHT) {
      controllerScocciatura.updatePriorityDoctors(allDoctorUffaPriority, concreteShift, PriorityQueue.NIGHT);
      controllerScocciatura.orderByPriority(allDoctorUffaPriority, PriorityQueue.NIGHT);

    }

  }

  for(DoctorUffaPriority dup: allDoctorUffaPriority) {
    [...]

    if(contextDoctorsOnDuty.size() < qss.getQuantity()) {  //bisogna modificare solo la coda di priorità adeguata.

      if(concreteShift.getShift().getTimeSlot() == TimeSlot.NIGHT)
        doctorScheduleState.updatePriority(PriorityQueue.NIGHT);
      elif(concreteShift.getShift().getDuration().toMinutes() > 360)
        doctorScheduleState.updatePriority(PriorityQueue.LONG_SHIFT);
      else
        doctorScheduleState.updatePriority(PriorityQueue.GENERAL);

    }
    selectedUsers++;

  } 

  [...]

}
```

* Per far ciò, è necessario ritoccare anche il metodo ScheduleBuilder.build()...
```
public Schedule build() {
  schedule.purify();

  List<DoctorUffaPriority> allDoctorUffaPriority = daoDoctorUffaPriority.findAll();
  //TODO: normalizzazione dei livelli di priorità

  for(ConcreteShift at: this.schedule.getAssegnazioniTurno()) {
    Month currentMonth = LocalDate.ofEpochDay(at.getDataEpochDay()).getMonth()

    for(DoctorUffaPriority dup: allDoctorUffaPriority) {  //clean-up delle informazioni sugli 8 mesi più 'vecchi'
      for(Month month: Month.values()) {
        if(ChronoUnit.MONTHS.between(month, currentMonth) > 3)
          dup.getTotalUffas().put(month, 0);

      }

    }

    try {  //prima pensiamo a riempire le allocazioni, che sono le più importanti
      for(RuoloNumero rn: at.getTurno().getRuoliNumero()) {
        this.aggiungiUtenti(at, rn.getNumero(), at.getUtentiDiGuardia(), allDoctorUffaPriority);
      }
    } catch(NotEnoughFeasibledoctorsException e) {
      [...]
    }

    try {  //passo poi a riempire le riserve
      for(RuoloNumero rn: at.getTurno().getRuoliNumero()) {
        this.aggiungiUtenti(at, rn.getNumero(), at.getUtentiReperibili(), allDoctorUffaPriority);
      }
    } catch(NotEnoughFeasibledoctorsException e) {
      [...]
    }

  }

  for(DoctorUffaPriority dup: allDoctorUffaPriority) {
    daoDoctorUffaPriority.save(dup);
  }

  return this.schedule;

}
```

* ... e il metodo controllerScocciatura.updatePriorityDoctors() (che dovrebbe sostituire controllerScocciatura.addUffaTempUtenti())...
```
public void updatePriorityDoctors(List<DoctorUffaPriority> allDoctorUffaPriority, ConcreteShift concreteShift, PriorityQueue pq) {

  int priorityDelta;
  ContestoScocciatura contestoScocciatura;

  for(DoctorUffaPriority dup: allDoctorUffaPriority) {
    contestoScocciatura = new ContestoScocciatura(dup, concreteShift);
    priorityDelta = this.calcolaUffaComplessivoUtenteAssegnazione(contestoScocciatura);
    dup.updatePartialPriority(priorityDelta, pq);

  }

}
```

* ... e il metodo controllerScocciatura.orderByPriority() (che dovrebbe sostituire controllerScocciatura.ordinaByUffa()):
```
public void orderByPriority(List<DoctorUffaPriority> dup, PriorityQueue pq) {
  Collections.shuffle(dup);

  switch(pq) {
    case GENERAL:
      dup.sort((u1, u2) -> u1.getPartialGeneralPriority() - u2.getPartialGeneralPriority());

    case LONG_SHIFT:
      dup.sort((u1, u2) -> u1.getPartialLongShiftPriority() - u2.getPartialLongShiftPriority());

    case NIGHT:
      dup.sort((u1, u2) -> u1.getPartialNightPriority() - u2.getPartialNightPriority());

  }

}
```
