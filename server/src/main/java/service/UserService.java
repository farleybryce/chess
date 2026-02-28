package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
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
        UserData checkUser = userDAO.getUser(registerRequest.username());
        if (checkUser != null) {
            throw new DataAccessException(403, "Error: already taken");
        } else {
            UserData userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
            userDAO.createUser(userData);
            AuthData authData = authDAO.createAuth(userData.username());
            return new RegisterLoginResult(userData.username(), authData.authToken());
        }
    }

    public RegisterLoginResult login(LoginRequest loginRequest) throws DataAccessException {
        UserData userData = userDAO.getUser(loginRequest.username());
        if (userData == null) {
            throw new DataAccessException(400, "Error: bad request");
        }
        if (!Objects.equals(userData.password(), loginRequest.password())) {
            throw new DataAccessException(401, "Error: unauthorized");
        }
        AuthData authData = authDAO.createAuth(userData.username());
        return new RegisterLoginResult(userData.username(), authData.authToken());
    }

    public void logout(String authToken) {

    }
}
