package BusinessLogic;

import DataModel.*;
import java.io.Serializable;
import java.util.*;

public class TasksManagement implements Serializable {

    private final Map<Employee, List<Task>> employees;
    private final List<Task> allTasks;

    public TasksManagement() {
        employees = new HashMap<>();
        allTasks = new ArrayList<>();
    }

    /**
     * Assigns a task to the employee identified by ID.
     * @param idEmployee the ID of the employee who receives the task
     * @param task the task to assign
     * @return 1 if the assignment succeeds, -1 otherwise
     */
    public int assignTaskToEmployee(int idEmployee, Task task) {
        for(Employee employee : employees.keySet()) {
            if(employee.getIdEmployee() == idEmployee) {
                for(Task assignedTask : employees.get(employee)) {
                    if(assignedTask.getIdTask() == task.getIdTask()) {
                        return -1;
                    }
                    if(assignedTask.taskType() == 1)
                    {
                       if(checkComplexAssignedTask(assignedTask,task)==-1)
                       {
                           return -1;
                       }
                    }
                }
                employees.get(employee).add(task);
                employee.assignTask(task);
                return 1;
            }
        }
        return -1;
    }

    /**
     * Checks if a task already exists inside
     *  the complex task tree.
     */
    private int checkComplexAssignedTask(Task assignedTask,Task task) {
        for(Task subtask: assignedTask.getSubtasks()) {
            if(subtask.getIdTask() == task.getIdTask()) {
                return -1;
            }
            if(subtask.taskType() == 1)
            {
                if(checkComplexAssignedTask(subtask,task)==-1)
                    return -1;
            }
        }
        return 0;
    }

    /**
     * Computes the work duration of an employee.
     * Completed tasks contribute with full duration,
     * while uncompleted complex ones contribute with the
     * duration of their completed subtasks.
     * @param idEmployee the ID of the employee.
     * @return the work duration, or -1 if the employee doesn't exist.
     */
    public int calculateEmployeeWorkDuration(int idEmployee){
        Employee givenEmployee = null;
        int workDuration = 0;
        List<Integer> existingTasks = new ArrayList<>();

        for(Employee employee : employees.keySet()) {
            if (employee.getIdEmployee() == idEmployee) {
                givenEmployee = employee;
                break;
            }
        }

        if(givenEmployee != null) {
            for(Task task : givenEmployee.getTasks()) {
                if(!existingTasks.contains(task.getIdTask())) {

                    if(task.taskType() == 0) {
                        if(task.getStatusTask().equals("Completed")) {
                            workDuration += task.estimateDuration();
                        }
                        existingTasks.add(task.getIdTask());
                    }

                    else if(task.taskType() == 1) {
                        existingTasks.add(task.getIdTask());

                        for(Task subtask : task.getSubtasks()) {
                            if(!existingTasks.contains(subtask.getIdTask())) {
                                if(subtask.taskType() == 0 && subtask.getStatusTask().equals("Completed")) {
                                    workDuration += subtask.estimateDuration();
                                    existingTasks.add(subtask.getIdTask());
                                }
                            }
                        }
                    }
                }
            }
            return workDuration;
        }
        else return -1;
    }

