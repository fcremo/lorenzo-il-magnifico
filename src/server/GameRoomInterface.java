package server;

import server.exceptions.RoomNotJoinableException;

public interface GameRoomInterface {
    boolean isJoinable();

    void addPlayer(ClientConnection clientConnection) throws RoomNotJoinableException;
}
