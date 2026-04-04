package server.serverwebsocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import sharedwebsocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        Set<Session> sessions = connections.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet());

        sessions.add(session);
    }

    public void remove(int gameID, Session session) {
        Set<Session> sessions = connections.get(gameID);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                connections.remove(gameID);
            }
        }
    }

    public void broadcast(int gameID, Session excludeSession, ServerMessage serverMessage) throws IOException {
        Set<Session> sessions = connections.get(gameID);
        String msg = new Gson().toJson(serverMessage);
        for (Session c : sessions) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}