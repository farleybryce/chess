package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import server.LoginRequest;
import server.RegisterLoginResult;
import server.RegisterRequest;

import java.util.Objects;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterLoginResult register(RegisterRequest registerRequest) throws DataAccessException {
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            throw new DataAccessException(400, "Error: bad request");
        }
        UserData checkUser = userDAO.getUser(registerRequest.username());
        if (checkUser != null) {
            throw new DataAccessException(403, "Error: already taken");
        } else {
            String hashedPassword = BCrypt.hashpw(registerRequest.password(), BCrypt.gensalt());
            UserData userData = new UserData(registerRequest.username(), hashedPassword, registerRequest.email());
            userDAO.createUser(userData);
            AuthData authData = authDAO.createAuth(userData.username());
            return new RegisterLoginResult(userData.username(), authData.authToken());
        }
    }

    public RegisterLoginResult login(LoginRequest loginRequest) throws DataAccessException {
        if (loginRequest.username() == null || loginRequest.password() == null) {
            throw new DataAccessException(400, "Error: bad request");
        }

        UserData userData = userDAO.getUser(loginRequest.username());

        if (userData == null) {
            throw new DataAccessException(401, "Error: unauthorized");
        }
        if (!BCrypt.checkpw(loginRequest.password(), userData.password())) {
            throw new DataAccessException(401, "Error: unauthorized");
        }
        AuthData authData = authDAO.createAuth(userData.username());
        return new RegisterLoginResult(userData.username(), authData.authToken());
    }

    public void logout(String authToken) throws DataAccessException {
        authDAO.getAuth(authToken);
        authDAO.deleteAuth(authToken);
    }
}
