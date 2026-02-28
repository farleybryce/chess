package dataaccess;

import model.UserData;
import server.RegisterLoginResult;

public interface UserDAO {
    UserData getUser(String username) throws DataAccessException;

    void createUser(UserData userData) throws DataAccessException;

    void clear() throws DataAccessException;
}
