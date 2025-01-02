package main;

import controller.TaskController;
import model.ReminderScheduler;
import view.TaskManagerCount;

import javax.swing.*;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                TaskManagerCount taskManagerCount = new TaskManagerCount();
                TaskController taskController = new TaskController();
                ReminderScheduler scheduler = new ReminderScheduler(taskController);
                scheduler.scheduleReminders();
                taskManagerCount.setVisible(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
