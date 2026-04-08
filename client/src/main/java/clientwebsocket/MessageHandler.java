package clientwebsocket;

import websocket.messages.ServerMessage;

public interface MessageHandler {
    void notify(ServerMessage serverMessage);
}
