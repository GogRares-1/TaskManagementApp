
package DataModel;

import java.util.ArrayList;
import java.util.List;

public final class ComplexTask extends Task {
    private List<Task> tasks;

    public ComplexTask(int idTask, String statusTask, String taskName) {
        super(idTask, statusTask, taskName);
        this.tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    @Override
    public List<Task> getSubtasks()
    {
        return List.copyOf(tasks);
    }

    @Override
    public int taskType(){
        return 1;
    }

    /**
     * Computes total estimated duration of this complex task
     * by summing the duration of all its subtasks.
     * @return total estimated duration.
     */
    @Override
    public int estimateDuration()
    {
        int duration =0;
        for(Task task : tasks)
        {
            duration += task.estimateDuration();
        }
        return duration;
    }

}
