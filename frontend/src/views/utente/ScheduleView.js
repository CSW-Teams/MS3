/* eslint jsx-a11y/anchor-is-valid: 0 */

import React from 'react';
import {ViewState} from '@devexpress/dx-react-scheduler';
import {AllDayPanel} from '@devexpress/dx-react-scheduler-material-ui';
import { ServizioAPI } from '../../API/ServizioAPI';
import Stack from '@mui/material/Stack';
import {AppointmentContent, Content} from "../../components/common/CustomAppointmentComponents.js"
import Collapse from '@mui/material/Collapse';
import {
  Button,
  Paper,
} from "@mui/material";
import {
  Scheduler,
  Resources,
  Appointments,
  AppointmentTooltip,
  DayView,
  MonthView,
  DateNavigator,
  TodayButton,
  Toolbar,
  ViewSwitcher,
  WeekView, CurrentTimeIndicator,
  AppointmentForm,
} from '@devexpress/dx-react-scheduler-material-ui';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import {
   EditingState,IntegratedEditing
} from '@devexpress/dx-react-scheduler';
import { ServiceFilterSelectorButton } from '../../components/common/ServiceFilterSelectorButton';
import { UtenteAPI } from '../../API/UtenteAPI';
import TextField from '@mui/material/TextField';
import Autocomplete from '@mui/material/Autocomplete';
import { HolidaysAPI } from '../../API/HolidaysAPI';
import { AssegnazioneTurnoAPI } from '../../API/AssegnazioneTurnoAPI';
import { BasicLayout, Nullcomponent, Overlay, OverlaySingle, SingleLayout } from '../../components/common/AssegnazioneTurnoModificaComponent';
import ButtonLegalSchedulation from '../../components/common/ButtonLegalSchedulation';
import { ShiftPrinterCSV } from "../../components/common/ShiftPrinterCSV";
import {
  RichiestaRimozioneDaTurnoAPI
} from "../../API/RichiestaRimozioneDaTurnoAPI";


/**
 * Componente utilizzato per visualizzare i messaggi di errori della violazione dei vincoli
 * quando si modifica un assegnazione turno
 * @param {} props
 * @returns
 */
function ViolationLog(props){

  return (
    <div>
        <ul>
          {props.log.map((msg) => <li> {msg} </li>)}
        </ul>
    </div>
  );
}

const CustomTooltipHeader = ({...restProps }) => (
  <div {...restProps} style={{backgroundColor: "black"}}>
    Personalizzato
  </div>
);



/**
 * This view defines a generic shift schedule view.
 * Children are expected to query shifts from their preferred backend API
 * before the render() method is fired, i.e. overriding the componentDidMount() method
 *
 */
class ScheduleView extends React.Component{

    constructor(props) {
        super(props);
        this.state = {
          attore : localStorage.getItem("attore"),
            data: [],   // list of shifts to display in schedule (not filtered yet)
            mainResourceName: 'utenti_guardia',
            resources: [
              {fieldName: 'utenti_guardia', title: 'Guardia', allowMultiple: true,instances: [{}]},
              {fieldName: 'utenti_reperibili', title: 'Reperibilità',allowMultiple: true, instances: [{}]},
            ],
            /**
             * Filter criteria are the attributes used by filters to choose if a shift can be displayed.
             */
            filterCriteria: {

              /** what services we want to display? (default: all) */
              services: new Set(),
              users: [],

              // add more filter criteria here ...
            },
            /** all services registered in the system */
            allServices: new Set(),
            allUser : [],
            appointmentContentComponent : AppointmentContent,
            openOptionFilter: false,

            /** Holidays to display */
            holidays: [],

            /**
             * Com'è andata l'ultima volta che abbiamo chiesto i turni al backend?
             * "GOOD" --> I turni sono stati caricati con successo
             * "BAD" --> C'è stato un errore nel caricamento dei turni
             * "ABOUT_TO_ASK" --> Non abbiamo ancora chiesto i turni al backend, ma lo faremo appena possibile
             */
            shiftQueriedResponse: "ABOUT_TO_ASK",
            idUser: localStorage.getItem("id"),
            justification: "",
            outcome: false,
            idShift: 0,
            requests: []
          };
          /**
           * All filtering functions.
           * Each filter function must take a shift as input
           * and return true if the shift is feasible according to the filter conditions confronting
           * them to the filterCriteria specified in state.
           * We can adopt the following name convention to make the code more readable:
           * @function filterBy\<MyCriterion\>(shift) -> boolean
           */
          this.filters = [

            function filterByServices(shift){
              let services = this.state.filterCriteria.services;
              return services.size === 0 || services.has(shift.servizio);
            }.bind(this),

            function filterByUsers(shift){
              let users = this.state.filterCriteria.users;

              for (let i = 0; i < shift.utenti_guardia.length; i++)
                for (let j = 0; j < users.length; j++)
                  if(users[j].id === shift.utenti_guardia[i].id)
                    return true;

              return users.length === 0
            }.bind(this),

            // add more filters here ...
          ];
          this.changeMainResource = this.changeMainResource.bind(this);
          this.componentDidMount= this.componentDidMount.bind(this);
          this.updateFilterCriteria = this.updateFilterCriteria.bind(this);
          this.commitChanges = this.commitChanges.bind(this);
    }

