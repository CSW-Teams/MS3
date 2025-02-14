import React from 'react';
import Drawer from '@material-ui/core/Drawer';
import BasicDatePicker from './DataPicker';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Autocomplete from '@mui/material/Autocomplete';
import Button from '@mui/material/Button';
import {AssegnazioneTurnoAPI} from '../../API/AssegnazioneTurnoAPI';
import {DoctorAPI} from '../../API/DoctorAPI';
import {ServiceAPI} from '../../API/ServiceAPI';
import {TurnoAPI} from '../../API/TurnoAPI';
import Switch from '@mui/material/Switch';
import FormControlLabel from '@mui/material/FormControlLabel';
import {toast} from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import InformationDialogs from './InformationVincoloComponent';
import {
  MDBCard,
  MDBCardBody,
  MDBCardTitle,
  MDBTextArea
} from "mdb-react-ui-kit";
import FilesUpload from './FilesUpload'
import {GiustificaForzaturaAPI} from "../../API/GiustificaForzaturaAPI";
import {t} from "i18next";
import {panic} from "./Panic";

function ViolationLog(props) {

  return (
    <div>
      <ul>
        {props.log.map((msg) => <li> {msg} </li>)}
      </ul>
    </div>
  );
}

export default function TemporaryDrawer(props) {

  const [user, setUser] = React.useState([])
  const [data, setdata] = React.useState("")
  const [turno, setTurno] = React.useState("")
  const [servizio, setServizio] = React.useState("")
  const [forced, setForced] = React.useState(false)
  const [utentiSelezionatiGuardia, setUtentiSelezionatiGuardia] = React.useState([])
  const [utentiSelezionatiReperibilita, setUtentiSelezionatiReperibilita] = React.useState([])
  const [state, setState] = React.useState({bottom: false});
  const [giustificato, setGiustificato] = React.useState(false)
  const [allServices, setAllServices] = React.useState([])
  const [timeSlot, setTimeSlot] = React.useState("")
  const [selectedShiftDaysOfWeek, setSelectedShiftDaysOfWeek] = React.useState([]);
  const [giustificazioneState, setGiustificazioneState] = React.useState("")
  let giustificazione = ""


  //Sono costretto a dichiarare questa funzione per poterla invocare in modo asincrono.
  async function getUser() {
    let doctorApi = new DoctorAPI();
    let doctors

    try {
      doctors = await doctorApi.getAllDoctorsInfo()
    } catch (err) {

      panic()
      return
    }
    const d = []
    for (let i = 0; i < doctors.length; i++) {
      d.push({
        label: doctors[i].name + " " + doctors[i].lastname + " - " + doctors[i].seniority,
        value: doctors[i]
      })
    }

    setUser(d);
  }

  async function getService() {
    let servizioAPI = new ServiceAPI();
    let services
    try {
      services = await servizioAPI.getAllServices()
    } catch (err) {

      panic()
      return
    }

    const d = []
    for (let i = 0; i < services.length; i++) {
      d.push({label: services[i].name, value: services[i]})
    }

    setServizio(d);
    setAllServices(d);
  }


  function formatDaysOfWeek(daysOfWeek) {
    let showDaysOfWeek = "";
    for (var i = 0; i < daysOfWeek.length; i++) {
      showDaysOfWeek = showDaysOfWeek.substring(0, showDaysOfWeek.length) + t(daysOfWeek[i]) + ",";
    }

    return showDaysOfWeek.substring(0, showDaysOfWeek.length - 1);
  }

  async function getShift(servizio) {
    if (servizio.length > 0) {

      let turnoApi = new TurnoAPI();
      let shifts

      try {
        shifts = await turnoApi.getTurniByServizio(servizio[0].label)
      } catch (err) {

        panic()
        return
      }

      const d = []
      for (let i = 0; i < shifts.length; i++) {
        if (shifts[i].daysOfWeek.length == 7) {
          d.push({
            label: t("Everyday") + " - " + t(shifts[i].tipologia),
            value: shifts[i]
          });
        } else {
          console.log(shifts[i].daysOfWeek);
          let showDaysOfWeek = formatDaysOfWeek(shifts[i].daysOfWeek);
          d.push({
            label: showDaysOfWeek + " - " + t(shifts[i].tipologia),
            value: shifts[i]
          });
        }
      }

      setTurno(d);
    } else {
      setTurno([]);
    }
  }

  //Questa funzione aggiorna lo stato del componente.
  React.useEffect(() => {
    getUser();
    getService();
    getShift(servizio);
  }, []);

  //Funzione che implementa l'inversione di controllo. Verrà invocata dal componente figlio che permette di selezionare la data.
  //Viene passata al componente <BasicDatePicker>
  const handleData = (data) => {
    setdata(data);
  }


  //Funzione che implementa l'inversione di controllo. Verrà invocata dal componente figlio che permette di selezionare il turno.
  //Viene passata al componente <MultipleSelect>
  const handleTurno = (timeslot) => {
    if (timeslot.length > 0) {
      setTimeSlot(timeslot[0].value.tipologia)
      setSelectedShiftDaysOfWeek(timeslot[0].value.daysOfWeek)
    }
  }

  const handleServizio = (servizio) => {
    if (servizio.length > 0) {
      setServizio(servizio[0].value);
      getShift(servizio);
    }
  }

  //Funzione che apre la schermata secondaria che permette di creare un associazione.
  //Viene passata come callback al componente <Drawer>
  const toggleDrawer = (anchor, open) => (event) => {
    if (event.type === 'keydown' && (event.key === 'Tab' || event.key === 'Shift')) {
      return;
    }
    setState({...state, [anchor]: open});

  };

  const giustificaCompilata = (anchor, open) => async (event) => {
    if (giustificazione !== '') {
      setGiustificato(true)
      setGiustificazioneState(giustificazione)
    } else {
      toast.error(t("Justification not compiled"), {
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


  //La funzione verrà invocata quando l'utente schiaccerà il bottone per creare una nuova assegnazione.
  //Viene passata come callback al componente <Button>Assegna turno</Button>
  const assegnaTurno = (anchor, open) => async (event) => {
    if (event.type === 'keydown' && (event.key === 'Tab' || event.key === 'Shift')) {
      return;
    }

    let assegnazioneTurnoAPI = new AssegnazioneTurnoAPI()
    let response; //risposta http del server. In base al suo valore è possibile capire se si sono verificati errori

    /**
     * Chiediamo al backend di registrare l'assegnazione turno.
     * In base alla risposta che riceviamo sapremo se l'assegnazione è andata a buon fine,
     * oppure se è stata rigettata.
     * In quest'ultimo caso ci verranno forniti dei messaggi informativi per l'utente riguardo a cosa è andato storto.
     */
    {/*const mansione = turno.toString().substring(turno.toString().lastIndexOf(" ")+1, turno.toString().length)
    const tipologiaTurno = turno.toString().substring(0,turno.toString().indexOf(" "))*/
    }
    const mansione = turno;
    const tipologiaTurno = timeSlot;
    console.log("Servizio: ", servizio);
    console.log(mansione);

    let today = new Date();
    let startDateAsDate = new Date(data);

    if (data === "") {
      toast.error(t("Date must be selected"), {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
    } else if (today.getTime() > startDateAsDate.getTime()) {
      toast.error(t("Start date must be after at least today"), {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
    } else if (servizio === "") {
      toast.error(t("Choose a medical service from the ones offered by the hospital"), {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
    } else if (tipologiaTurno === "") {
      toast.error(t("Select a timeslot for the new concrete shift"), {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
    } else if (utentiSelezionatiGuardia.length === 0) {
      toast.error(t("Select doctors on duty"), {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
    } else if (utentiSelezionatiReperibilita.length === 0) {
      toast.error(t("Select doctors on call"), {
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

      if (selectedShiftDaysOfWeek) {

        const dateDayOfWeek = startDateAsDate.getDay()
        const weekday = ["SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"];
        const result = selectedShiftDaysOfWeek.reduce((result, value) => {
          return result || (value === weekday[dateDayOfWeek])
        }, false)

        if (!result) {
          toast.error(t("Error in creating assignement, incompatible days of week"), {
            position: "top-center",
            autoClose: 5000,
            hideProgressBar: true,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
            theme: "colored",
          });
          return
        }
        try {
          response = await assegnazioneTurnoAPI.postAssegnazioneTurno(data, tipologiaTurno, utentiSelezionatiGuardia, utentiSelezionatiReperibilita, servizio, mansione, forced);
        } catch (err) {

          panic();
          return
        }
      }
      /* If all fields are non empty*/

      //Chiamo la callback che aggiorna i turni visibili sullo scheduler.
      props.onPostAssegnazione()

      //Verifico la risposta del server analizzando il codice di risposta http
      let responseStatusClass = Math.floor(response.status / 100)
      switch (responseStatusClass) {

        // 200 family, success
        case 2:
          // Informa l'utente che l'assegnazione turno è stata registrata con successo
          toast.success(t("Assignment successfully created"), {
            position: "top-center",
            autoClose: 5000,
            hideProgressBar: true,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
            theme: "colored",
          });
          if (forced === true) {
            let giustificaForzaturaAPI = new GiustificaForzaturaAPI()
            //TODO: check for deletion //let bodyResponse = response.json()
            //TODO: check for deletion //let assegnazioneTurnoId = bodyResponse.turno
            let utente_id = localStorage.getItem("id")

            let utentiAllocatiIDs = utentiSelezionatiGuardia.map((value) => {
              return value.value.id
            })

            var requestParams = {
              message: giustificazioneState,
              utenteGiustificatoreId: utente_id,
              giorno: data.$d.getDate(),
              mese: data.$d.getMonth() + 1,
              anno: data.$d.getFullYear(),
              //TODO: check for deletion //turno : turno,
              timeSlot: timeSlot,
              utentiAllocati: utentiAllocatiIDs,
              servizio: servizio
            }
            console.log("FANFADEBUG: " + turno)


            let status; //Codice di risposta http del server. In base al suo valore è possibile capire se si sono verificati errori

            try {
              status = await giustificaForzaturaAPI.caricaGiustifica(requestParams);
            } catch (err) {

              panic()
              return
            }
            if (status === 202) {
              toast.success(t("Justification saved"), {
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
              // TODO: Bisogna cancellare l'assegnazione turno inserita
              toast.error(t("An error occurred while trying to save the justification"), {
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
          break;
        // 400 family, malformed request
        case 4:
          if (response.status === 406) {

            /**
             * L'assegnazione è fallita a causa di una violazione dei vincoli.
             * Mostriamo a schermo quali vincoli sono stati violati.
             */
            let responseBody = await response.json();
            /**
             * FIXME: non sono riuscito a passare al toast il componente del ViolationLog usando la sintassi JSX
             * perché non riuscivo a passargli i messaggi da stampare come props. Questo è un workaround che aggira il problema
             * simulando il passaggio delle props invocando il componente direttamente come funzione. Se qualcuno riesce a sistemarlo
             * passandolo direttamente nella forma <ViolationLog log={responseBody.messagges}/> sarebbe meglio.
             */
            toast.error(ViolationLog({log: responseBody.messagges}), {
              position: "top-center",
              hideProgressBar: false,
              closeOnClick: true,
              pauseOnHover: true,
              draggable: true,
              progress: undefined,
              theme: "colored",
              autoClose: false,
            });
          } else if (response.status === 404) {
            toast.error(t("Generate a new planning before adding a concrete shift"), {
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
            // malformed request

            toast.error(t("Parameters Error"), {
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
          break;
        // 500 family, server error
        case 5:
        // TODO: Dovremmo gestire i casi in cui il server si inceppa, e informare l'utente riguardo
        // le cause del problema e consigliargli di contattare un amministratore o un tecnico (noi!)
        default:
          // If you get here, something went really wrong. For real. :/
          console.log("Unexpected response status: " + response.status);
          break;

      }
      // chiudiamo il cassetto dell'assegnazione turno solo se l'assegnazione è andata a buon fine
      setState({
        ...state,
        [anchor]: (responseStatusClass === 2) ? open : !open
      });
      setForced(false);
      setGiustificato(false);
    }
  }

  const handleChange = (e) => {
    e.persist()
    giustificazione = e.target.value
  };

  function Giustifica() {
    return (
      <MDBCard>
        <MDBCardBody>
          <MDBCardTitle
            className="text-center">{t("Explain the override")}</MDBCardTitle>
          <MDBTextArea
            contrast id='textAreaGiustifica'
            rows={4}
            className="text"
            onChange={handleChange}
            required>

          </MDBTextArea>
          <li>{t("Add Waiver:")}</li>
          <FilesUpload/>
          <Button title={t('Save')}
                  onClick={giustificaCompilata('bottom', false)}>
            {t('Save')}
          </Button>
        </MDBCardBody>
      </MDBCard>
    )
  }


  return (
    <div>
      <React.Fragment key='bottom'>
        <Button
          variant="contained"
          style={{marginBottom: "16px"}}
          onClick={toggleDrawer('bottom', true)}
        >
          {t("Add Concrete Shift")}
        </Button>

        <Drawer anchor='bottom' open={state['bottom']}
                onClose={toggleDrawer('bottom', false)}>
          <div style={{
            display: 'flex',
            'padding-top': '20px',
            justifyContent: 'center',
            height: '75vh',
          }}>


            <Stack spacing={3}>
              <BasicDatePicker onSelectData={handleData}></BasicDatePicker>
              {/*<MultipleSelect onSelectTurno = {handleTurno} onSelectServizio = {handleServizio}></MultipleSelect>*/}
              <Autocomplete
                onChange={(event, value) => handleServizio(value)}
                multiple
                options={allServices}
                sx={{width: 300}}
                renderInput={(params) => <TextField {...params}
                                                    label={t("Service")}/>}
              />
              <Autocomplete
                onChange={(event, value) => handleTurno(value)}
                multiple
                options={turno}
                sx={{width: 300}}
                renderInput={(params) => <TextField {...params}
                                                    label={t("Shift")}/>}
              />
              <Autocomplete
                onChange={(event, value) => setUtentiSelezionatiGuardia(value)}
                multiple
                options={user}
                sx={{width: 300}}
                renderInput={(params) => <TextField {...params}
                                                    label={t("Doctors on Duty")}/>}
              />
              <Autocomplete
                onChange={(event, value) => setUtentiSelezionatiReperibilita(value)}
                multiple
                options={user}
                sx={{width: 300}}
                renderInput={(params) => <TextField {...params}
                                                    label={t("Doctors on Call")}/>}
              />
              <div>
                <FormControlLabel control={<Switch onClick={() => {
                  setForced(!forced);
                  setGiustificato(false)
                }}/>} label={t("Override Optional Constraints")}/>
                <InformationDialogs></InformationDialogs>
                {(forced && !giustificato && <Giustifica/>)}
              </div>
              {(forced && giustificato &&
                <MDBCard><MDBCardBody>{t("Compiled Justification")}</MDBCardBody></MDBCard>)}

              <Button variant="contained" size="small"
                      disabled={forced && !giustificato}
                      onClick={assegnaTurno('bottom', false)}>
                {t("Assign Concrete Shift")}
              </Button> {/* todo shift */}
            </Stack>
          </div>
        </Drawer>
      </React.Fragment>
    </div>

  );
}
