
package DataAccess;

import BusinessLogic.TasksManagement;
import java.io.*;

public class Serialization {
    private static final String FILE = "tasks_management";

    /**
     * Saves current TaskManagement object into a file.
     * Allows data to be restored later.
     */
    public static void save(TasksManagement tasksManagement) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE))) {
            oos.writeObject(tasksManagement);
        } catch (Exception e) {
            System.err.println("Error saving");
        }
    }

    /**
     * Loads a previously saved TaskManagement object from the file.
     */
    public static TasksManagement load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE))) {
            return (TasksManagement) ois.readObject();
        } catch (Exception e) {
            System.err.println("Error loading");
            return null;
        }
    }
}
