package src.UI;

import javax.imageio.ImageIO;
import javax.swing.*;

import src.model.CommentManager;
import src.model.NotificationStore;
import src.model.Post;
import src.model.PostLikesManager;
import src.model.PostStore;
import src.model.SessionStore;
import src.model.User;
import src.model.UserStore;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.util.List;

public class HomeFeedUI extends JFrame {
	private static final int WIDTH = 300;
	private static final int HEIGHT = 500;
	private static final int NAV_ICON_SIZE = 20; // Corrected static size for bottom icons
	private static final int IMAGE_WIDTH = WIDTH - 100; // Width for the image posts
	private static final int IMAGE_HEIGHT = 150; // Height for the image posts
	private static final Color LIKE_BUTTON_COLOR = new Color(255, 90, 95); // Color for the like button
	private CardLayout cardLayout;
	private JPanel cardPanel;
	private JPanel homePanel;
	private JPanel imageViewPanel;
	private final SessionStore sessionStore = new SessionStore();
	private final NotificationStore interactionStore = new NotificationStore();
	private final CommentManager commentManager = new CommentManager();
	private final PostStore postStore = new PostStore();
	private final PostLikesManager likeManager = new PostLikesManager();
	private final UserStore userStore = new UserStore();

	public HomeFeedUI() {
		setTitle("Quakstagram Home 3");
		setSize(WIDTH, HEIGHT);
		setMinimumSize(new Dimension(WIDTH, HEIGHT));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);

		homePanel = new JPanel(new BorderLayout());
		imageViewPanel = new JPanel(new BorderLayout());

		initializeUI();

		cardPanel.add(homePanel, "Home");
		cardPanel.add(imageViewPanel, "ImageView");

		add(cardPanel, BorderLayout.CENTER);
		cardLayout.show(cardPanel, "Home"); // Start with the home view

		// Header Panel (reuse from InstagramProfileUI or customize for home page)
		// Header with the Register label
		JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		headerPanel.setBackground(new Color(51, 51, 51)); // Set a darker background for the header
		JLabel lblRegister = new JLabel("🐥 Quackstagram 🐥");
		lblRegister.setFont(new Font("Arial", Font.BOLD, 16));
		lblRegister.setForeground(Color.WHITE); // Set the text color to white
		headerPanel.add(lblRegister);
		headerPanel.setPreferredSize(new Dimension(WIDTH, 40)); // Give the header a fixed height

		add(headerPanel, BorderLayout.NORTH);

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

