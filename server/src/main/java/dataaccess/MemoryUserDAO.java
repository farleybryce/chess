package dataaccess;

import model.UserData;
import server.RegisterLoginResult;

import java.util.HashMap;
import java.util.HashSet;

public class MemoryUserDAO implements UserDAO {

    final private HashMap<String, UserData> userDataHashMap = new HashMap<>();

    public UserData getUser(String username) {
        return userDataHashMap.getOrDefault(username, null);
    }

    public void createUser(UserData userData) {
        userDataHashMap.put(userData.username(), userData);
    }
}
