package org.cswteams.ms3.control.scheduler;

import java.time.LocalDate;
import java.util.*;

import org.cswteams.ms3.control.scocciatura.ControllerScocciatura;
import org.cswteams.ms3.control.utils.MappaSchedulo;
import org.cswteams.ms3.control.utils.MappaUtenti;
import org.cswteams.ms3.dao.*;
import org.cswteams.ms3.dto.ModificaAssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.RegistraAssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.ScheduloDTO;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.entity.doctor.Doctor;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
import org.cswteams.ms3.exception.IllegalScheduleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;


@Service
public class ControllerScheduler implements IControllerScheduler{

    @Autowired
    private UtenteDao utenteDao;

    @Autowired
    private TurnoDao turnoDao;

    @Autowired
    private ScheduleDao scheduleDao;

    @Autowired
    private VincoloDao vincoloDao;

    @Autowired
    private AssegnazioneTurnoDao assegnazioneTurnoDao;

    @Autowired
    private ScocciaturaDao scocciaturaDao;



    private ScheduleBuilder scheduleBuilder;


    /**
     * Permette la creazione di un nuovo schedulo specificando data inizio e data fine della generazione.
     * @param startDate giorno di inizio della validità della pianificazione
     * @param endDate giorno di fine (compreso) della validità della pianificazione
     * @return An instance of schedule, if correctly created and saved in persistence, null otherwise
     */
    @Override
    public Schedule createSchedule(@NotNull LocalDate startDate, @NotNull LocalDate endDate)  {

        //Verifico se esiste già uno schedulo per i giorni che voglio pianificare
        if(!check(startDate,endDate))
            return null;

        //Data che uso per scorre l'intervallo di giorni
        LocalDate currentDay = startDate;
        List<ConcreteShift> allAssegnazioni = new ArrayList<>();

        // creo assegnazioni associando una data a ogni turno.
        //Scorro tutti i giorni dell'intervallo. Per ogni giorno scorro tutti i turni.
        while(!currentDay.isAfter(endDate)){
            for(Shift shift : turnoDao.findAll()){
                
                // Possiamo assegnare questo shift a questo giorno solo se il giorno
                // della settimana è previsto tra quelli ammissibili del shift
                if (shift.getGiorniDiValidità().isDayOfWeekIncluded(currentDay.getDayOfWeek())){
                    allAssegnazioni.add(new ConcreteShift(currentDay, shift));
                }
                
            }

            //Mi sposto al giorno seguente.
            currentDay = currentDay.plusDays(1);
        }

        //Creo uno schedule builder ad ogni nuova pianificazione
        try {
            this.scheduleBuilder = new ScheduleBuilder(
                startDate,  // data inizio pianificazione
                endDate,    // data fine pianificazione
                vincoloDao.findAll(),    // tutti i vincoli da rispettare quando si assegna una persona a un turno
                allAssegnazioni,    // assegnazioni di turno con data (senza partecipanti)
                utenteDao.findAll() // tutti i candidati da allocare ai turni
                );

            this.scheduleBuilder.setControllerScocciatura(new ControllerScocciatura(scocciaturaDao.findAll()));
            // Setto il controller che gestisce gli uffa points
            return  scheduleDao.save(this.scheduleBuilder.build());

        } catch (IllegalScheduleException e) {
            return null;
        }
    }

    /**
     * Permette la rigenerazione di una schedulazione. Non &egrave; possibile rigenerare schedulazioni passate. Solo schedulazioni future.
     * @param id An existing schedule ID
     * @return Boolean that represents if the regeneration ended succesfully
     */
    @Override
    public boolean rigeneraSchedule(long id) {
        Optional<Schedule> optionalSchedule = scheduleDao.findById(id);
        if(optionalSchedule.isEmpty())
            return false;


        Schedule schedule = optionalSchedule.get();
        LocalDate startDate = schedule.getStartDate();
        LocalDate endDate = schedule.getEndDate();

        //Non è possibile eliminare una vecchia schedulazione.
        if(!rimuoviSchedulo(id))
            return false;

        createSchedule(startDate,endDate);
        return true;
    }

