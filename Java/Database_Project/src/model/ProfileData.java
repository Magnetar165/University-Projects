package src.model;

public class ProfileData {
    private final String username;
    private final String bio;
    private final int postCount;
    private final int followerCount;
    private final int followingCount;
    private final boolean currentUser;
    private final boolean followedByLoggedInUser;

    public ProfileData(String username, String bio, int postCount, int followerCount,
                       int followingCount, boolean currentUser, boolean followedByLoggedInUser) {
        this.username = username;
        this.bio = bio;
        this.postCount = postCount;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.currentUser = currentUser;
        this.followedByLoggedInUser = followedByLoggedInUser;
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public int getPostCount() {
        return postCount;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public boolean isCurrentUser() {
        return currentUser;
    }

    public boolean isFollowedByLoggedInUser() {
        return followedByLoggedInUser;
    }
}
