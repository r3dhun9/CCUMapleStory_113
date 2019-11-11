package server;

/**
 *
 * @author lrenex
 */
public class ServerConfig {

    private static boolean isLogChat = false;

    public static boolean isLogChat() {
        return isLogChat;
    }

    public static void setLogChat(boolean b) {
        isLogChat = b;
    }

}
