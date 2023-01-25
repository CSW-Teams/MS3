import { Holiday, SchedulableType } from "./Schedulable";
import {red} from "@mui/material/colors";

export class HolidaysAPI {

    /** gets all holidays from backend  */
    async getHolidays() {
        let response = await fetch('/api/holidays/year=2023/country=IT', {method: 'GET'});
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
}
