
package view;

import controller.TaskController;
import model.ReminderScheduler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class TaskManager extends JFrame {

    private TaskController taskController;
    private TaskManagerCount parentWindow;

    private JTextField taskField;
    private JTextField noteField;
    private JSpinner timeSpinner;
    private TaskManagerCount taskManagerCount;

    public TaskManager(TaskManagerCount parentWindow) {
        this.parentWindow = parentWindow;
        setTitle("Quản Lý Công Việc");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
        try {
            taskController = new TaskController();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        add(createTopPanel(), BorderLayout.NORTH);
        add(createInputPanel(), BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createTopPanel() {
        JButton backButton = createButton("Quay lại", e -> goBack());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(backButton);
        return topPanel;
    }

    private JPanel createInputPanel() {
        taskField = new JTextField(20);
        noteField = new JTextField(20);
        timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm, dd-MM-yyyy");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setValue(new Date());

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Quan trọng để các trường kéo dãn

        addComponent(inputPanel, new JLabel("Công Việc:"), gbc, 0, 0);
        addComponent(inputPanel, taskField, gbc, 1, 0);
        addComponent(inputPanel, new JLabel("Thời Gian:"), gbc, 0, 1);
        addComponent(inputPanel, timeSpinner, gbc, 1, 1);
        addComponent(inputPanel, new JLabel("Ghi Chú:"), gbc, 0, 2);
        addComponent(inputPanel, noteField, gbc, 1, 2);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(createButton("Thêm", e -> addTask()));

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2; // Chiếm 2 cột
        inputPanel.add(buttonPanel, gbc);

        return inputPanel;
    }

    private void addComponent(JPanel panel, Component component, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(component, gbc);
    }

    private JButton createButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        return button;
    }

    private void goBack() {
        this.dispose();
        parentWindow.setVisible(true);
        parentWindow.updateDashboardCounts();
    }

    private void addTask() {
        String note = noteField.getText().trim();
        String taskTitle = taskField.getText().trim();
        String dueTimeString = getFormattedDueTime();
        if (taskTitle.isEmpty()) {
            showMessage("task.title.empty", "error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (dueTimeString == null) {
            showMessage("task.dueTime.invalid", "warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            taskController.addTask(taskTitle, dueTimeString, note);
            ReminderScheduler scheduler = new ReminderScheduler(taskController); // Tạo mới scheduler
            scheduler.scheduleReminders();
            clearInputFields();
            showMessage("task.add.success", "info", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            showMessage("task.add.failed", "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getFormattedDueTime() {
        Object value = timeSpinner.getValue();
        if (value instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return sdf.format((Date) value);
        }
        return null;
    }

    private void clearInputFields() {
        taskField.setText("");
        noteField.setText("");
        timeSpinner.setValue(new Date()); // Reset về thời gian hiện tại
    }


    private void showMessage(String messageKey, String titleKey, int messageType) {
        ResourceBundle messages = ResourceBundle.getBundle("messages");
        String message = messages.getString(messageKey);
        String title = messages.getString(titleKey);
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}