    changeMainResource(mainResourceName) {
      this.setState({ mainResourceName });
    }

    pendingRetirementRequestForShiftExist = async (idShift) => {
      //console.log(idUser, this.state.requests.some(request => request.id === idShift));
      //return this.state.requests.some(request => request.id === idShift);

      let idUser = -1;

      for (let i = 0; i < this.state.requests.length; i++) {
        if (this.state.requests[i].id === idShift) {
          idUser = this.state.requests[i].idUser;
          break;
        }
      }

      if (idUser !== -1) {
        let api = new UtenteAPI();
        const userDetails = await api.getUserDetails(idUser);
        let name = userDetails.nome;
        let surname = userDetails.cognome;
        return `${name} ${surname}`
      }

      return idUser;

    }

    handleRetirement = async (justification, idShift) => {
      this.state.justification = justification;
      this.state.idShift = idShift;

      const subState = {
        assegnazioneTurnoId: this.state.idShift,
        utenteId: this.state.idUser,
        descrizione: this.state.justification,
        esito: this.state.outcome
      }

      let richiestaRimozioneDaTurnoAPI = new RichiestaRimozioneDaTurnoAPI();
      let httpResponse = await richiestaRimozioneDaTurnoAPI.postRequest(subState);

      console.log(httpResponse);  // todo remove

      if (httpResponse.status === 202) {
        toast.success('Richiesta inoltrata con successo', {
          position: "top-center",
          autoClose: 5000,
          hideProgressBar: true,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
          theme: "colored",
        });
      } else {
        toast.error('Non è stato possibile inoltrare la richiesta', {
          position: "top-center",
          autoClose: 5000,
          hideProgressBar: true,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
          theme: "colored",
        });
      }
    }

    /**
     * Questa funzione verrà invocata nel momento in cui il pianificatore effettua una modifica su una assegnazione turno.
     * Le modifiche effettuate su una assegnazione turno già esistente verranno inviate al backend.
     * @param {*} param0
     */
    async commitChanges({ added, changed, deleted }) {

      let assegnazioneTurnoApi = new AssegnazioneTurnoAPI();

      if(changed){
        let { data} = this.state;
        let appointmentChanged;

        /**
        * Il campo changed contiene l'id dell'assegnazione appena modificata e le modifiche apportate.
        * Un esempio changed = {idAssegnazione: {utenti_guardia:[...], utenti_reperibili: [...]}
        * Poichè l'id dell'assegnazione è espresso come numero e non è referenziato da una stringa
        * sono costretto a scorrere tutte le assegnazioni turni per verificare quell'id a quale asseganzione turno corrisponde
        */
        for( let i=0; i < data.length ; i++){
          if(changed[data[i].id])
            appointmentChanged=data[i]
        }


        let response = await assegnazioneTurnoApi.aggiornaAssegnazioneTurno(appointmentChanged,changed[appointmentChanged.id],localStorage.getItem("id"));
        let responseStatusClass = Math.floor(response.status / 100)

        if(responseStatusClass==5){

          toast.error('Errore nel server!', {
            position: "top-center",
            autoClose: 5000,
            hideProgressBar: true,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
            theme: "colored",
          });
        }

        else if(responseStatusClass!= 2){

          let responseBody = await response.json();

          toast.error(ViolationLog({log : responseBody.messagges}), {
            position: "top-center",
            hideProgressBar: false,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
            theme: "colored",
            autoClose: false,
          });

        }else{

          toast.success('Assegnazione modificata con successo', {
            position: "top-center",
            autoClose: 5000,
            hideProgressBar: true,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
            theme: "colored",
          });

          let turni = await assegnazioneTurnoApi.getGlobalTurn();

          this.setState({data:turni});
          this.forceUpdate();
        }

      } else if(deleted){

        let response = await assegnazioneTurnoApi.eliminaAssegnazioneTurno(deleted);
        let responseStatusClass = Math.floor(response.status / 100);

        if(responseStatusClass!=2){

          toast.error('Non è stato possibile eliminare l\'assegnazione selezionata!!', {
            position: "top-center",
            autoClose: 5000,
            hideProgressBar: true,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
            theme: "colored",
          });
        }

        else{

          toast.success("Eliminazione avvenuta con successo", {
            position: "top-center",
            hideProgressBar: false,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
            theme: "colored",
            autoClose: false,
          });

          let turni = await assegnazioneTurnoApi.getGlobalTurn();

          this.setState({data:turni});
          this.forceUpdate();

        }


      }

    }

