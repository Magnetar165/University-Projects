package src.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.*;
import java.awt.*;

import src.DBConnection;

public class CommentManager {
    private final NotificationStore notificationStore = new NotificationStore();

    public void addComment(String username, int postId, String commentary, String recipient) {
        String sql = "INSERT INTO Comments (PostID, UserID, Commentary) VALUES " +
                     "(?, (SELECT UserID FROM User WHERE Username = ?), ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, postId);
            pstmt.setString(2, username);
            pstmt.setString(3, commentary);
            pstmt.executeUpdate();

            notificationStore.addNotification(recipient, username, postId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Holt alle Kommentare für einen Post inkl. dem Namen des Verfassers.
     * Reply all comments for a Post incl. the name of the commentator.
     */
    public List<String> getCommentsForPost(int postId) {
        List<String> comments = new ArrayList<>();
        String sql = "SELECT U.Username, C.Commentary FROM Comments C " +
                     "JOIN User U ON C.UserID = U.UserID " +
                     "WHERE C.PostID = ? ORDER BY C.CommentID ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, postId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(rs.getString("Username") + ": " + rs.getString("Commentary"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }

    public JPanel createCommentSection(int postId, String postOwner, String currentUser) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

    // Bestehende Kommentare laden | load existent comments

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String comment : getCommentsForPost(postId)) {
            listModel.addElement(comment);
        }
        JList<String> commentList = new JList<>(listModel);
        commentList.setFont(new Font("Arial", Font.PLAIN, 10));
        JScrollPane scrollPane = new JScrollPane(commentList);
        scrollPane.setPreferredSize(new Dimension(280, 60));

    // Eingabebereich für die Kommentare | Code for the input area for comments
        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextField commentField = new JTextField("Add a comment...");
        JButton postButton = new JButton("Post");

        postButton.addActionListener(e -> {
            String text = commentField.getText().trim();
            if (!text.isEmpty()) {
                addComment(currentUser, postId, text, postOwner); // Speichert Kommentar in DB
                listModel.addElement(currentUser + ": " + text); // UI Update
                commentField.setText("");
            }
        });

        inputPanel.add(commentField, BorderLayout.CENTER);
        inputPanel.add(postButton, BorderLayout.EAST);

        container.add(new JLabel("Comments:"));
        container.add(scrollPane);
        container.add(inputPanel);

        return container;
    }
}
