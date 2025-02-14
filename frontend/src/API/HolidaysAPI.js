import { Holiday, SchedulableType } from "./Schedulable";
import {red} from "@mui/material/colors";
import {fetchWithAuth} from "../utils/fetchWithAuth";

export class HolidaysAPI {


    /** gets all holidays from backend  */
    async getHolidays(year) {

        let timeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;
        let county = await this.searchCountry(timeZone);

        let response = await fetchWithAuth('/api/holidays/year='+year+'/country='+county, {method: 'GET'});

        let serializedHolidays = await response.json();
        let holidays = [];

        // we need to rename some properties to make the holidays readable by the scheduler
        for (let sh of serializedHolidays){
            let h = new Holiday(
                sh.name,
                // frontend months are 0-11, backend months are 1-12
                new Date(sh.startDateEpochDay * 24 * 60 * 60 * 1000),
                new Date(sh.endDateEpochDay * 24 * 60 * 60 * 1000), // +1 because the scheduler doesn't include the end date
            );


            h.allDay = true;
            h.schedulableType=SchedulableType.Holiday;
            h.category = sh.category;
            h.location = sh.location;
            h.utenti_guardia = [-1];
            holidays.push(h);
        }

        return holidays;


    }

     //Questa funzione restituisce la nazione in cui è eseguito il frontend sulla base della timezone.
     //Serve per capire contattare il backend e richiedere le fetività di quella nazione specifica
    async searchCountry(timeZone){

        if(timeZone.includes("Rome"))
            return "IT"

        if(timeZone.includes("Paris"))
            return "FR"

        if(timeZone.includes("Madrid"))
            return "SP"

        // Altri timeZone..

        return "IT"
    }

    async saveCustomHoliday(holiday) {

      let response = await fetchWithAuth('/api/holidays/new-holiday', {
        method : 'POST',
        headers : {
          "Content-Type": "application/json"
        },
        body : JSON.stringify(holiday)
      })

      let content = await response.json()

      return [response.status, content] ;
    }

    async getCustomHolidays() {
      let response = await fetchWithAuth('/api/holidays/custom-holidays')

      let content = await response.json()

      return [response.status, content]
    }

    async deleteCustomHoliday(holidayID) {
      let response = await fetchWithAuth('/api/holidays/delete-custom', {
        method : 'DELETE',
        headers : {
          "Content-Type": "application/json"
        },
        body : JSON.stringify(holidayID)
      })

      return response.status
    }
}


