
package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {
    private Connection connection;

    public TaskRepository() {
        String dbName = "QuanLyCongViec1";
        String dbUser = "sa";
        String dbPassword = "123";

        String url = "jdbc:sqlserver://localhost:1433;databaseName=" + dbName + ";trustServerCertificate=true";

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
        } catch (Exception e) {
            System.err.println("Database connection error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
 //Thêm tasks
    public void addTask(String taskTitle, Timestamp dueTime, String note) throws SQLException {
        String sql = "INSERT INTO tasks (title, dueTime, note) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, taskTitle);
            statement.setTimestamp(2, dueTime);
            statement.setString(3, note);
            statement.executeUpdate();
        }
    }
    public void removeTaskById(int id) throws SQLException {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public void updateTask(int id, String newTask, Timestamp newDueTime, String note) throws SQLException {
        String sql = "UPDATE tasks SET title = ?, dueTime = ?, note = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newTask);
            pstmt.setTimestamp(2, newDueTime);
            pstmt.setString(3, note);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        }
    }
//Lấy ra toàn bộ Tasks
    public List<Task> getTasks() throws SQLException {
        String sql = "SELECT * FROM tasks ORDER BY dueTime ASC";
        List<Task> tasks = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String taskTitle = rs.getString("title");
                Timestamp dueTime = rs.getTimestamp("dueTime");
                String note = rs.getString("note");
                tasks.add(new Task(id, taskTitle, dueTime, note));
            }
        }

        return tasks;
    }


    //Lấy ra task trong hôm nay
    public List<Task> getTodayTasks() throws SQLException {
        String sql = "SELECT * FROM tasks WHERE CAST(dueTime AS DATE) = CAST(GETDATE() AS DATE)";
        List<Task> tasks = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String taskTitle = rs.getString("title");
                Timestamp dueTime = rs.getTimestamp("dueTime");
                String note = rs.getString("note");
                tasks.add(new Task(id, taskTitle, dueTime, note));
            }
        }

        return tasks;
    }
    //Lấy ra task đã hoàn thành
    public List<Task> getLastTasks() throws SQLException {
        String sql = "SELECT * FROM tasks WHERE dueTime < GETDATE()";
        List<Task> tasks = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String taskTitle = rs.getString("title");
                Timestamp dueTime = rs.getTimestamp("dueTime");
                String note = rs.getString("note");
                tasks.add(new Task(id, taskTitle, dueTime, note));
            }
        }

        return tasks;
    }

    //Lấy ra task trong tương lai
    public List<Task> getFutureTasks() throws SQLException {
        String sql = "SELECT * FROM tasks WHERE dueTime > GETDATE()";
        List<Task> tasks = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String taskTitle = rs.getString("title");
                Timestamp dueTime = rs.getTimestamp("dueTime");
                String note = rs.getString("note");
                tasks.add(new Task(id, taskTitle, dueTime, note));
            }
        }

        return tasks;
    }

//Lấy ra danh sách cho vào kế hoạch
    public List<Task> getScheduledTasks() throws SQLException {
        String sql = "SELECT * FROM tasks WHERE dueTime > GETDATE() ORDER BY dueTime ASC";
        List<Task> tasks = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String taskTitle = rs.getString("title");
                Timestamp dueTime = rs.getTimestamp("dueTime");
                String note = rs.getString("note");
                tasks.add(new Task(id, taskTitle, dueTime, note));
            }
        }

        return tasks;
    }

    // Đếm tất cả công việc
    public int countAllTasks() throws SQLException {
        String sql = "SELECT COUNT(*) FROM tasks";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);  // Trả về số lượng công việc
            }
        }
        return 0;
    }

    //Đếm công việc hôm nay
    public int countTodayTasks() throws SQLException {
        String sql = "SELECT COUNT(*) FROM tasks WHERE CONVERT(DATE, dueTime) = CONVERT(DATE, GETDATE())";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);  // Trả về số lượng công việc trong ngày
            }
        }
        return 0;
    }

    //Đếm công việc đã hoàn thành
    public int countLastTasks() throws SQLException {
        String sql = "SELECT COUNT(*) FROM tasks WHERE dueTime < CURRENT_TIMESTAMP;";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);  // Trả về số lượng công việc trong ngày
            }
        }
        return 0;
    }

    //Đếm công việc trong tương lai
    public int countFutureTasks() throws SQLException {
        String sql = "SELECT COUNT(*) FROM tasks WHERE dueTime > CURRENT_TIMESTAMP;";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);  // Trả về số lượng công việc trong ngày
            }
        }
        return 0;
    }

//    public void close() {
//        try {
//            if (connection != null) {
//                connection.close();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
}

