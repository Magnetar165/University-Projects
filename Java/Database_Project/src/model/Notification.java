package src.model;
public class Notification {
    private final String recipientUsername;
    private final String actorUsername;
    private final String imagePath;
    private final String timestamp;

    public Notification(String recipientUsername, String actorUsername, String imagePath, String timestamp) {
        this.recipientUsername = recipientUsername;
        this.actorUsername = actorUsername;
        this.imagePath = imagePath;
        this.timestamp = timestamp;
    }

    public String getRecipientUsername() {
        return recipientUsername;
    }

    public String getActorUsername() {
        return actorUsername;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getImagePath() { // DAS IST DER ENTSCHEIDENDE GETTER
        return imagePath;
    }
}
