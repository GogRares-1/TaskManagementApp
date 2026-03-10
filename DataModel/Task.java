
package DataModel;

import java.io.Serializable;
import java.util.List;

public abstract sealed class Task implements Serializable permits ComplexTask, SimpleTask {
    private int idTask;
    private String statusTask;
    private String taskName;

    public Task(int idTask, String statusTask, String taskName) {
        this.idTask = idTask;
        this.statusTask = statusTask;
        this.taskName = taskName;
    }

    public int getIdTask() {
        return idTask;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getStatusTask() {
        return statusTask;
    }

    /**
     * Changes the task status from Uncompleted to Completed or the other way around.
     * This method does not receive a new status as parameter.
     */
    public void setStatusTask() {
        if(this.statusTask.equals("Uncompleted")) {
            this.statusTask = "Completed";
        }
        else {
            this.statusTask = "Uncompleted";
        }
    }

    public abstract List<Task> getSubtasks();
    public abstract int taskType();
    public abstract int estimateDuration();
}
