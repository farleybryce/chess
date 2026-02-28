package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import server.RegisterLoginResult;
import server.RegisterRequest;

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
}
