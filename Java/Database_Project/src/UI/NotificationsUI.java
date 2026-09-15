package src.UI;
import javax.swing.*;

import src.DBConnection;
import src.model.Notification;
import src.model.NotificationStore;
import src.model.SessionStore;
import src.model.User;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class NotificationsUI extends JFrame {

	private static final int WIDTH = 300;
	private static final int HEIGHT = 500;
	private static final int NAV_ICON_SIZE = 20; // Size for navigation icons
	private final SessionStore sessionStore = new SessionStore();
	private final NotificationStore interactionStore = new NotificationStore();
	
	public NotificationsUI() {
		setTitle("Notifications");
		setSize(WIDTH, HEIGHT);
		setMinimumSize(new Dimension(WIDTH, HEIGHT));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		initializeUI();
	}

	private void initializeUI() {
		// Reuse the header and navigation panel creation methods from the
		// InstagramProfileUI class
		JPanel headerPanel = createHeaderPanel();
		JPanel navigationPanel = createNavigationPanel();

		// Content Panel for notifications
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane(contentPanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		// Read the current username (now handled via the database-backed SessionStore)
		String currentUsername = sessionStore.getLoggedInUsername();

		// interactionStore fetches data from the Notification table in the DB
		for (Notification item : interactionStore.findNotificationsFor(currentUsername)) {
		   // String notificationMessage =
		   //         item.getActorUsername() + " liked your picture - "
		   //         + getElapsedTime(item.getTimestamp()) + " ago";
			String actor = item.getActorUsername(); 
    		String imagePath = item.getImagePath();
			String actionText;
  
    		if (imagePath == null || imagePath.isEmpty() || imagePath.equals("0")) {
        
        		actionText = "started following you";
    		} else {
        
        		if (isCommentNotification(actor, imagePath)) {
            		actionText = "commented on your picture";
        		} else {
            		actionText = "liked your picture";
        		}
    		}


    		String notificationMessage = actor + " " + actionText + " - " 
                                 + getElapsedTime(item.getTimestamp()) + " ago";

		    JPanel notificationPanel = new JPanel(new BorderLayout());
		    notificationPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

		    JLabel notificationLabel = new JLabel(notificationMessage);
		    notificationPanel.add(notificationLabel, BorderLayout.CENTER);

		    contentPanel.add(notificationPanel);
		}
		// Add panels to frame
		add(headerPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(navigationPanel, BorderLayout.SOUTH);
	}

	private String getElapsedTime(String timestamp) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime timeOfNotification = LocalDateTime.parse(timestamp, formatter);
    LocalDateTime currentTime = LocalDateTime.now();

    long totalMinutes = ChronoUnit.MINUTES.between(timeOfNotification, currentTime);
    long daysBetween = ChronoUnit.DAYS.between(timeOfNotification, currentTime);
    long hoursBetween = ChronoUnit.HOURS.between(timeOfNotification, currentTime) % 24; // Neu hinzugefügt
    long minutesBetween = ChronoUnit.MINUTES.between(timeOfNotification, currentTime) % 60;

    if (totalMinutes < 1) {
        return "less than a minute";
    }
    
    StringBuilder timeElapsed = new StringBuilder();
    if (daysBetween > 0) {
        timeElapsed.append(daysBetween).append(" day").append(daysBetween > 1 ? "s" : "");
    }
    if (hoursBetween > 0) { // Neu hinzugefügt
        if (timeElapsed.length() > 0) {
			timeElapsed.append(", ");
		}
        timeElapsed.append(hoursBetween).append(" hour").append(hoursBetween > 1 ? "s" : "");
    }
    if (minutesBetween > 0) {
        if (timeElapsed.length() > 0) { 
			timeElapsed.append(" and ");
		}
        timeElapsed.append(minutesBetween).append(" minute").append(minutesBetween > 1 ? "s" : "");
    }
    return timeElapsed.toString();
}

	private JPanel createHeaderPanel() {

		// Header Panel (reuse from InstagramProfileUI or customize for home page)
		// Header with the Register label
		JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		headerPanel.setBackground(new Color(51, 51, 51)); // Set a darker background for the header
		JLabel lblRegister = new JLabel(" Notifications 🐥");
		lblRegister.setFont(new Font("Arial", Font.BOLD, 16));
		lblRegister.setForeground(Color.WHITE); // Set the text color to white
		headerPanel.add(lblRegister);
		headerPanel.setPreferredSize(new Dimension(WIDTH, 40)); // Give the header a fixed height
		return headerPanel;
	}

	private JPanel createNavigationPanel() {
		// Create and return the navigation panel
		// Navigation Bar
		JPanel navigationPanel = new JPanel();
		navigationPanel.setBackground(new Color(249, 249, 249));
		navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.X_AXIS));
		navigationPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		navigationPanel.add(createIconButton("img/icons/home.png", "home"));
		navigationPanel.add(Box.createHorizontalGlue());
		navigationPanel.add(createIconButton("img/icons/search.png", "explore"));
		navigationPanel.add(Box.createHorizontalGlue());
		navigationPanel.add(createIconButton("img/icons/add.png", "add"));
		navigationPanel.add(Box.createHorizontalGlue());
		navigationPanel.add(createIconButton("img/icons/heart.png", "notification"));
		navigationPanel.add(Box.createHorizontalGlue());
		navigationPanel.add(createIconButton("img/icons/profile.png", "profile"));

		return navigationPanel;
	}

	private JButton createIconButton(String iconPath, String buttonType) {
		ImageIcon iconOriginal = new ImageIcon(iconPath);
		Image iconScaled = iconOriginal.getImage().getScaledInstance(NAV_ICON_SIZE, NAV_ICON_SIZE, Image.SCALE_SMOOTH);
		JButton button = new JButton(new ImageIcon(iconScaled));
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setContentAreaFilled(false);

		// Define actions based on button type
		if ("home".equals(buttonType)) {
			button.addActionListener(e -> openHomeUI());
		} else if ("profile".equals(buttonType)) {
			button.addActionListener(e -> openProfileUI());
		} else if ("notification".equals(buttonType)) {
			button.addActionListener(e -> notificationsUI());
		} else if ("explore".equals(buttonType)) {
			button.addActionListener(e -> exploreUI());
		} else if ("add".equals(buttonType)) {
			button.addActionListener(e -> ImageUploadUI());
		}
		return button;

	}

	private boolean isCommentNotification(String actorUsername, String imagePath) {
    	String sql = "SELECT 1 FROM Comments C " +
                 	 "JOIN User U ON C.UserID = U.UserID " +
                 	 "JOIN Image I ON C.PostID = I.PostID " +
                 	 "WHERE U.Username = ? AND I.ImagePath = ? LIMIT 1";
    
    	try (Connection conn = DBConnection.getConnection();
         	 PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        	pstmt.setString(1, actorUsername);
        	pstmt.setString(2, imagePath);
        
        	try (ResultSet rs = pstmt.executeQuery()) {
            	return rs.next(); // Gibt 'true' zurück, wenn ein Kommentar existiert
        	}
    	} catch (SQLException e) {
        	e.printStackTrace();
    	}
    	return false; // Fallback auf "like" | Fallback to "like"
}

	private void ImageUploadUI() {
		// Open InstagramProfileUI frame
		this.dispose();
		ImageUploadUI upload = new ImageUploadUI();
		upload.setVisible(true);
	}

	private void openProfileUI() {
		this.dispose();

		String loggedInUsername = sessionStore.getLoggedInUsername();
		User user = new User(loggedInUsername);

		QuackstagramProfileUI profileUI = new QuackstagramProfileUI(user);
		profileUI.setVisible(true);
	}

	private void notificationsUI() {
		// Open InstagramProfileUI frame
		this.dispose();
		NotificationsUI notificationsUI = new NotificationsUI();
		notificationsUI.setVisible(true);
	}

	private void openHomeUI() {
		// Open InstagramProfileUI frame
		this.dispose();
		HomeFeedUI homeUI = new HomeFeedUI();
		homeUI.setVisible(true);
	}

	private void exploreUI() {
		// Open InstagramProfileUI frame
		this.dispose();
		ExploreUI explore = new ExploreUI();
		explore.setVisible(true);
	}
}