		add(navigationPanel, BorderLayout.SOUTH);
	}

	private void initializeUI() {

		// Content Scroll Panel
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // Vertical box layout
		JScrollPane scrollPane = new JScrollPane(contentPanel);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // Never allow horizontal scrolling


		String[][] sampleData = createSampleData();
		populateContentPanel(contentPanel, sampleData);
		add(scrollPane, BorderLayout.CENTER);

		// Set up the home panel

		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

		homePanel.add(scrollPane, BorderLayout.CENTER);

	}

	private String[][] createSampleData() {
		String currentUser = sessionStore.getLoggedInUsername();
    
    	// Alle Posts laden (Original-Methode bleibt unverändert!) | Load all posts (Original method stays the same!)
    	List<Post> allPosts = postStore.loadAllPosts();
    
    	// Filtern in Java | Filter in Java
    	List<Post> filteredPosts = new java.util.ArrayList<>();
    	for (Post p : allPosts) {
        // Post anzeigen, wenn es der eigene ist ODER wenn man dem User folgt | Show post, if its the own or from a followed User
        	if (p.getUsername().equals(currentUser) || userStore.isFollowing(currentUser, p.getUsername())) {
            	filteredPosts.add(p);
        }
    }
		//List<Post> posts = postStore.loadAllPosts();
		String[][] data = new String[filteredPosts.size()][5];
		for (int i = 0; i < filteredPosts.size(); i++) {
			Post p = filteredPosts.get(i);
			data[i][0] = p.getUsername();
			data[i][1] = p.getCaption();
			data[i][2] = "Likes: " + likeManager.getLikeCount(p.getPostId());
			data[i][3] = p.getImagePath();
			data[i][4] = String.valueOf(p.getPostId());
		}
		return data;
	}

	private void populateContentPanel(JPanel panel, String[][] sampleData) {

		for (String[] postData : sampleData) {
			
			JPanel itemPanel = new JPanel();
			itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
			itemPanel.setBackground(Color.WHITE); // Set the background color for the item panel
			itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			itemPanel.setAlignmentX(CENTER_ALIGNMENT);
			JLabel nameLabel = new JLabel(postData[0]);
			nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

			final String postIdString = postData[4]; //
            final int postId = Integer.parseInt(postIdString);

			// Crop the image to the fixed size
			JLabel imageLabel = new JLabel();
			imageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			imageLabel.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
			imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add border to image label
			String postIdStr = postData[4];

			try {
				BufferedImage originalImage = ImageIO.read(new File(postData[3]));
				Image iconScaled = originalImage.getScaledInstance(IMAGE_WIDTH,IMAGE_HEIGHT,Image.SCALE_SMOOTH);
				ImageIcon imageIcon = new ImageIcon(iconScaled);
				imageLabel.setIcon(imageIcon);
			} catch (IOException ex) {
				// Handle exception: Image file not found or reading error
				imageLabel.setText("Image not found");
				ex.printStackTrace();
			}

			JLabel descriptionLabel = new JLabel(postData[1]);
			descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

			JLabel likesLabel = new JLabel(postData[2]);
			likesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

			JButton likeButton = new JButton("❤");
			likeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
			likeButton.setBackground(LIKE_BUTTON_COLOR); // Set the background color for the like button
			likeButton.setOpaque(true);
			likeButton.setBorderPainted(false); // Remove border
			likeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					handleLikeAction(postIdStr, likesLabel);
				}
			});

			itemPanel.add(nameLabel);
			itemPanel.add(imageLabel);
			itemPanel.add(descriptionLabel);
			itemPanel.add(likesLabel);
			itemPanel.add(likeButton);

			JPanel commentPanel = new JPanel();
            commentPanel.setLayout(new BoxLayout(commentPanel, BoxLayout.Y_AXIS));
            commentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            commentPanel.setBackground(Color.WHITE);

            // Bestehende Kommentare laden | Load existent comments
            itemPanel.add(commentManager.createCommentSection(postId, postData[0], sessionStore.getLoggedInUsername()));                

			panel.add(itemPanel);

			// Make the image clickable
			imageLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					displayImage(postData); // Call a method to switch to the image view
				}
			});

			// Grey spacing panel
			JPanel spacingPanel = new JPanel();
			spacingPanel.setPreferredSize(new Dimension(WIDTH - 10, 5)); // Set the height for spacing
			spacingPanel.setBackground(new Color(230, 230, 230)); // Grey color for spacing
			panel.add(spacingPanel);
		}
	}

	private void handleLikeAction(String postIdStr, JLabel likesLabel) {
		String currentUser = sessionStore.getLoggedInUsername(); // Holt den aktuell angemeldeten User | Get the actual login user
		int postId = Integer.parseInt(postIdStr);

		if (!likeManager.hasUserLiked(currentUser, postId)) {
			likeManager.likeImage(currentUser, postId); // Speichert Like in DB | Save likes in DB
			
			// UI Update: Live aus der DB | UI Update: Live from DB
			int newLikes = likeManager.getLikeCount(postId);
			likesLabel.setText("Likes: " + newLikes);

			// Benachrichtigung via DB: Ermittelt den Empfänger (Post-Besitzer) aus der DB | Messages via DB: Identified the recipient (Post creator)
			String recipient = "";
			for(Post p : postStore.loadAllPosts()) { 
                if(p.getPostId() == postId) { 
                    recipient = p.getUsername(); 
                    break; 
                } 
            }
            // Minimale Änderung: Aufruf der addNotification Methode | Litte Change: Call of the addNotification Methode
			interactionStore.addNotification(recipient, currentUser, postId); 
		}
	}

	private void displayImage(String[] postData) {
		imageViewPanel.removeAll(); // Clear previous content

		String imageId = postData[4]; // Nutzt die PostID aus dem sampleData Array | Use the PostID of the sampleData Array
		JLabel likesLabel = new JLabel(postData[2]); // Update this line

		// Display the image
		JLabel fullSizeImageLabel = new JLabel();
		fullSizeImageLabel.setHorizontalAlignment(JLabel.CENTER);

		try {
			BufferedImage originalImage = ImageIO.read(new File(postData[3]));
			// Dynamically resizes the image when the components resizes. Could be done
			// better, but this is the Quack way.
			fullSizeImageLabel.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					int w = fullSizeImageLabel.getWidth();
					int h = fullSizeImageLabel.getHeight();

					double scale = Math.min(
							(double) w / originalImage.getWidth(),
							(double) h / originalImage.getHeight());

					int newW = (int) (originalImage.getWidth() * scale);
					int newH = (int) (originalImage.getHeight() * scale);

					Image scaled = originalImage.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
					fullSizeImageLabel.setIcon(new ImageIcon(scaled));
				}
			});
		} catch (IOException ex) {
			// Handle exception: Image file not found or reading error
			fullSizeImageLabel.setText("Image not found");
		}

		// User Info
		JPanel userPanel = new JPanel();
		userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
		JLabel userName = new JLabel(postData[0]);
		userName.setFont(new Font("Arial", Font.BOLD, 18));
		userPanel.add(userName);// User Name

		JButton likeButton = new JButton("❤");
		likeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		likeButton.setBackground(LIKE_BUTTON_COLOR); // Set the background color for the like button
		likeButton.setOpaque(true);
		likeButton.setBorderPainted(false); // Remove border
		likeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleLikeAction(imageId, likesLabel); // Update this line
				refreshDisplayImage(postData, imageId); // Refresh the view
			}
		});

		// Information panel at the bottom
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		infoPanel.add(new JLabel(postData[1])); // Description
		infoPanel.add(new JLabel(postData[2])); // Likes
		infoPanel.add(likeButton);

		imageViewPanel.add(fullSizeImageLabel, BorderLayout.CENTER);
		imageViewPanel.add(infoPanel, BorderLayout.SOUTH);
		imageViewPanel.add(userPanel, BorderLayout.NORTH);

		imageViewPanel.revalidate();
		imageViewPanel.repaint();

		cardLayout.show(cardPanel, "ImageView"); // Switch to the image view
	}

	private void refreshDisplayImage(String[] postData, String imageId) {
		// Liest aktualisierte Likes direkt aus der Datenbank statt aus einer Datei | Reads the updated Likes directly from the DB instead of a text file
		int likes = likeManager.getLikeCount(Integer.parseInt(imageId));
		postData[2] = "Likes: " + likes;

		// Call displayImage with updated postData
		displayImage(postData);
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

	private void ImageUploadUI() {
		// Open InstagramProfileUI frame
		this.dispose();
		ImageUploadUI upload = new ImageUploadUI();
		upload.setVisible(true);
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