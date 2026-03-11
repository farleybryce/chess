package dataaccess;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameDBTests {
    static DBGameDAO dbGameDAO;
    int gameID;

    @BeforeEach
    public void setUp() throws DataAccessException {
        try {
            dbGameDAO = new DBGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        dbGameDAO.clear();
        gameID = dbGameDAO.createGame("myGame");
    }

    @AfterAll
    public static void clearAfterDone() throws DataAccessException {
        dbGameDAO.clear();
    }

    @Test
    public void successfulCreateGameTest() {
        assertNotEquals(0, gameID);
    }



}
