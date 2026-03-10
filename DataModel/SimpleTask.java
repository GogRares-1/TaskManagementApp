
package DataModel;

import java.util.List;

public final class SimpleTask extends Task {
    private int startHour;
    private int endHour;

    public SimpleTask(int idTask, String statusTask, String taskName, int startHour, int endHour) {
        super(idTask, statusTask, taskName);
        this.startHour = startHour;
        this.endHour = endHour;
    }

    /**
     * Computes the duration of a simple task.
     * For invalid intervals it returns -1.
     */
    @Override
    public int estimateDuration() {
        int duration = endHour - startHour;
        if(duration<0)
            return -1;
        else return duration;
    }

    /**
     * Simple tasks don't have any subtasks.
     * @return an empty list.
     */
    @Override
    public List<Task> getSubtasks() {
        return List.of();
    }

    /**
     * @return 0 means a simple task.
     */
    @Override
    public int taskType(){
        return 0;
    }
}