    /**
     * This function is passed as a callback to the filter selectors components, which
     * can use it to change the filter criteria in ScheduleView state.
     * It merely consists of a decorator adding a call to forceUpdate() to the updateLogic function
     * defined by the filter selector.
     * the updateLogic() function must take the filterCriteria object as argument and change its properties values.
     * An example: function updateLogic(filterCriteria) { filterCriteria.myAttribute = myValue; }
     */
    updateFilterCriteria(updateLogic){
      updateLogic(this.state.filterCriteria);
      this.forceUpdate();
    }


    async componentDidMount(turni, utenti) {

      let api = new RichiestaRimozioneDaTurnoAPI();
      let requestsArray = await api.getAllPendingRequests();

      let allServices = await new ServizioAPI().getService();
      let allUser = await new UtenteAPI().getAllUsersInfo();
      let allHolidays = await new HolidaysAPI().getHolidays();

      this.setState(
        {
          requests: requestsArray,
          data: turni,
          mainResourceName: 'utenti_guardia_id',
          resources:
            [
              {
                fieldName: 'utenti_guardia_id',
                title: 'Guardia',
                allowMultiple: true,
                instances: utenti,
              }
              , {
              fieldName: 'utenti_reperibili_id',
              title: 'Reperibilità',
              allowMultiple: true,
              instances: utenti,
            },
            ],
          allServices: new Set(allServices),
          allUser: allUser,
          holidays: allHolidays,
          shiftQueriedResponse: "GOOD",
        })

    }


