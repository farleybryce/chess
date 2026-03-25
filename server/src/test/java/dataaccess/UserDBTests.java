package dataaccess;

import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import facade.DataAccessException;

import static org.junit.jupiter.api.Assertions.*;

public class UserDBTests {

    static DBUserDAO dbUserDAO;
    UserData userData;

    @BeforeEach
    public void setUp() throws DataAccessException {
        try {
            dbUserDAO = new DBUserDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        dbUserDAO.clear();
        userData = new UserData("username", "password", "test@example.com");
        dbUserDAO.createUser(userData);
    }

    @AfterAll
    public static void clearAfterDone() throws DataAccessException {
        dbUserDAO.clear();
    }

    @Test
    public void successfulCreateUserTest() throws DataAccessException {
        assertEquals(userData, dbUserDAO.getUser(userData.username()));
    }


    @Test
    public void successfulGetUserTest() throws DataAccessException {
        UserData userData1 = new UserData("username1", "password1", "test1@example.com");
        dbUserDAO.createUser(userData1);
        assertEquals(userData, dbUserDAO.getUser(userData.username()));
        assertEquals(userData1, dbUserDAO.getUser("username1"));
    }

    @Test
    public void failedGetUserTest() throws DataAccessException {
        assertNull(dbUserDAO.getUser("notUsername"));
    }

    @Test
    public void successfulUserClearTest() throws DataAccessException {
        dbUserDAO.clear();
        assertNull(dbUserDAO.getUser(userData.username()));
    }
}
