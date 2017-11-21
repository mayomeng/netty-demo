package ad.publish.logic.distributior;

import ad.publish.task.Task;


public class AdEvent {

    private Task task;

    public void setTask(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
