package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import server.*;

import java.util.ArrayList;

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

    public void joinGame(String authToken, JoinRequest joinRequest) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        gameDAO.getGame(joinRequest.gameID());
        gameDAO.joinGame(joinRequest.gameID(), joinRequest.playerColor(), authData.username());
    }

    public ListResult listGames(String authToken) throws DataAccessException {
        authDAO.getAuth(authToken);
        ArrayList<ListEntry> listResultArrayList = new ArrayList<>();
        ArrayList<GameData> gameDataArrayList = gameDAO.listGames();
        for (GameData g : gameDataArrayList) {
            listResultArrayList.add(new ListEntry(g.gameID(), g.whiteUsername(), g.blackUsername(), g.gameName()));
        }
        return new ListResult(listResultArrayList);
    }
}
