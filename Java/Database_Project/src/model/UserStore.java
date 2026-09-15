package src.model;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import src.DBConnection;

public class UserStore {

    private final SessionStore sessionStore = new SessionStore();
    private final UserRelationshipManager relationshipManager = new UserRelationshipManager();

    public ProfileData loadProfile(String viewedUsername) {
        String loggedInUsername = sessionStore.getLoggedInUsername();

        int postCount = countPosts(viewedUsername);
        int followerCount = relationshipManager.getFollowers(viewedUsername);
        int followingCount = relationshipManager.getFollowing(viewedUsername);
        String bio = findBio(viewedUsername);

        boolean isCurrentUser = viewedUsername.equals(loggedInUsername);
        boolean followedByLoggedInUser =
                loggedInUsername != null
                && !isCurrentUser
                && isFollowing(loggedInUsername, viewedUsername);

        return new ProfileData(
                viewedUsername,
                bio,
                postCount,
                followerCount,
                followingCount,
                isCurrentUser,
                followedByLoggedInUser
        );
    }

    private int countPosts(String username) {
        String sql = "SELECT COUNT(*) FROM Post WHERE UserID = " +
                     "(SELECT UserID FROM User WHERE Username = ?)";
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

    private String findBio(String username) {
        String sql = "SELECT Bio FROM User WHERE Username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String bio = rs.getString("Bio");
                    return (bio != null) ? bio : "";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Benutzer nicht gefunden";
    }

    public boolean isFollowing(String followerUsername, String followedUsername) {
        return relationshipManager.isAlreadyFollowing(followerUsername, followedUsername);
    }
    
    public void followUser(String followerUsername, String followedUsername) {
        relationshipManager.followUser(followerUsername, followedUsername);
        new NotificationStore().addNotification(followedUsername, followerUsername, 0);
    }

    public boolean userExists(String username) {
        String sql = "SELECT 1 FROM User WHERE Username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Gibt true zurück, wenn der User gefunden wurde | Give true back, if user is found
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }   
        return false;
    }
}