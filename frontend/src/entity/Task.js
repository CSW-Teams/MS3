export class Task {
    constructor (id, taskType, assigned) {
        // task id
        this.id       = id;

        // task name (type)
        this.taskType = taskType;

        // true if the task is assigned to at least one doctor
        this.assigned = assigned;
    }
}
