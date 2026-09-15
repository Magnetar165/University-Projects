package src.UI;

import javax.swing.*;
import src.model.Post;
import src.model.PostLikesManager;
import src.model.PostStore;
import src.model.ProfileData;
import src.model.SessionStore;
import src.model.User;
import src.model.UserStore;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class QuackstagramProfileUI extends JFrame {

	private static final int WIDTH = 300;
	private static final int HEIGHT = 500;
	private static final int PROFILE_IMAGE_SIZE = 80;
	private static final int GRID_IMAGE_SIZE = WIDTH / 3;
	private static final int NAV_ICON_SIZE = 20;
	private JPanel contentPanel;
	private JPanel headerPanel;
	private JPanel navigationPanel;
	private User currentUser;
	private final UserStore userStore = new UserStore();
	private final SessionStore sessionStore = new SessionStore();
	private final PostStore postStore = new PostStore(); // NEU
	private final PostLikesManager likesManager = new PostLikesManager(); // NEU
	private ProfileData profileData;

	public QuackstagramProfileUI(User user) {
		this.currentUser = user;
		this.profileData = userStore.loadProfile(user.getUsername());

		currentUser.setBio(profileData.getBio());
		currentUser.setFollowersCount(profileData.getFollowerCount());
		currentUser.setFollowingCount(profileData.getFollowingCount());
		currentUser.setPostCount(profileData.getPostCount());

		setTitle("Quackstagram - Profile");
		setSize(WIDTH, HEIGHT);
		setMinimumSize(new Dimension(WIDTH, HEIGHT));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		initializeUI();
	}

	private void initializeUI() {
		getContentPane().removeAll();
		headerPanel = createHeaderPanel();
		navigationPanel = createNavigationPanel();
		contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());

		JPanel profilePanel = createProfilePanel();
		JPanel imageGridPanel = createImageGridPanel();

		contentPanel.add(profilePanel, BorderLayout.NORTH);
		contentPanel.add(imageGridPanel, BorderLayout.CENTER);

		add(headerPanel, BorderLayout.NORTH);
		add(contentPanel, BorderLayout.CENTER);
		add(navigationPanel, BorderLayout.SOUTH);

		revalidate();
		repaint();
	}

	private JPanel createProfilePanel() {
		JPanel profilePanel = new JPanel();
		profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
		profilePanel.setBackground(Color.WHITE);
		profilePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBackground(Color.WHITE);

		JLabel profileImage = new JLabel();
		profileImage.setPreferredSize(new Dimension(PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE));
		profileImage.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		String imagePath = "img/storage/profile/" + currentUser.getUsername() + ".png";
		File imageFile = new File(imagePath);
		if (imageFile.exists()) {
			ImageIcon icon = new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(PROFILE_IMAGE_SIZE,
					PROFILE_IMAGE_SIZE, Image.SCALE_SMOOTH));
			profileImage.setIcon(icon);
		} else {
			profileImage.setIcon(new ImageIcon(new ImageIcon("img/logos/DACS.png").getImage()
					.getScaledInstance(PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE, Image.SCALE_SMOOTH)));
		}

		JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
		statsPanel.setBackground(Color.WHITE);
		statsPanel.add(createStatLabel(String.valueOf(currentUser.getPostsCount()), "Posts"));
		statsPanel.add(createStatLabel(String.valueOf(currentUser.getFollowersCount()), "Followers"));
		statsPanel.add(createStatLabel(String.valueOf(currentUser.getFollowingCount()), "Following"));

		topPanel.add(profileImage, BorderLayout.WEST);
		topPanel.add(statsPanel, BorderLayout.CENTER);

		profilePanel.add(topPanel);
		profilePanel.add(Box.createVerticalStrut(5));

		JLabel nameLabel = new JLabel(currentUser.getUsername());
		nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
		profilePanel.add(nameLabel);

		JLabel bioLabel = new JLabel("<html>" + currentUser.getBio() + "</html>");
		bioLabel.setFont(new Font("Arial", Font.PLAIN, 12));
		profilePanel.add(bioLabel);
		profilePanel.add(Box.createVerticalStrut(10));

		if (profileData.isCurrentUser()) {
			JButton editProfileButton = new JButton("Edit Profile");
			editProfileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			profilePanel.add(editProfileButton);
		} else {
			JButton followButton = new JButton(profileData.isFollowedByLoggedInUser() ? "Following" : "Follow");
			followButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			followButton.addActionListener(e -> {
				userStore.followUser(sessionStore.getLoggedInUsername(), currentUser.getUsername());
				initializeUI();
			});
			profilePanel.add(followButton);
		}

		return profilePanel;
	}

	private JPanel createImageGridPanel() {
		JPanel imageGridPanel = new JPanel(new GridLayout(0, 3, 2, 2));
		imageGridPanel.setBackground(Color.WHITE);

		// Korrektur: Nutzt jetzt den PostStore, um Bilder des Nutzers aus der DB zu laden | Uses yet the PostStore to load images from the user from DB
		List<Post> userPosts = postStore.getPostsByUser(currentUser.getUsername());
		
		for (Post post : userPosts) {
			String imagePath = post.getImagePath();
			ImageIcon imageIcon = new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(GRID_IMAGE_SIZE,
					GRID_IMAGE_SIZE, Image.SCALE_SMOOTH));
			JLabel imageLabel = new JLabel(imageIcon);
			imageLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					displayImage(imagePath);
				}
			});
			imageGridPanel.add(imageLabel);
		}

		JScrollPane scrollPane = new JScrollPane(imageGridPanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());

		JPanel mainGridPanel = new JPanel(new BorderLayout());
		mainGridPanel.add(scrollPane, BorderLayout.CENTER);
		return mainGridPanel;
	}

	private void displayImage(String imagePath) {
		contentPanel.removeAll();
		contentPanel.setLayout(new BorderLayout());

		// Korrektur: Daten aus der DB holen statt aus der Textdatei | Get the data from DB instead of text file
		Post post = postStore.getPostByImagePath(imagePath);
		String username = (post != null) ? post.getUsername() : "Unknown";
		String bio = (post != null) ? post.getCaption() : "";
		String timestampString = (post != null) ? post.getTimestamp() : "";
		int likes = (post != null) ? likesManager.getLikeCount(post.getPostId()) : 0;

		String timeSincePosting = "Unknown";
		if (timestampString != null && !timestampString.isEmpty()) {
			try {
				String formattedTime = timestampString.split("\\.")[0];
				LocalDateTime timestamp = LocalDateTime.parse(formattedTime,
						DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				LocalDateTime now = LocalDateTime.now();
				long days = ChronoUnit.DAYS.between(timestamp, now);
				timeSincePosting = days + " day" + (days != 1 ? "s" : "") + " ago";
			} catch (Exception e) {
				timeSincePosting = "Recently";
			}
		}

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBackground(Color.WHITE);
		JLabel usernameLabel = new JLabel(username);
		JLabel timeLabel = new JLabel(timeSincePosting);
		timeLabel.setHorizontalAlignment(JLabel.RIGHT);
		topPanel.add(usernameLabel, BorderLayout.WEST);
		topPanel.add(timeLabel, BorderLayout.EAST);

		JLabel imageLabel = new JLabel();
		imageLabel.setHorizontalAlignment(JLabel.CENTER);
		try {
			BufferedImage originalImage = ImageIO.read(new File(imagePath));
			imageLabel.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					int w = imageLabel.getWidth();
					int h = imageLabel.getHeight();
					if (w > 0 && h > 0) {
						double scale = Math.min((double) w / originalImage.getWidth(), (double) h / originalImage.getHeight());
						int newW = (int) (originalImage.getWidth() * scale);
						int newH = (int) (originalImage.getHeight() * scale);
						Image scaled = originalImage.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
						imageLabel.setIcon(new ImageIcon(scaled));
					}
				}
			});
		} catch (IOException ex) {
			imageLabel.setText("Image not found");
		}
		

		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setBackground(Color.WHITE);
		JTextArea bioTextArea = new JTextArea(bio);
		bioTextArea.setEditable(false);
		JLabel likesLabel = new JLabel("Likes: " + likes);

		// Comments
		src.model.CommentManager cm = new src.model.CommentManager();
        String owner = (post.getUsername() != null) ? post.getUsername() : post.getUsername();
        
        JPanel commentSection = cm.createCommentSection(post.getPostId(), owner, sessionStore.getLoggedInUsername());
		JPanel southInfoPanel = new JPanel();
        southInfoPanel.setLayout(new BoxLayout(southInfoPanel, BoxLayout.Y_AXIS));
        southInfoPanel.setBackground(Color.WHITE);
        southInfoPanel.add(likesLabel);
        southInfoPanel.add(commentSection);
		//---------

		bottomPanel.add(bioTextArea, BorderLayout.CENTER);
		bottomPanel.add(southInfoPanel, BorderLayout.SOUTH);

		JButton backButton = new JButton("Back");
		backButton.addActionListener(e -> initializeUI());

		contentPanel.add(backButton, BorderLayout.NORTH);
		contentPanel.add(topPanel, BorderLayout.BEFORE_FIRST_LINE); // Pseudo-Header inside content
		
		JPanel container = new JPanel(new BorderLayout());
		container.add(topPanel, BorderLayout.NORTH);
		container.add(imageLabel, BorderLayout.CENTER);
		container.add(bottomPanel, BorderLayout.SOUTH);
		
		contentPanel.add(container, BorderLayout.CENTER);

		revalidate();
		repaint();
	}

	private JPanel createStatLabel(String count, String label) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(Color.WHITE);
		JLabel countLabel = new JLabel(count);
		countLabel.setFont(new Font("Arial", Font.BOLD, 14));
		countLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel textLabel = new JLabel(label);
		textLabel.setFont(new Font("Arial", Font.PLAIN, 12));
		textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(countLabel);
		panel.add(textLabel);
		return panel;
	}

	private JPanel createHeaderPanel() {
		JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		headerPanel.setBackground(new Color(51, 51, 51));
		JLabel lblRegister = new JLabel(" Quackstagram 🐥");
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

		if ("home".equals(buttonType)) {
			button.addActionListener(e -> openHomeUI());
		} else if ("profile".equals(buttonType)) {
			// Already on profile
		} else if ("notification".equals(buttonType)) {
			button.addActionListener(e -> notificationsUI());
		} else if ("explore".equals(buttonType)) {
			button.addActionListener(e -> exploreUI());
		} else if ("add".equals(buttonType)) {
			button.addActionListener(e -> ImageUploadUI());
		}
		return button;
	}

	private void ImageUploadUI() {
		this.dispose();
		ImageUploadUI upload = new ImageUploadUI();
		upload.setVisible(true);
	}

	private void notificationsUI() {
		this.dispose();
		NotificationsUI notificationsUI = new NotificationsUI();
		notificationsUI.setVisible(true);
	}

	private void openHomeUI() {
		this.dispose();
		HomeFeedUI homeUI = new HomeFeedUI();
		homeUI.setVisible(true);
	}

	private void exploreUI() {
		this.dispose();
		ExploreUI explore = new ExploreUI();
		explore.setVisible(true);
	}
}