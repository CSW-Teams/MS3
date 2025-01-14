class Shift {
  constructor({
                id,
                timeSlot,
                startHour,
                startMinute,
                durationMinutes,
                daysOfWeek = [],
                medicalService,
                quantityShiftSeniority = [],
                additionalConstraints = [],
              }) {
    this.id = id;
    this.timeSlot = timeSlot;
    this.startHour = startHour;
    this.startMinute = startMinute;
    this.durationMinutes = durationMinutes;
    this.daysOfWeek = daysOfWeek;
    this.medicalService = medicalService;
    this.quantityShiftSeniority = quantityShiftSeniority;
    this.additionalConstraints = additionalConstraints;
  }
}
