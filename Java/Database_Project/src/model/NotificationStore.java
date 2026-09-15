package src.model;
import java.util.ArrayList;
import java.util.List;

import src.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotificationStore {
	

	public List<Notification> findNotificationsFor(String username) {
		List<Notification> result = new ArrayList<>();
		String sql = "SELECT U1.Username as Recipient, U2.Username as Actor, I.ImagePath, N.Timestamp " +
                     "FROM Notification N " +
                     "JOIN User U1 ON N.RecipientUserID = U1.UserID " +
                     "JOIN User U2 ON N.ActorUserID = U2.UserID " +
                     "LEFT JOIN Image I ON N.PostID = I.PostID " + 
                     "WHERE U1.Username = ? " +
                     "ORDER BY N.Timestamp DESC";

		try (Connection connection = DBConnection.getConnection();
             PreparedStatement nfstmt = connection.prepareStatement(sql)) {
			nfstmt.setString(1, username);
			
			try(ResultSet rs = nfstmt.executeQuery()) {
				while (rs.next()) {
					result.add(new Notification(
                        rs.getString("Recipient"),
                        rs.getString("Actor"),
                        rs.getString("ImagePath"), 
                        rs.getString("Timestamp")
                    ));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Could not read notifications", e);
		}

		return result;
	}

	public void addNotification(String recipientUsername, String actorUsername, int postId) {
		String sql = "INSERT INTO Notification (PostID, RecipientUserID, ActorUserID, Timestamp) " +
                 "VALUES (?, " +
                 "(SELECT UserID FROM User WHERE Username = ?), " +
                 "(SELECT UserID FROM User WHERE Username = ?), " +
                 "NOW())";

		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		try (Connection connection = src.DBConnection.getConnection();
        	 PreparedStatement anstmt = connection.prepareStatement(sql)) {
			if (postId == 0) {
    			anstmt.setNull(1, java.sql.Types.INTEGER); 
			} else {
    			anstmt.setInt(1, postId);
			}
			anstmt.setString(2, recipientUsername);
			anstmt.setString(3, actorUsername);

			anstmt.executeUpdate();
		} catch (SQLException e) {
			//throw new RuntimeException("Could not write notification", e);
			e.printStackTrace();
		}
	}
}
