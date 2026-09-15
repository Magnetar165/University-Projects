package src.model;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import src.DBConnection;

public class UserRelationshipManager {


    // Method to follow a user
    public void followUser(String followerName, String followingName) {
        String sql = "INSERT INTO Follower (FollowerID, FollowedID) VALUES (" +
                     "(SELECT UserID FROM User WHERE Username = ?), " +
                     "(SELECT UserID FROM User WHERE Username = ?))";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, followerName);
            pstmt.setString(2, followingName);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("Relationship already exist.");
            } else {
                e.printStackTrace();
            }
        }
    }

    // Method to check if a user is already following another user
    boolean isAlreadyFollowing(String followerName, String followingName) {
        String sql = "SELECT 1 FROM Follower " +
                     "WHERE FollowerID = (SELECT UserID FROM User WHERE Username = ?) " +
                     "AND FollowedID = (SELECT UserID FROM User WHERE Username = ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, followerName);
            pstmt.setString(2, followingName);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to get the list of followers for a user
    public int getFollowers(String username) {
        String sql = "SELECT COUNT(*) FROM Follower WHERE FollowedID = " +
                     "(SELECT UserID FROM User WHERE Username = ?)";
        return executeCountQuery(sql, username);
    }

    // Method to get the list of users a user is following
    public int getFollowing(String username) {
        String sql = "SELECT COUNT(*) FROM Follower WHERE FollowerID = " +
                     "(SELECT UserID FROM User WHERE Username = ?)";
        return executeCountQuery(sql, username);
    }

    private int executeCountQuery(String sql, String username) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
