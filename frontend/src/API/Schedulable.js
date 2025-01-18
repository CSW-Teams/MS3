/**What kind of schedulable object is this? */

export const SchedulableType = {
    AssignedShift: Symbol('AssignedShift'),
    Holiday: Symbol('Holiday'),
}

export const ShiftState = {
  Complete: Symbol('Complete'),
  Incomplete: Symbol('Incomplete'),
  Infeasible: Symbol('Infeasible'),
  Holiday: Symbol('Holiday')
}

/** A schedulable is an object that can be displayed in the scheduler */
class Schedulable{
    constructor(title, startDate, endDate, schedulableType, color, shiftState){
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.schedulableType = schedulableType;
        this.color=color;
        this.shiftState=shiftState.description
    }
}

export class AssignedShift extends Schedulable{
    constructor(title, startDate, endDate, baseColor, shiftState){
        super(title, startDate, endDate, SchedulableType.AssignedShift, baseColor, shiftState);
    }
}

export class Holiday extends Schedulable{
    constructor(title, startDate, endDate, color){
        super(title, startDate, endDate, SchedulableType.Holiday,color, ShiftState.Holiday);
    }
}
