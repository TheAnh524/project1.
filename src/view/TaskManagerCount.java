
package view;

import controller.TaskController;
import model.Task;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class TaskManagerCount extends JFrame {

    private DefaultListModel<Task> taskListModel;
    private JList<Task> taskList;
    private JPanel mainPanel;
    private JLabel todayCountLabel, futureCountLabel, allCountLabel, lastCountLabel;
    private TaskController taskController;

    public TaskManagerCount() {
        setTitle("Quản lý Công việc");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        mainPanel = new JPanel(new CardLayout());

        try {
            taskController = new TaskController();
        } catch (SQLException e) {
            handleError(e, "Lỗi kết nối cơ sở dữ liệu");
            System.exit(1);
            return;
        }

        JPanel homeScreen = createHomeScreen();
        JPanel taskListScreen = createTaskListScreen();

        mainPanel.add(homeScreen, "Home");
        mainPanel.add(taskListScreen, "TaskList");

        add(mainPanel, BorderLayout.CENTER);
        ((CardLayout) mainPanel.getLayout()).show(mainPanel, "Home");

        updateDashboardCounts();
    }

    private JPanel createHomeScreen() {
        JPanel homePanel = new JPanel(new GridLayout(2, 2, 10, 10));
        homePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        todayCountLabel = createStyledLabel("Hôm nay: 0");
        futureCountLabel = createStyledLabel("Sắp tới: 0");
        allCountLabel = createStyledLabel("Tất cả: 0");
        lastCountLabel = createStyledLabel("Đã hoàn thành: 0");

        homePanel.add(createDashboardItem("Hôm nay", todayCountLabel, e -> showTaskList("today")));
        homePanel.add(createDashboardItem("Lịch dự kiến", futureCountLabel, e -> showTaskList("upcoming")));
        homePanel.add(createDashboardItem("Tất cả", allCountLabel, e -> showTaskList("all")));
        homePanel.add(createDashboardItem("Đã hoàn thành", lastCountLabel, e -> showTaskList("completed")));

//        JPanel bottomPanel = createBottomPanel();
//        add(bottomPanel, BorderLayout.SOUTH);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton newReminderButton = new JButton("Lời nhắc mới");
        newReminderButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                new TaskManager(this);
                this.setVisible(false);
            });
        });
        bottomPanel.add(newReminderButton);
        add(bottomPanel, BorderLayout.SOUTH);
        return homePanel;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        return label;
    }

//    private JPanel createBottomPanel() {
//        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        JButton newReminderButton = new JButton("Lời nhắc mới");
//        newReminderButton.addActionListener(e -> {
//            SwingUtilities.invokeLater(() -> {
//                new TaskManager(this);
//                this.setVisible(false);
//            });
//        });
//        bottomPanel.add(newReminderButton);
//        return bottomPanel;
//    }

    private JPanel createDashboardItem(String title, JLabel label, ActionListener listener) {
        JPanel panel = new JPanel(new BorderLayout());
        JButton button = new JButton(title);
        button.addActionListener(listener);
        panel.add(button, BorderLayout.CENTER);
        panel.add(label, BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Thêm viền
        return panel;
    }

    private JPanel createTaskListScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setCellRenderer(new TaskListManager());
        panel.add(new JScrollPane(taskList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton backButton = new JButton("Quay lại");
        backButton.addActionListener(e -> ((CardLayout) mainPanel.getLayout()).show(mainPanel, "Home"));
        buttonPanel.add(backButton);

        JButton removeButton = new JButton("Xóa công việc");
        removeButton.addActionListener(e -> removeTask());
        buttonPanel.add(removeButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void showTaskList(String filter) {
        loadTasks(filter);
        ((CardLayout) mainPanel.getLayout()).show(mainPanel, "TaskList");
    }

    private void loadTasks(String filter) {
        try {
            List<Task> tasks = getTasksByFilter(filter);
            taskListModel.clear();
            updateDashboardCounts();
            if (tasks != null && !tasks.isEmpty()) {
                for (Task task : tasks) {
                    taskListModel.addElement(task);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Không có công việc nào.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            handleError(e, "Lỗi khi tải danh sách công việc");
        }
    }

    private List<Task> getTasksByFilter(String filter) throws SQLException {
        switch (filter) {
            case "today": return taskController.getTodayTasks();
            case "upcoming": return taskController.getFutureTasks();
            case "completed": return taskController.getLastTasks();
            default: return taskController.getTasks();
        }
    }

    private void removeTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            Task selectedTask = taskListModel.getElementAt(selectedIndex);
            try {
                taskController.removeTaskById(selectedTask.getId());
                taskListModel.remove(selectedIndex);
                updateDashboardCounts();
                JOptionPane.showMessageDialog(this, "Công việc đã được xóa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                handleError(ex, "Lỗi khi xóa công việc");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một công việc để xóa.", "Lỗi", JOptionPane.WARNING_MESSAGE);
        }
    }


  public void updateDashboardCounts() {
        try {
            todayCountLabel.setText("Hôm nay: " + taskController.countTodayTasks());
            futureCountLabel.setText("Sắp tới: " + taskController.countFutureTasks());
            allCountLabel.setText("Tất cả: " + taskController.countAllTasks());
            lastCountLabel.setText("Đã hoàn thành: " + taskController.countLastTasks());
        } catch (SQLException e) {
            handleError(e, "Lỗi khi cập nhật số lượng");
        }
    }

    private void handleError(Exception e, String message) {
        JOptionPane.showMessageDialog(this, message + ": " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

}




