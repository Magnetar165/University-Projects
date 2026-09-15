package src.UI;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import src.model.PostStore;
import src.model.SessionStore;
import src.model.User;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ImageUploadUI extends JFrame {

	private static final int WIDTH = 300;
	private static final int HEIGHT = 500;
	private static final int NAV_ICON_SIZE = 20; // Size for navigation icons
	private JLabel imagePreviewLabel;
	private JTextArea bioTextArea;
	private JButton uploadButton;
	private JButton saveButton;
	private boolean imageUploaded = false;
	private File selectedFile;
	private final SessionStore sessionStore = new SessionStore();
	private final PostStore postStore = new PostStore();

	public ImageUploadUI() {
		setTitle("Upload Image");
		setSize(WIDTH, HEIGHT);
		setMinimumSize(new Dimension(WIDTH, HEIGHT));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		initializeUI();
	}

	private void initializeUI() {
		JPanel headerPanel = createHeaderPanel(); // Reuse the createHeaderPanel method
		JPanel navigationPanel = createNavigationPanel(); // Reuse the createNavigationPanel method

		// Main content panel
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

		// Image preview
		imagePreviewLabel = new JLabel();
		imagePreviewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		imagePreviewLabel.setPreferredSize(new Dimension(WIDTH, HEIGHT / 3));
		imagePreviewLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		imagePreviewLabel.setText("No image selected");
		imagePreviewLabel.setHorizontalAlignment(JLabel.CENTER);

		// Caption area
		bioTextArea = new JTextArea("Enter a caption...");
		bioTextArea.setAlignmentX(Component.CENTER_ALIGNMENT);
		bioTextArea.setLineWrap(true);
		bioTextArea.setWrapStyleWord(true);
		JScrollPane bioScrollPane = new JScrollPane(bioTextArea);
		bioScrollPane.setPreferredSize(new Dimension(WIDTH - 50, HEIGHT / 6));

		// Upload button
		uploadButton = new JButton("Upload Image");
		uploadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		uploadButton.addActionListener(this::uploadImage);

		// Save button
		saveButton = new JButton("Save Post");
		saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		saveButton.addActionListener(e -> saveImage());

		contentPanel.add(Box.createVerticalStrut(20));
		contentPanel.add(imagePreviewLabel);
		contentPanel.add(Box.createVerticalStrut(20));
		contentPanel.add(bioScrollPane);
		contentPanel.add(Box.createVerticalStrut(20));
		contentPanel.add(uploadButton);
		contentPanel.add(Box.createVerticalStrut(10));
		contentPanel.add(saveButton);

		add(headerPanel, BorderLayout.NORTH);
		add(contentPanel, BorderLayout.CENTER);
		add(navigationPanel, BorderLayout.SOUTH);
	}

	private void uploadImage(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileChooser.getSelectedFile();
			ImageIcon imageIcon = new ImageIcon(new ImageIcon(selectedFile.getAbsolutePath()).getImage()
					.getScaledInstance(WIDTH, HEIGHT / 3, Image.SCALE_SMOOTH));
			imagePreviewLabel.setIcon(imageIcon);
			imagePreviewLabel.setText(null);
			imageUploaded = true;
		}
	}

	private void saveImage() {
		if (imageUploaded && selectedFile != null) {
			String caption = bioTextArea.getText();
			String username = sessionStore.getLoggedInUsername();
			
			// Erstellt den Zielpfad im img-Ordner | Create the path to the img folder
			String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
			Path destPath = Paths.get("img", "uploaded", fileName);
			
			try {
				// Erstellt das Verzeichnis, falls es nicht existiert | Create the directory, if not existent
				Files.createDirectories(destPath.getParent());
				// Kopiert die physische Datei | Copy the physical file
				Files.copy(selectedFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
				
				// Speichert die Informationen (Post und Image Verknüpfung) in der Datenbank | Save the information (post and image connection) in the DB
				postStore.saveUploadedPost(username, destPath.toString(), caption);
				
				JOptionPane.showMessageDialog(this, "Image saved successfully!");
				openHomeUI();
			} catch (IOException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Error saving image file.");
			}
		} else {
			JOptionPane.showMessageDialog(this, "No image selected.");
		}
	}

	private JPanel createHeaderPanel() {
		JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		headerPanel.setBackground(new Color(51, 51, 51));
		JLabel lblRegister = new JLabel("🐥 Quackstagram 🐥");
		lblRegister.setFont(new Font("Arial", Font.BOLD, 16));
		lblRegister.setForeground(Color.WHITE);
		headerPanel.add(lblRegister);
		headerPanel.setPreferredSize(new Dimension(WIDTH, 40));
		return headerPanel;
	}

	private JPanel createNavigationPanel() {
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
		}
		return button;

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
