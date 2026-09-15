package src.model;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import src.DBConnection;

public class SessionStore {

    private static String currentUsername = null;

    public String getLoggedInUsername() {
        return currentUsername;
    }

    public static void setLoggedInUser(String username) {
        currentUsername = username;
    }
}
