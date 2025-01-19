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
    constructor(title, startDate, endDate, schedulableType, shiftState){
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.schedulableType = schedulableType;
        if(schedulableType === SchedulableType.Holiday) {
          this.color = 'red'
        }
        else if (schedulableType === SchedulableType.AssignedShift){
          this.color = this.getColorFromShiftState(shiftState);
        }
        else{
          this.color = 'black'
        }
        this.shiftState=shiftState.description
    }
  /**
   * Restituisce il colore basato sullo stato dello shift.
   * @param {Symbol} shiftState - Stato dello shift.
   * @returns {string} Colore corrispondente allo stato dello shift.
   */
  getColorFromShiftState(shiftState) {
    switch (shiftState) {
      case ShiftState.Complete:
        return '#4db6ac';
      case ShiftState.Incomplete:
        return 'orange';
      case ShiftState.Infeasible:
        return '#e05568';
      case ShiftState.Holiday:
        return 'red';
      default:
        return 'black'; // Valore di default
    }
  }

}

export class AssignedShift extends Schedulable{
    constructor(title, startDate, endDate, shiftState){
        super(title, startDate, endDate, SchedulableType.AssignedShift, shiftState);
    }
}

export class Holiday extends Schedulable{
    constructor(title, startDate, endDate){
        super(title, startDate, endDate, SchedulableType.Holiday, ShiftState.Holiday);
    }
}
