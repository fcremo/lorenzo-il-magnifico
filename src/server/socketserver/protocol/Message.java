package server.socketserver.protocol;

public class Message {
    private ProtocolAction action;
    private Object payload;

    public ProtocolAction getAction() {
        return action;
    }

    public void setAction(ProtocolAction action) {
        this.action = action;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}