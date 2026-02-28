package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {

    final private HashMap<String, AuthData> authDataHashMap = new HashMap<>();

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData createAuth(String username) {
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, username);
        authDataHashMap.put(authToken, authData);
        return authData;
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        if (!authDataHashMap.containsKey(authToken)) {
            throw new DataAccessException(401, "Error: unauthorized");
        }
        return authDataHashMap.get(authToken);
    }

    public void deleteAuth(String authToken) {
        authDataHashMap.remove(authToken);
    }
}
