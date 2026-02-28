package dataaccess;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException;
}
