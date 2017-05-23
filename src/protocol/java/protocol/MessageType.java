package protocol;

public enum MessageType {
    REQUEST("request"), EXCEPTION("exception"), RESPONSE("response"), PUSH("push");

    private String type;

    MessageType(String s) {
        this.type = s;
    }
}