package protocol;

/**
 * This class represents a message sent on the socket.
 * It includes the message type and the message payload
 */
public class SocketMessage {
    private MessageType type;
    private Message message;

    /**
     * Create a new SocketMessage
     */
    public SocketMessage() {
        this.message = new Message();
    }

    /**
     * Parse a JSON string into a SocketMessage
     *
     * @param message the JSON representation of the message
     */
    public SocketMessage(String message) {

    }

    public MessageType getType() {
        return type;
    }

    public SocketMessage setType(MessageType type) {
        this.type = type;
        return this;
    }

    public ProtocolAction getAction() {
        return this.message.getAction();
    }

    public SocketMessage setAction(ProtocolAction action) {
        this.message.setAction(action);
        return this;
    }

    public Object getPayload() {
        return this.message.getPayload();
    }

    public SocketMessage setPayload(Object payload) {
        this.message.setPayload(payload);
        return this;
    }
}
