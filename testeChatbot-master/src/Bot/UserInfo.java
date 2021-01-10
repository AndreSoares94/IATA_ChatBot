package Bot;

public class UserInfo implements java.io.Serializable{
    String chatString;
    String emotion;

    public UserInfo(String chatString, String emotion) {
        this.chatString = chatString;
        this.emotion = emotion;
    }

    public String getChatString() {
        return chatString;
    }

    public String getEmotion() {
        return emotion;
    }
}
