package server.socketserver.protocol;

import com.google.gson.Gson;

/**
 * This class implements the actual socket protocol parsing and encoding
 */
public class SocketProtocol {
    private static Gson jsonParser = new Gson();

    public static SocketMessage parseSocketMessage(String s) {
        return null;
    }

    public static byte[] encodeSocketMessage(SocketMessage s) {
        return null;
    }

    public static byte[] loginPlayer(String name) {
        SocketMessage message = new SocketMessage()
                .setType(MessageType.REQUEST)
                .setAction(ProtocolAction.LOGIN)
                .setPayload(name);
        return encodeSocketMessage(message);
    }
}