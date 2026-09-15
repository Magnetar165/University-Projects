package src.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import src.DBConnection;

public class PostStore {
    

    public void saveUploadedPost(String username, String imagePath, String caption) {
        String insertPostSql = "INSERT INTO Post (UserID, Caption, Timestamp) VALUES " +
                               "((SELECT UserID FROM User WHERE Username = ?), ?, NOW())";
        
        String insertImageSql = "INSERT INTO Image (ImagePath, PostID) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Transaktion starten, damit beides oder nichts gespeichert wird | Transaction is started, so that both or nothing is saved.

            // Post speichern | Save post
            int newPostId = -1;
            try (PreparedStatement pstmt = conn.prepareStatement(insertPostSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, username);
                pstmt.setString(2, caption);
                pstmt.executeUpdate();

                // Die von der DB automatisch vergebene PostID abgreifen | Get the PostID, which is automatically generated from the DB
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newPostId = generatedKeys.getInt(1);
                    }
                }
            }

            // Image speichern (verknüpft mit der neuen PostID) | Save Image (connect with the new PostID)
            if (newPostId != -1) {
                try (PreparedStatement pstmt = conn.prepareStatement(insertImageSql)) {
                    pstmt.setString(1, imagePath);
                    pstmt.setInt(2, newPostId);
                    pstmt.executeUpdate();
                }
            }

            conn.commit(); // Alles okay, in DB schreiben | All fine, write in DB
            System.out.println("Post is successfull saved with ID " + newPostId + " .");

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex + 1);
    }

    public List<Post> loadAllPosts() {
        List<Post> posts = new ArrayList<>();
    // JOIN zwischen Post, Image und User, um alle Daten für den Feed zu bekommen | JOIN between post, image and user to get all Data for the feed
    String sql = "SELECT p.PostID, i.ImagePath, p.Caption, p.Timestamp, u.Username " +
                 "FROM Post p " +
                 "JOIN Image i ON p.PostID = i.PostID " +
                 "JOIN User u ON p.UserID = u.UserID " +
                 "ORDER BY p.Timestamp DESC";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        while (rs.next()) {
            posts.add(new Post(
                rs.getInt("PostID"),
                rs.getString("ImagePath"),
                rs.getString("Caption"),
                rs.getString("Timestamp"),
                rs.getString("Username")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return posts;
    }

    public Post getPostByImagePath(String imagePath) {
        String sql = "SELECT p.PostID, i.ImagePath, p.Caption, p.Timestamp, u.Username " +
                     "FROM Post p " +
                     "JOIN Image i ON p.PostID = i.PostID " +
                     "JOIN User u ON p.UserID = u.UserID " +
                     "WHERE i.ImagePath = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, imagePath);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Post(
                        rs.getInt("PostID"),
                        rs.getString("ImagePath"),
                        rs.getString("Caption"),
                        rs.getString("Timestamp"),
                        rs.getString("Username")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Post> getPostsByUser(String username) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.PostID, i.ImagePath, p.Caption, p.Timestamp, u.Username " +
                    "FROM Post p " +
                    "JOIN Image i ON p.PostID = i.PostID " +
                    "JOIN User u ON p.UserID = u.UserID " +
                    "WHERE u.Username = ? " +
                    "ORDER BY p.Timestamp DESC";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    posts.add(new Post(
                        rs.getInt("PostID"),
                        rs.getString("ImagePath"),
                        rs.getString("Caption"),
                        rs.getString("Timestamp"),
                        rs.getString("Username")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }
}