    /**
     * Permette di aggiungere una nuova assegnazione ad uno schedulo gia esistente. Il controller andrà a cercare
     * lo schedulo contenente il giorno della nuova assegnazione e lo passerà al builder.
     * Se forced è true l'assegnazione verrà aggiunta solo se vengono rispetatti i vincoli non violabili.
     * Se forced è false l'assegnazione verà aggiunta se vengono rispettati tutti i vincoli.
     */
    public Schedule aggiungiAssegnazioneTurno(@NotNull ConcreteShift concreteShift, boolean forced) throws IllegalScheduleException {

        Schedule schedule;
        
        //creo un nuovo builder passandogli uno schedulo già esistente
        this.scheduleBuilder = new ScheduleBuilder(
                vincoloDao.findAll(), // tutti i vincoli da rispettare quando si assegna una persona a un turno
                utenteDao.findAll(),  // tutti i candidati da allocare ai turni
                scheduleDao.findByDateBetween(concreteShift.getDataEpochDay()) //Schedulo gia esistente
        );

        schedule = this.scheduleBuilder.addAssegnazioneTurno(concreteShift,forced);
        
        // we commit changes to schedule only if they do not taint it
        if (!schedule.isIllegal()){
            scheduleDao.flush();
        }
        
        return schedule;
    }

    /**
     * Rimuove un assegnazione turno solo dallo schedulo ma non dal database.
     * @param concreteShiftOld
     */
    public void rimuoviAssegnazioneTurnoSchedulo(@NotNull ConcreteShift concreteShiftOld) {
        Schedule schedule = scheduleDao.findByDateBetween(concreteShiftOld.getDataEpochDay());
        schedule.getAssegnazioniTurno().remove(concreteShiftOld);
        scheduleDao.flush();
    }

    @Override
    public boolean rimuoviAssegnazioneTurno(@NotNull Long idAssegnazione) {
        Optional<ConcreteShift> assegnazioneTurno = assegnazioneTurnoDao.findById(idAssegnazione);
        if(assegnazioneTurno.isEmpty())
            return false;

        this.rimuoviAssegnazioneTurnoSchedulo(assegnazioneTurno.get());
        assegnazioneTurnoDao.delete(assegnazioneTurno.get());
        return true;
    }

    @Override
    public Schedule aggiungiAssegnazioneTurno(@NotNull RegistraAssegnazioneTurnoDTO assegnazione, boolean forced) throws AssegnazioneTurnoException, IllegalScheduleException {
        // Per convertire il dto in un entità ho bisogno di un shift che dovrebbe essere
        // presente nel database
        List<Shift> turni = turnoDao.findAllByServizioNomeAndTipologiaTurno(assegnazione.getServizio().getNome(), assegnazione.getTipologiaTurno());
        if(turni.isEmpty())
            throw new AssegnazioneTurnoException("Non esiste un shift con la coppia di attributi servizio: "+assegnazione.getServizio().getNome() +",tipologia shift: "+assegnazione.getTipologiaTurno().toString());
        Shift shift = null;
        for(Shift turnodb: turni){
            if(turnodb.getMansione().equals(assegnazione.getMansione())){
                shift = turnodb;
                break;
            }
        }
        if(shift == null){
            throw new AssegnazioneTurnoException("Non esiste un shift con la coppia di attributi servizio: "+assegnazione.getServizio().getNome() +",mansione: "+assegnazione.getMansione().toString());
        }
        ConcreteShift concreteShift = new ConcreteShift(
                LocalDate.of(assegnazione.getAnno(), assegnazione.getMese(), assegnazione.getGiorno()),
                shift,
                MappaUtenti.utenteDTOtoEntity(assegnazione.getUtentiReperibili()),
                MappaUtenti.utenteDTOtoEntity(assegnazione.getUtentiDiGuardia()));


        if(!checkAssegnazioneTurno(concreteShift)){
            throw new AssegnazioneTurnoException("Collisione tra utenti reperibili e di guardia");
        }

        // Converto il dto in un entità

        return this.aggiungiAssegnazioneTurno(concreteShift,forced);

    }