    render(view){

      console.log("Richieste:    ",this.state.requests)


      // add shifts to the schedulables to display
        let { data, resources} = this.state;

        /** Filtering of shifts is performed by ANDing results of all filter functions applied on each shift */
        data = data.filter((shift) => {
          return this.filters.reduce(
            (isFeasible, currentFilter) => isFeasible && currentFilter(shift, this.state.filterCriteria),
            true
          );
        });

        /**
         * Prepariamo un messaggio diverso per il link al download del csv con i turni
         * in base a se tali turni sono disponibili.
         */
        let shifts = data.slice();
        let textLink = "";
        switch(this.state.shiftQueriedResponse){
          case "GOOD":
            textLink="Scarica questi "+shifts.length+" turni come file CSV ⬇️"
            break;
          case "BAD":
            textLink="Non è stato possibile caricare i turni dal backend. Riprova più tardi o contattare un tecnico. ❌"
            break;
          case "ABOUT_TO_ASK":
            textLink="Caricamento... ⏳"
            break;
          default:
            // this should never appear
            textLink="Unexpected shiftQueriedResponse value: "+this.state.shiftQueriedResponse + "🫠"
            break;
        }

        // add holidays to the schedulables to display
        data.push(...this.state.holidays);

        return (
          <React.Fragment>
            <Paper>
              {/**
               * Al click, scarica la pianificazione visualizzata in formato CSV, ma solo se siamo riusciti a
               * caricare i turni dal backend.
               */}
               <ShiftPrinterCSV rawShifts={shifts} shiftsChanged={true} textLink={textLink} enable={this.state.shiftQueriedResponse === "GOOD"}></ShiftPrinterCSV>
              <Collapse in={this.state.openOptionFilter}>
                <Stack spacing={1} style={{
                      display: 'flex',
                      'padding-top': '10px',
                      justifyContent: 'center',
                      'align-items': 'center'
                    }}>

                  <Autocomplete
                    onChange={(event, value) => {
                      this.updateFilterCriteria(()=>this.state.filterCriteria.users= value)
                      }}
                    multiple
                    options={this.state.allUser}
                    sx={{ width: 300 }}
                    renderInput={(params) => <TextField {...params} label="Medici di guardia" />}
                  />
                  {/** Service Filter selectors */}
                  <div style={{display : 'flex','justify-content': 'space-between','column-gap': '20px'}}>
                    {Array.from(this.state.allServices).map(
                      (service, i) => (
                        <ServiceFilterSelectorButton key={i} criterion={service} updateFilterCriteriaCallback={this.updateFilterCriteria}/>
                      ))}
                  </div>

                </Stack>
              </Collapse>

              <Button
                onClick={() => {
                  this.setState({openOptionFilter: !this.state.openOptionFilter});
                }}
                style={{
                  'display': 'block',
                  'margin-left': 'auto',
                  'margin-right': 'auto',
                  'margin-top':'1%',
                  'margin-bottom':'-1%'
                }}
              >
                {this.state.openOptionFilter?"Chiudi":"Filtra"}
              </Button>

              <ButtonLegalSchedulation ></ButtonLegalSchedulation>

              <Scheduler
                locale={"it-IT"}
                firstDayOfWeek={1}
                data={data}
              >
                <ViewState/>
                <WeekView
                  displayName="Settimanale"
                  startDayHour={0}
                  endDayHour={24}
                  cellDuration={60}
                />
                <DayView
                  displayName="Giornaliero"
                  startDayHour={0}
                  endDayHour={24}
                  cellDuration={60}
                />
                <MonthView displayName="Mensile" />
                <Toolbar/>


                <EditingState onCommitChanges={this.commitChanges}/>
                <IntegratedEditing/>
                <Appointments
                  appointmentContentComponent={(props) => (
                    <AppointmentContent attore={this.state.attore} {...props} />
                  )}
                />
                <AllDayPanel/>
                <Resources
                  data={resources}
                />
                <DateNavigator />

                <TodayButton  buttonComponent={(props) => {
                  return (
                    <Button onClick={() => props.setCurrentDate(new Date())}>
                      Oggi
                    </Button>

                  );
                }}/>
                <ViewSwitcher />


                {view==="global" && this.state.attore==="PIANIFICATORE" &&
                  //Visualizzo il bottone per eliminare un assegnazione solo se sono sulla schermata globale
                 //SOLO IL PIANIFICATORE PUO' MODIFICARE I TURNI
                  <AppointmentTooltip
                    header
                    showCloseButton
                    showOpenButton
                    showDeleteButton
                    contentComponent={(props) => (
                      <Content {...props} view={view} actor={this.state.attore} checkRequests={this.pendingRetirementRequestForShiftExist} />
                    )}
                  />
                }

                {view === "global" && this.state.attore !== "PIANIFICATORE" &&
                < AppointmentTooltip
                  contentComponent={(props) => (
                    <Content {...props} view={view} actor={this.state.attore} />
                  )}
                />
                }

                {view!=="global" &&
                  //Se sono sulla schermata "singola" non visualizzo il bottone per eliminare l'assegnazione turno
                  <AppointmentTooltip
                    showCloseButton
                    showOpenButton
                    contentComponent={(props) => (
                      <Content {...props} view={view} onRetirement={this.handleRetirement} actor={this.state.attore} />
                    )}
                  />
                }

                <CurrentTimeIndicator
                  shadePreviousAppointments={true}
                  shadePreviousCells={true}
                  updateInterval={60000}
                />

                {view=="global" && this.state.attore!=="UTENTE" ?
                  <AppointmentForm
                    overlayComponent = {Overlay}
                    textEditorComponent={Nullcomponent}
                    labelComponent={Nullcomponent}
                    booleanEditorComponent={Nullcomponent}
                    dateEditorComponent ={Nullcomponent}
                    basicLayoutComponent={BasicLayout}
                  />
                  :
                  <AppointmentForm
                    overlayComponent = {OverlaySingle}
                    textEditorComponent={Nullcomponent}
                    labelComponent={Nullcomponent}
                    booleanEditorComponent={Nullcomponent}
                    dateEditorComponent ={Nullcomponent}
                    basicLayoutComponent={SingleLayout.bind(this)}
                    readOnly
                />
                }



              </Scheduler>

            </Paper>
          </React.Fragment>

        );

    }

}

export default ScheduleView;
