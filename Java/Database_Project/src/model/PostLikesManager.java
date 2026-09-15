package src.model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import src.DBConnection;

public class PostLikesManager {

    

    // Method to like an image
    public void likeImage(String username, int postId) {
        String sql = "INSERT INTO `Likes` (PostID, UserID) VALUES (?, " +
                     "(SELECT UserID FROM User WHERE Username = ?))";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement listmt = connection.prepareStatement(sql)) {
            
            listmt.setInt(1, postId);
            listmt.setString(2, username);
            
            listmt.executeUpdate();
            System.out.println("Post " + postId + " was liked from " + username);
            
        } catch (SQLException e) {
            // Falls der User den Post bereits geliked hat, gibt der zusammengesetzte | If the user already liked the post,
            // Primärschlüssel eine Exception (Duplicate Entry) aus. | the combined primary key give a exemption (Duplicate Entry) back.
            if (e.getErrorCode() == 1062) { // MySQL Error Code für Duplicate Entry
                System.out.println("User liked already this post.");
            } else {
                e.printStackTrace();
            }
        }
    }

    // Method to read likes from file
    public int getLikeCount(int postId) {
        String sql = "SELECT COUNT(*) FROM `Likes` WHERE PostID = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement glcstmt = connection.prepareStatement(sql)) {
            
            glcstmt.setInt(1, postId);
            try (ResultSet rs = glcstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Method to save likes to file
    public boolean hasUserLiked(String username, int postId) {
        String sql = "SELECT 1 FROM `Likes` L " +
                     "JOIN User U ON L.UserID = U.UserID " +
                     "WHERE L.PostID = ? AND U.Username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, postId);
            pstmt.setString(2, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // true, wenn ein Datensatz gefunden wurde | true, if the dataset is found
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
