package GUI;

import BusinessLogic.*;
import DataAccess.Serialization;
import DataModel.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class GUI {
    private final TasksManagement taskManager;
    private final Utility utility;
    private JFrame frame;
    private JTextArea textBox;
    private StringBuilder empAndTasks = new StringBuilder();

    /**
     * Constructor which receives:
     * TaskManagement object with app data and logic, stored in taskManager
     * creates a Utility object for statistics operations
     * calls initGUI to build the window
     */
    public GUI(TasksManagement TM) {
        taskManager = TM;
        utility = new Utility();
        initGUI();
    }

    /**
     * Initializes the GUI
     */
    private void initGUI() {
        frame = new JFrame("Task Management Window");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        JButton addEmployeeBtn = new JButton("Add Employee");
        panel.add(addEmployeeBtn);
        addEmployeeBtn.addActionListener(e -> addEmployee());

        JButton addTaskBtn = new JButton("Add Task");
        panel.add(addTaskBtn);
        addTaskBtn.addActionListener(e -> addTask());

        JButton addSubtaskBtn = new JButton("Add Subtask");
        panel.add(addSubtaskBtn);
        addSubtaskBtn.addActionListener(e -> addSubtaskToComplexTask());

        JButton assignTaskBtn = new JButton("Assign Task");
        panel.add(assignTaskBtn);
        assignTaskBtn.addActionListener(e -> assignTask());

        JButton viewEmployeesBtn = new JButton("View Employees");
        panel.add(viewEmployeesBtn);
        viewEmployeesBtn.addActionListener(e -> viewEmployees());

        JButton viewStatisticsBtn = new JButton("View Statistics");
        panel.add(viewStatisticsBtn);
        viewStatisticsBtn.addActionListener(e -> viewStatistics());

        JButton modifyTaskStatusBtn = new JButton("Modify Task Status");
        panel.add(modifyTaskStatusBtn);
        modifyTaskStatusBtn.addActionListener(e -> modifyTaskStatus());

        JButton saveDataBtn = new JButton("Save Data");
        panel.add(saveDataBtn);
        saveDataBtn.addActionListener(e -> {
            Serialization.save(taskManager);
            JOptionPane.showMessageDialog(frame, "Data saved", "Save", JOptionPane.INFORMATION_MESSAGE);
        });

        textBox = new JTextArea(10, 50);
        textBox.setEditable(false);
        JScrollPane info = new JScrollPane(textBox);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(info, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    /**
     * Used to allow the user to add an employee.
     */
    private void addEmployee() {
        String id = JOptionPane.showInputDialog("Enter employee ID:");
        String name = JOptionPane.showInputDialog("Enter employee name:");
      try {
          if (id != null && name != null) {
              int employeeID = Integer.parseInt(id);
              Employee employee = new Employee(employeeID, name);
              if(taskManager.addEmployee(employee)==-1)
              {
                  textBox.setText("Error adding employee");
                  return;
              }
              textBox.setText("Employee added: ID " + employeeID + ", named: " + name);
          }
      } catch (Exception e) {
          JOptionPane.showMessageDialog(frame, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
      }
    }

    /**
     * Used to allow the user to add a task.
     */
    private void addTask() {

        String type = JOptionPane.showInputDialog("Enter task type: \n (1) for Simple \n (2) for Complex");
        String id = JOptionPane.showInputDialog("Enter task ID:");
        String name = JOptionPane.showInputDialog("Enter task name:");

        try {
            if ((id != null) && name != null && type != null) {
                int taskID = Integer.parseInt(id);
                Task newTask;
                if (type.equals("1")) {
                    String start = JOptionPane.showInputDialog("Enter start hour:");
                    String end = JOptionPane.showInputDialog("Enter end hour:");
                    newTask = new SimpleTask(taskID, "Uncompleted", name, Integer.parseInt(start), Integer.parseInt(end));
                } else {
                    newTask = new ComplexTask(taskID, "Uncompleted", name);
                }
                if(taskManager.addTask(newTask)==-1)
                {
                    textBox.setText("Error adding task");
                    return;
                }
                textBox.setText("Task added: ID " + taskID + ", named: " + name);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     * Used to allow the user to attach an existing task
     * as a subtask to a complex task.
     */
    private void addSubtaskToComplexTask() {
        StringBuilder complexTasks = new StringBuilder("Complex tasks:\n");
        for (Task task : taskManager.getAllTasks()) {
            if (task.taskType() == 1) {
                complexTasks.append("ID: ").append(task.getIdTask()).append(", named: ").append(task.getTaskName()).append("\n");
            }
        }
        String complexTaskID = (String) JOptionPane.showInputDialog(frame, complexTasks + "\nEnter complex task ID:", "Selection", JOptionPane.PLAIN_MESSAGE, null, null, "");

        StringBuilder allTasks = new StringBuilder("Available tasks:\n");
        for (Task task : taskManager.getAllTasks()) {
            allTasks.append("ID: ").append(task.getIdTask()).append(", named: ").append(task.getTaskName()).append("\n");
        }
        String subTID = (String) JOptionPane.showInputDialog(frame, allTasks + "\nEnter subtask ID:", "Selection", JOptionPane.PLAIN_MESSAGE, null, null, "");

        if (subTID == null || subTID.trim().isEmpty() || complexTaskID == null || complexTaskID.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int complexID = Integer.parseInt(complexTaskID);
            int subtaskID = Integer.parseInt(subTID);

            Task complexTask = taskManager.getTaskById(complexID);
            Task subtask = taskManager.getTaskById(subtaskID);

            if (complexTask != null && subtask != null) {
                if (taskManager.addSubtaskToComplexTask(complexID, subtask) == 1) {
                    textBox.setText("Subtask ID: " + subtaskID + " added to complex task ID: " + complexID);
                } else {
                    textBox.setText("Failed to add subtask");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Used to allow the user to assign a task to an employee.
     */
    private void assignTask() {
        StringBuilder employees = new StringBuilder("Employees:\n");
        for (Employee employee : taskManager.getAllEmployees().keySet()) {
            employees.append("ID: ").append(employee.getIdEmployee()).append(", named: ").append(employee.getName()).append("\n");
        }

        String employeeID = (String) JOptionPane.showInputDialog(frame, employees + "\nEnter employee ID:", "Selection", JOptionPane.PLAIN_MESSAGE, null, null, "");
        if (employeeID == null || employeeID.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Invalid employee ID", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder allTasks = new StringBuilder("Tasks:\n");
        for (Task task : taskManager.getAllTasks()) {
            allTasks.append("ID: ").append(task.getIdTask()).append(", named: ").append(task.getTaskName()).append("\n");
        }

        String taskID = (String) JOptionPane.showInputDialog(frame, allTasks + "\nEnter task ID:", "Selection", JOptionPane.PLAIN_MESSAGE, null, null, "");
        if (taskID == null || taskID.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Invalid task ID", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int empID = Integer.parseInt(employeeID);
            int TaskID = Integer.parseInt(taskID);
            Task task = taskManager.getTaskById(TaskID);

            if (task != null) {
                if(taskManager.assignTaskToEmployee(empID, task)==-1){
                    textBox.setText("Employee not found or task was already assigned");
                }
                else {
                    textBox.setText("Task ID: " + TaskID + " assigned to employee ID: " + empID);
                }
            } else {
                textBox.setText("Task not found");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Used to allow the user to see all employees.
     */
    private void viewEmployees() {
       empAndTasks = new StringBuilder("Employees & assigned tasks:\n\n");
       Map<Employee,List<Task>> employees = taskManager.getAllEmployees();

        for (Employee employee : employees.keySet()) {
            List<Task> assignedTasks = employee.getTasks();

            empAndTasks.append("Employee ID: ").append(employee.getIdEmployee()).append(", named: ").append(employee.getName()).append(" has worked ").append(taskManager.calculateEmployeeWorkDuration(employee.getIdEmployee())).append(" hours \n");

            if (assignedTasks.isEmpty()) {
                empAndTasks.append("No assigned task(s)\n");
            } else {
                for (Task task : assignedTasks) {
                    empAndTasks.append("~ Task ID: ").append(task.getIdTask()).append(", named: ").append(task.getTaskName()).append(" (Duration: ").append(task.estimateDuration()).append(" hours) ( Status: ").append(task.getStatusTask()).append(" )\n");
                    if(task instanceof ComplexTask)
                    {
                        displayComplexSubtasks(task);
                    }
                }
            }
            empAndTasks.append("\n");
        }
        textBox.setText(empAndTasks.toString());
    }

    /**
     * Used to allow the user to see all subtasks of a complex task.
     * @param task given complex task.
     */
    private void displayComplexSubtasks(Task task) {
        for(Task subtask : task.getSubtasks())
        {
            empAndTasks.append("~~ Subtasks ID: ").append(subtask.getIdTask()).append(", named: ").append(subtask.getTaskName()).append(" (Duration: ").append(subtask.estimateDuration()).append(" hours) ( Status: ").append(subtask.getStatusTask()).append(" )\n");
            if(subtask instanceof ComplexTask)
            {
                empAndTasks.append(" \n Previous complex tasks subtasks: \n");
                displayComplexSubtasks(subtask);
            }
        }
        empAndTasks.append("\n");
    }

    /**
     * Used to allow the user to modify the status of a task.
     */
    private void modifyTaskStatus() {
        String employeeID = JOptionPane.showInputDialog("Enter employee ID:");
        String taskID = JOptionPane.showInputDialog("Enter task ID:");
        try{
        if (employeeID != null && taskID != null) {
            int empID = Integer.parseInt(employeeID);
            int TaskID = Integer.parseInt(taskID);
            taskManager.modifyTaskStatus(empID, TaskID);
            textBox.setText("Updated status for task ID: " + TaskID + " of employee ID: " + empID);
        }
        else {
            textBox.setText("Invalid input");
        }
        }catch(Exception e){
            JOptionPane.showMessageDialog(frame, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Used to allow the user to see statistics
     * related to employees and tasks.
     */
    private void viewStatistics() {
        StringBuilder stats = new StringBuilder("Overworked employees:\n");
        List<Employee> overworked = utility.findOverworkedEmployees(List.copyOf(taskManager.getAllEmployees().keySet()));
        for (Employee employee : overworked) {
            stats.append(employee.getName()).append(", has worked ").append(taskManager.calculateEmployeeWorkDuration(employee.getIdEmployee())).append(" hours \n");
        }

        stats.append("\n Employee task statistics: \n");
        for (Employee employee : taskManager.getAllEmployees().keySet()) {
            Map<String, Integer> taskStats = utility.calculateEmployeeNumberOfTasks(employee.getIdEmployee(), List.copyOf(taskManager.getAllEmployees().keySet()), true);
            stats.append(employee.getName()).append("~ Completed tasks: ").append(taskStats.getOrDefault(employee.getName(), 0)).append(" \n");

            taskStats = utility.calculateEmployeeNumberOfTasks(employee.getIdEmployee(), List.copyOf(taskManager.getAllEmployees().keySet()), false);
            stats.append(employee.getName()).append("~ Uncompleted tasks: ").append(taskStats.getOrDefault(employee.getName(), 0)).append(" \n");
            stats.append("\n");
        }
        textBox.setText(stats.toString());
    }

    /**
     * Starting point of the app.
     * The app loads saved data or creates
     * a new task manager if there is no data.
     */
    public static void main(String[] args) {
        TasksManagement tasksManagement = Serialization.load();

        if (tasksManagement == null) {
            tasksManagement = new TasksManagement();
        }

        new GUI(tasksManagement);
    }
}
