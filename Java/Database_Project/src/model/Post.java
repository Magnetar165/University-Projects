package src.model;
import java.util.List;
import java.util.ArrayList;

// Represents a Post on Quackstagram
public class Post {
    private final String imagePath;
    private final String caption;
    private int likeCount;
    private final String timestamp;
    private final String username;
    private final int postId;

    public Post(int postId, String imagePath, String caption, String timestamp, String username) {
        this.imagePath = imagePath;
        this.caption = caption;
        this.timestamp = timestamp;
        this.username = username;
        this.postId = postId;
    }

    // Getter methods for picture details
    public String getImagePath() { return imagePath; }
    public String getCaption() { return caption; }
    public int getLikeCount() { return likeCount; }
    public String getTimestamp() { return timestamp; }
    public String getUsername() { return username; }
    public int getPostId() { return postId; }
}
