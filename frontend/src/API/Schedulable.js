/**What kind of schedulable object is this? */
import {red} from "@mui/material/colors";
import {teal} from "@material-ui/core/colors";

export const SchedulableType = {
    AssignedShift: Symbol('AssignedShift'),
    Holiday: Symbol('Holiday'),
}

/** A schedulable is an object that can be displayed in the scheduler */
class Schedulable{
    constructor(title, startDate, endDate, schedulableType,color){
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.schedulableType = schedulableType;
        this.color=color;
    }
}

export class AssignedShift extends Schedulable{
    constructor(title, startDate, endDate){
        super(title, startDate, endDate, SchedulableType.AssignedShift,teal);
    }
}

export class Holiday extends Schedulable{
    constructor(title, startDate, endDate,color){
        super(title, startDate, endDate, SchedulableType.Holiday,color);
    }
}