    private boolean checkAssegnazioneTurno(@NotNull ConcreteShift turno) {

        for(Doctor doctor1 : turno.getUtentiDiGuardia()){
            for(Doctor doctor2 : turno.getUtentiReperibili()){
                if (doctor1.getId().longValue() == doctor2.getId().longValue()){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Questo metodo modifica un assegnazione turno già esistente. Il suo compito è quello di eliminare
     * la vecchia assegnazione turno, verificare che la nuova assegnazione turno modificata rispetti tuitti  i
     * vincoli e in caso di successo salvarla nel database. Se qualche vincolo invece è violato la vecchia assegnazione
     * che era stata eliminata viene salvata nuovamente nel database.
     * @param modificaAssegnazioneTurnoDTO
     * @return
     */
    @Override
    @Transactional
    public Schedule modificaAssegnazioneTurno(@NotNull ModificaAssegnazioneTurnoDTO modificaAssegnazioneTurnoDTO) throws IllegalScheduleException {

        ConcreteShift concreteShiftOld = assegnazioneTurnoDao.findById(modificaAssegnazioneTurnoDTO.getIdAssegnazione()).get();
        ConcreteShift concreteShiftNew = concreteShiftOld.clone();

        List<Doctor> allUsersOld = concreteShiftOld.getUtentiAsList();

        //Apporto le modifiche sugli utenti allocati , se necessario
        if(modificaAssegnazioneTurnoDTO.getUtenti_guardia()!= null){
            concreteShiftNew.setUtentiDiGuardia(new HashSet<>());
            for (long idGuardia: modificaAssegnazioneTurnoDTO.getUtenti_guardia()) {
                concreteShiftNew.addUtentediGuardia(utenteDao.findById(idGuardia));
            }
        }

        //Apporto le modifiche sugli utenti di riserva , se necessario
        if(modificaAssegnazioneTurnoDTO.getUtenti_reperibili()!=null){
            concreteShiftNew.setUtentiReperibili(new HashSet<>());
            for (long idReperibile: modificaAssegnazioneTurnoDTO.getUtenti_reperibili()) {
                concreteShiftNew.addUtenteReperibile(utenteDao.findById(idReperibile));
            }
        }

        /**
         * registriamo gli utenti allocati nella vecchia assegnazione turno che non sono
         * presenti nella nuova asegnazione turno come utenti rimossi, oltre a quelli che
         * gia erano segnati come rimossi nella vecchia assegnazione turno
         */
        concreteShiftNew.setRetiredDoctors(new HashSet<>());
        for (Doctor doctor : allUsersOld) {
            if (!concreteShiftNew.isAllocated(doctor) && !concreteShiftNew.isReserve(doctor)){
                concreteShiftNew.getRetiredDoctors().add(doctor);
            }
        }
        concreteShiftNew.getRetiredDoctors().addAll(concreteShiftOld.getRetiredDoctors());
        
        //rimuovo la vecchia assegnazione e provo ad aggiungere la nuova
        this.rimuoviAssegnazioneTurnoSchedulo(concreteShiftOld);
        Schedule schedule = this.aggiungiAssegnazioneTurno(concreteShiftNew, true);

        // Se un vincolo è violato riaggiungo l'assegnazione che avevo in precedenza eliminato
        if (schedule.isIllegal()) {
            schedule.getAssegnazioniTurno().add(concreteShiftOld);
            scheduleDao.flush();
        }
        else{
            //Rimuovo la vecchia assegnazione turno anche dal database
            this.rimuoviAssegnazioneTurno(concreteShiftOld.getId());
        }

        return schedule;
    }

    public List<ScheduloDTO> leggiSchedulazioni(){
        return MappaSchedulo.scheduloEntitytoDTO(scheduleDao.findAll());
    }

    @Override
    public List<ScheduloDTO> leggiSchedulazioniIllegali() {
        return MappaSchedulo.scheduloEntitytoDTO(scheduleDao.leggiSchedulazioniIllegali());
    }

    /**
     * Permette la rimozione di uno schedulo. La rimozione può essere eseguita correttamente solo sulle generazioni future
     * e non quelle passate.
     * @param id
     * @return
     */
    public boolean rimuoviSchedulo(long id){

        Optional<Schedule> scheduleOptional = scheduleDao.findById(id);

        if(scheduleOptional.isEmpty())
            return false;

        //Verifico se lo schedulo che voglio eliminare è uno schedulo futuro e non passato
        if(scheduleOptional.get().getEndDate().isBefore(LocalDate.now()))
            return false;

        scheduleDao.deleteById(id);

        return true;

    }


    /**
     * Verifica se esiste già uno schedulo esistente per i giorni che vogliamo pianificare
     * @param startNewSchedule
     * @return
     */
    public boolean check(@NotNull LocalDate startNewSchedule, @NotNull LocalDate endNewSchedule){
        List<Schedule> allSchedule = scheduleDao.findAll();

        for (Schedule schedule : allSchedule) {
            if (!schedule.getStartDate().isAfter(endNewSchedule) && !schedule.getEndDate().isBefore(startNewSchedule))
                return false;
        }

        return true;

    }
}
