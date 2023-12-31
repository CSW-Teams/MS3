import { Holiday, SchedulableType } from "./Schedulable";
import {red} from "@mui/material/colors";

export class HolidaysAPI {


    /** gets all holidays from backend  */
    async getHolidays() {

        let date = new Date()
        let timeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;
        let county = await this.searchCountry(timeZone);

        let response = await fetch('/api/holidays/year='+date.getFullYear()+'/country='+county, {method: 'GET'});

        let serializedHolidays = await response.json();
        let holidays = [];

        // we need to rename some properties to make the holidays readable by the scheduler
        for (let sh of serializedHolidays){
            let h = new Holiday(
                sh.name,
                // frontend months are 0-11, backend months are 1-12
                new Date(sh.startYear, sh.startMonth - 1, sh.startDayOfMonth),
                new Date(sh.endYear, sh.endMonth - 1, sh.endDayOfMonth + 1), // +1 because the scheduler doesn't include the end date
                red,
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




}


