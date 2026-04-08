package clientwebsocket;

import facade.DataAccessException;
import sharedwebsocket.messages.ServerMessage;

public interface MessageHandler {
    void notify(ServerMessage serverMessage);
}
