/**What kind of schedulable object is this? */
export const SchedulableType = {
    AssignedShift: Symbol('AssignedShift'),
    Holiday: Symbol('Holiday'),
}

/** A schedulable is an object that can be displayed in the scheduler */
class Schedulable{
    constructor(title, startDate, endDate, schedulableType){
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.schedulableType = schedulableType;
    }
}

export class AssignedShift extends Schedulable{
    constructor(title, startDate, endDate){
        super(title, startDate, endDate, SchedulableType.AssignedShift);
    }
}

export class Holiday extends Schedulable{
    constructor(title, startDate, endDate){
        super(title, startDate, endDate, SchedulableType.Holiday);
    }
}