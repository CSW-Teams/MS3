import { Holiday } from "./Schedulable";

export class HolidaysAPI {

    /** gets all holidays from backend  */
    async getHolidays() {
        let response = await fetch('/api/holidays/', {method: 'GET'});
        let serializedHolidays = await response.json();
        let holidays = [];

        // we need to rename some properties to make the holidays readable by the scheduler
        for (let sh of serializedHolidays){

            let h = new Holiday(
                sh.name,
                // frontend months are 0-11, backend months are 1-12
                new Date(sh.startYear, sh.startMonth - 1, sh.startDayOfMonth),
                new Date(sh.endYear, sh.endMonth - 1, sh.endDayOfMonth + 1) // +1 because the scheduler doesn't include the end date
            );
            h.allDay = true;
            h.category = sh.category;
            h.location = sh.location;
            holidays.push(h);
        }

        return holidays;


    }
}
