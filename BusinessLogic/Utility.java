package BusinessLogic;

import DataModel.*;
import java.util.*;

public class Utility{

    /**
     * Builds and returns a list of employees whose
     * worked time is at least 40 hours.
     */
    public List<Employee> findOverworkedEmployees(List<Employee> employees) {
        List<Employee> overworkedEmployees = new ArrayList<>();

        for(Employee employee : employees) {
             int workedHours = 0;

             for(Task task : employee.getTasks()) {
                 if(task.getStatusTask().equals("Completed")) {
                     workedHours+=task.estimateDuration();
                 }
                 else if(task.getStatusTask().equals("Uncompleted") && task.taskType()==1) {
                     workedHours+=sumSubtaskDuration(task);
                 }
             }
             if(workedHours >= 40) {
                 overworkedEmployees.add(employee);
             }
         }
        return overworkedEmployees;
    }

    /**
     * Counts how many tasks an employee has.
     * It separates completed and uncompleted tasks.
     */
    public Map<String,Integer> calculateEmployeeNumberOfTasks(int idEmployee, List<Employee> employees,boolean whichTaskType) {
        int completedTasks = 0;
        int uncompletedTasks = 0;
        Map<String,Integer> employeeTasks = new HashMap<>();

        for(Employee employee : employees) {
            if (idEmployee == employee.getIdEmployee()) {
                for (Task task : employee.getTasks()) {
                    if (task.getStatusTask().equals("Completed")) {
                        completedTasks++;
                    } else if (task.getStatusTask().equals("Uncompleted")) {
                        uncompletedTasks++;
                    }
                }
                if(whichTaskType) {
                    employeeTasks.put(employee.getName(), completedTasks);
                }
                else
                    employeeTasks.put(employee.getName(), uncompletedTasks);
            }
        }
        return employeeTasks;
    }

    /**
     * Recursively goes through all subtasks of a given task.
     * @param task represents given task, if complex sum the subtask's duration.
     * @return Duration of completed tasks.
     */
    public static int sumSubtaskDuration(Task task) {
        int subtasksDuration = 0;
        for(Task subtask : task.getSubtasks()) {
            if(subtask.taskType() == 1) {
                subtasksDuration += sumSubtaskDuration(subtask);
            }
            else if(subtask.getStatusTask().equals("Completed") && subtask.taskType() == 0) {
                subtasksDuration+=subtask.estimateDuration();
            }
        }
        return subtasksDuration;
    }
}