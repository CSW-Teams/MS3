export class MedicalService {
    constructor (id, name, tasks) {
        // medical service id
        this.id       = id;

        // medical service name
        this.name     = name;

        // tasks, as a list of Task
        this.tasks    = tasks;

        // true if at least one task is assigned
        this.assigned = this.isAtLeastOneTaskAssigned();
    }

    isAtLeastOneTaskAssigned() {
        var assignedCheck = false;
        for (let j = 0; j < this.tasks.length; j++) {
            if(this.tasks[j].assigned == true){
                assignedCheck = true;
            }
        }
        return assignedCheck;
    }

    getTasksAsString() {
    var string = "";
        for (let j = 0; j < this.tasks.length; j++) {
            string = string.concat(
                this.tasks[j].taskType,
                (j != this.tasks.length-1) ? ", " : ""
            );
        }
    return string;
    }
}
