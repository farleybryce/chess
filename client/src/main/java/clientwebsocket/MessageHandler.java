package clientwebsocket;

import sharedwebsocket.messages.ServerMessage;

public interface MessageHandler {
    void notify(ServerMessage serverMessage);
}
