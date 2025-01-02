package controller;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import model.Task;
import model.TaskRepository;

import javax.swing.*;

public class TaskController {
    private final TaskRepository taskRepository;
    public TaskController() throws SQLException {
        taskRepository = new TaskRepository();
    }


    public void addTask(String taskTitle, String dueTimeString, String note) throws SQLException {
        try {
            // Chuyển đổi String thành java.util.Date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date dueTime = sdf.parse(dueTimeString);
            // Chuyển đổi java.util.Date thành java.sql.Timestamp
            Timestamp timestampDueTime = new Timestamp(dueTime.getTime());
            // Gọi phương thức addTask với kiểu java.sql.Timestamp
            taskRepository.addTask(taskTitle, timestampDueTime, note);
        } catch (ParseException e) {
            throw new SQLException("Invalid date format. Please use 'yyyy-MM-dd HH:mm'.", e);
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());  // In ra lỗi SQL để kiểm tra
            throw e;  // Ném lại lỗi để xử lý tiếp
        }
    }


    public void removeTaskById(int id) throws SQLException {
        taskRepository.removeTaskById(id);
    }

    public List<Task> getTasks() throws SQLException {
        return taskRepository.getTasks();
    }
    public List<Task> getTodayTasks() throws SQLException {
        return taskRepository.getTodayTasks();
    }
    public List<Task> getFutureTasks() throws SQLException {
        return taskRepository.getFutureTasks();
    }
    public List<Task> getLastTasks() throws SQLException {
        return taskRepository.getLastTasks();
    }

    public int countAllTasks() throws SQLException{
        return taskRepository.countAllTasks();
    }
    public int countTodayTasks() throws SQLException{
        return taskRepository.countTodayTasks();
    }
    public int countLastTasks() throws SQLException{
        return taskRepository.countLastTasks();
    }
    public int countFutureTasks() throws SQLException{
        return taskRepository.countFutureTasks();
    }

    public List<Task> getScheduledTasks() throws SQLException {
        return taskRepository.getScheduledTasks();
    }
}

