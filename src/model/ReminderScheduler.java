
package model;


import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import controller.TaskController;

import javax.swing.*;

public class ReminderScheduler {
    private final TaskController taskController;
    private final ScheduledExecutorService scheduler;

    public ReminderScheduler(TaskController taskController) {
        this.taskController = taskController;
        this.scheduler = Executors.newScheduledThreadPool(1); // Tạo thread pool với 1 luồng
    }

    public void scheduleReminders() {
        try {
            List<Task> tasks = taskController.getScheduledTasks();// Lấy công việc đã lên lịch
            for (Task task : tasks) {
                Timestamp reminderTime = task.getDueTime();
                System.out.println("Công việc: " + task.getTitle() + ", Thời gian cần phải thực hiện: " + reminderTime);

                if (reminderTime != null) {
                    long delay = calculateDelay(reminderTime);
                    if (delay > 0) {
                        scheduler.schedule(() -> sendReminder(task.getTitle()), delay, TimeUnit.MILLISECONDS);
                        System.out.println("Lên lịch nhắc nhở cho: " + task.getTitle() + " còn " + delay + " ms nữa.");
                    } else {
                        System.out.println("Công việc có thời gian nhắc nhở trong quá khứ: " + task.getTitle());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lên lịch nhắc nhở: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendReminder(String taskTitle) {
        System.out.println("Đang gửi nhắc nhở cho công việc: " + taskTitle);
        SystemTrayNotification.displayNotification("Nhắc Nhở Công Việc", "Đã đến giờ: " + taskTitle);
    }

    private long calculateDelay(Timestamp reminderTime) {
        long delay = reminderTime.toInstant().toEpochMilli() - Instant.now().toEpochMilli();
        if (delay < 0) {
            System.err.println("Cảnh báo: Thời gian nhắc nhở cho công việc là quá khứ.");
            return 0; // Không lên lịch cho các công việc có thời gian trong quá khứ
        }
        return delay;
    }
}
