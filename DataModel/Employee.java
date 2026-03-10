
package DataModel;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Employee implements Serializable {
    private int idEmployee;
    private String name;
    private List<Task> tasks;

    public Employee(int idEmployee, String name) {
        this.idEmployee = idEmployee;
        this.name = name;
        this.tasks = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getIdEmployee() {
        return idEmployee;
    }

    /**
     * @return the list of tasks assigned to the employee.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Gives a task to an employee.
     * @param task represents the task to be given to the employee.
     */
    public void assignTask(Task task){
        tasks.add(task);
    }
}