    /**
     * Changes the status of a task assigned to the given employee.
     * If the task is complex, the status is propagated to all of its subtasks.
     * @param idEmployee the ID of the employee.
     * @param idTask the ID of the task whose status is changed.
     */
    public void modifyTaskStatus(int idEmployee, int idTask) {
        for (Employee employee : employees.keySet()) {
            if (employee.getIdEmployee() == idEmployee) {
                for (Task task : employee.getTasks()) {
                    if (task.getIdTask() == idTask) {
                        task.setStatusTask();

                        if (task.taskType() == 1) {
                            changeStatusForSubtasks(task);
                        }

                        for (Task employeeTask : employee.getTasks()) {
                            if (employeeTask.taskType() == 1) {
                                updateComplexTaskStatus(employeeTask);
                            }
                        }
                        return;
                    }
                    if (task.taskType() == 1) {
                        if (findSubtask(task, idTask)) {
                            updateComplexTaskStatus(task);

                            for (Task employeeTask : employee.getTasks()) {
                                if (employeeTask.taskType() == 1) {
                                    updateComplexTaskStatus(employeeTask);
                                }
                            }
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * Recursively updates all subtasks so that their status matches
     * the status of the parent task.
     */
    private void changeStatusForSubtasks(Task task) {
        for(Task subtask:task.getSubtasks()) {
            if(!subtask.getStatusTask().equals(task.getStatusTask())) {
                subtask.setStatusTask();
            }
            if(subtask.taskType()==1) {
                changeStatusForSubtasks(subtask);
            }
        }
    }

    /**
     * Searches for a subtask with the given ID.
     * If found, it changes that subtask's status
     * along with its own subtasks.
     */
    private boolean findSubtask(Task task,int idTask) {
        for(Task subtask:task.getSubtasks()) {
            if(subtask.getIdTask() == idTask) {
                subtask.setStatusTask();
                changeStatusForSubtasks(subtask);
                updateComplexTaskStatus(task);
                return true;
            }
            if(subtask.taskType() == 1) {
                if (findSubtask(subtask, idTask)) {
                    updateComplexTaskStatus(task);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks for existence and based on that
     * add a new employee.
     */
    public int addEmployee(Employee employee) {
        for(Employee checkEmployee : employees.keySet()) {
            if(checkEmployee.getIdEmployee() == employee.getIdEmployee()){
                return -1;
            }
        }
        employees.put(employee, new ArrayList<>());
        return 0;
    }

    public Task getTaskById(int taskId) {
        for (Task task : allTasks) {
            if (task.getIdTask() == taskId) {
                return task;
            }
        }
        return null;
    }

    /**
     * Adds a subtask to a complex one which is
     * identified by the given ID.
     * @param complexTaskID the ID of the complex task.
     * @param subtask the subtask to add.
     * @return 1 if the operation succeeds, -1 otherwise.
     */
    public int addSubtaskToComplexTask(int complexTaskID,Task subtask) {
        if(complexTaskID == subtask.getIdTask()) {
            return -1;
        }

        Task task = getTaskById(complexTaskID);
        for(Task existingSubtask : task.getSubtasks())
        {
            if(existingSubtask.getIdTask() == subtask.getIdTask())
                return -1;
        }
        if (task.taskType()==1) {
            ((ComplexTask)task).addTask(subtask);
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * Checks if all subtasks of a complex task are completed.
     * @param task given task.
     * @return true if all completed, false otherwise.
     */
    private boolean subtasksCompleted(Task task) {
        for(Task subtask : task.getSubtasks()) {
            if(subtask.getStatusTask().equals("Uncompleted")) {
                return false;
            }

            if(subtask.taskType()==1) {
                if(!subtasksCompleted(subtask)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * If all subtasks are completed,
     * the parent is also completed.
     * @param task the given task.
     */
    private void updateComplexTaskStatus(Task task) {
        if(task.taskType()==1){
            if(subtasksCompleted(task)) {
                if(task.getStatusTask().equals("Uncompleted")) {
                    task.setStatusTask();
                }
            } else {
                if(task.getStatusTask().equals("Completed")) {
                    task.setStatusTask();
                }
            }
        }
    }

    public int addTask(Task task) {
        List<Task> otherTasks = getAllTasks();
        for(Task otherTask : otherTasks) {
            if(otherTask.getIdTask() == task.getIdTask()) {
                return -1;
            }
        }
        allTasks.add(task);
        return 0;
    }

    public List<Task> getAllTasks() {
        return allTasks;
    }

    public Map<Employee, List<Task>> getAllEmployees() {
        return employees;
    }
}
