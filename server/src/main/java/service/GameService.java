package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import server.CreateRequest;
import server.CreateResult;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public CreateResult createGame(String authToken, CreateRequest createRequest) throws DataAccessException {
        authDAO.getAuth(authToken);
        int gameID = gameDAO.createGame(createRequest.gameName());
        return new CreateResult(gameID);
    }
}
