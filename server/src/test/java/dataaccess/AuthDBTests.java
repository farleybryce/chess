package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthDBTests {

    static DBAuthDAO dbAuthDAO;
    AuthData authData;

    @BeforeEach
    public void setUp() throws DataAccessException {
        try {
            dbAuthDAO = new DBAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        dbAuthDAO.clear();
        authData = dbAuthDAO.createAuth("username");
    }

    @AfterAll
    public static void clearAfterDone() throws DataAccessException {
        dbAuthDAO.clear();
    }

    @Test
    public void successfulCreateAuthTest() throws DataAccessException {
        assertEquals("username", authData.username());
        assertNotNull(authData.authToken());
    }

    @Test
    public void failedCreateAuthTest() {
        assertThrows(DataAccessException.class, () -> {
            dbAuthDAO.createAuth("username");
        });
    }

    @Test
    public void successfulGetAuthTest() throws DataAccessException {
        assertEquals(authData, dbAuthDAO.getAuth(authData.authToken()));
    }

    @Test
    public void failedGetAuthTest() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> {
            dbAuthDAO.getAuth("invalidAuthToken");
        });
    }

    @Test
    public void successfulDeleteAuthTest() throws DataAccessException {
        dbAuthDAO.deleteAuth(authData.authToken());
        assertThrows(DataAccessException.class, () -> {
            dbAuthDAO.getAuth(authData.authToken());
        });
    }

    @Test
    public void failedDeleteAuthTest() throws DataAccessException {
        dbAuthDAO.deleteAuth("invalidAuthToken");
        assertEquals(authData, dbAuthDAO.getAuth(authData.authToken()));
    }

    @Test
    public void successfulUserClearTest() throws DataAccessException {
        dbAuthDAO.clear();
        assertThrows(DataAccessException.class, () -> {
            dbAuthDAO.getAuth(authData.authToken());
        });
    }

}
