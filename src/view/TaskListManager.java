package view;

import model.Task;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TaskListManager extends DefaultListCellRenderer {

        private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm, dd/MM/yyyy");
        private SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        private Border border = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY); // Đường gạch ngang dưới mỗi item
        private Border emptyBorder = new EmptyBorder(5, 5, 5, 5); // Khoảng trống xung quanh nội dung

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Task) {
                Task task = (Task) value;
                try {
                    Date dueTime = inputDateFormat.parse(String.valueOf(task.getDueTime()));
                    String formattedTime = dateFormat.format(dueTime);

                    label.setText("<html>" + task.getTitle() + " ( " + task.getNote() +" ) "+ "<br>" + formattedTime + "</html>");
                } catch (ParseException e) {
                    label.setText("<html>Lỗi định dạng thời gian: " + e.getMessage() + "</html>");
                    e.printStackTrace();
                }
            }

            // Áp dụng border và khoảng trống
            label.setBorder(BorderFactory.createCompoundBorder(border, emptyBorder));

            return label;
        }
    }
