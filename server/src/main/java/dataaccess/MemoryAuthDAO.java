package dataaccess;

import model.AuthData;

import java.util.HashSet;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {

    final private HashSet<AuthData> authDataHashSet = new HashSet<>();

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData createAuth(String username) throws DataAccessException {
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, username);
        authDataHashSet.add(authData);
        return authData;
    }
}
