package model;

import view.TaskManagerCount;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.net.URL;

public class SystemTrayNotification {
    private static TrayIcon trayIcon;

    public static void displayNotification(String title, String message) {
        //taskManagerCount = new TaskManagerCount();
        if (!SystemTray.isSupported()) {
            System.err.println("System tray is not supported on this system!");
            return;
        }
        SystemTray tray = SystemTray.getSystemTray();
        // Tải hình ảnh từ đường dẫn hoặc tài nguyên
        Image image = loadImage("/icon.png");
        if (image == null) {
            System.err.println("Notification icon not found. Using default icon.");
            image = Toolkit.getDefaultToolkit().createImage(new byte[0]); // Tạo một biểu tượng trống
        }
        if (trayIcon == null) {
            trayIcon = new TrayIcon(image, "Lời Nhắc");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("Lời Nhắc");

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println("Failed to add tray icon.");
                e.printStackTrace();
                return;
            }
        } else {
            trayIcon.setImage(image); // Cập nhật hình ảnh nếu cần
        }
        trayIcon.displayMessage(title, message, MessageType.INFO);
        //taskManagerCount.updateDashboardCounts();
    }

    private static Image loadImage(String imagePath) {
        try {
            URL imageURL = SystemTrayNotification.class.getResource(imagePath);
            if (imageURL != null) {
                return Toolkit.getDefaultToolkit().getImage(imageURL);
            } else {
                System.err.println("Image resource not found: " + imagePath);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Failed to load image: " + e.getMessage());
            return null;
        }
    }
}

