/* eslint jsx-a11y/anchor-is-valid: 0 */

import React from 'react';
import {ViewState} from '@devexpress/dx-react-scheduler';
import {AllDayPanel} from '@devexpress/dx-react-scheduler-material-ui';
import { ServizioAPI } from '../API/ServizioAPI';
import Stack from '@mui/material/Stack';
import {AppointmentContent, Content} from "../components/common/CustomAppointmentComponents.js"
import Collapse from '@mui/material/Collapse';

import {
  Button, Grid,
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
} from '@devexpress/dx-react-scheduler-material-ui';

import {blue} from "@mui/material/colors";
import { ServiceFilterSelectorButton } from '../components/common/ServiceFilterSelectorButton';
import { UtenteAPI } from '../API/UtenteAPI';
import TextField from '@mui/material/TextField';
import Autocomplete from '@mui/material/Autocomplete';
import { HolidaysAPI } from '../API/HolidaysAPI';



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
            data: [],   // list of shifts to display in schedule (not filtered yet)
            mainResourceName: 'utenti_guardia',
            resources: [ // TODO: queste risorse sarebbero i dettagli da scrivere nelle carte dei turni assegnati? Ulteriori dettagli andrebbero aggiunti qui?
              {fieldName: 'utenti_guardia', title: 'Guardia', allowMultiple: true,instances: [{}] , color:blue,},
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
                  if(users[j].id === shift.utenti_guardia[i])
                    return true;

              return users.length === 0
            }.bind(this),

            // add more filters here ...
          ];
          this.changeMainResource = this.changeMainResource.bind(this);
          this.updateFilterCriteria = this.updateFilterCriteria.bind(this);
    }

    changeMainResource(mainResourceName) {
      this.setState({ mainResourceName });
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

    async componentDidMount(turni, utenti){
      let allServices = await new ServizioAPI().getService();
      let allUser = await new UtenteAPI().getAllUserOnlyNameSurname();
      let allHolidays = await new HolidaysAPI().getHolidays();


      this.setState(
        {
          data:turni,
          mainResourceName: 'utenti_guardia',
          resources:
            [
              {
                fieldName: 'utenti_guardia', title: 'Guardia',allowMultiple: true, instances: utenti,
              }
              ,{
             fieldName:'utenti_reperibili', title: 'Reperibilità',allowMultiple: true, instances: utenti,
            },
            ],
            allServices: new Set(allServices),
            allUser : allUser,
            holidays: allHolidays,
        })
      }



    render(){

        // add shifts to the schedulables to display
        let { data, resources} = this.state;

        /** Filtering of shifts is performed by ANDing results of all filter functions applied on each shift */
        data = data.filter((shift) => {
          return this.filters.reduce(
            (isFeasible, currentFilter) => isFeasible && currentFilter(shift, this.state.filterCriteria),
            true
          );
        });

        // add holidays to the schedulables to display
        data.push(...this.state.holidays);

        return (
          <React.Fragment>
            <Paper>
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
                  cellDuration={120}
                />
                <DayView
                  displayName="Giornaliero"
                  startDayHour={0}
                  endDayHour={24}
                  cellDuration={120}
                />
                <MonthView displayName="Mensile" />
                <Toolbar />
                <Appointments appointmentContentComponent={this.state.appointmentContentComponent} />
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
                <AppointmentTooltip
                  showCloseButton
                  contentComponent={Content} //go to CustomContent.js
                />
                <CurrentTimeIndicator
                  shadePreviousAppointments={true}
                  shadePreviousCells={true}
                  updateInterval={60000}
                />
              </Scheduler>
            </Paper>
          </React.Fragment>
        );

    }

}

export default ScheduleView;
