package WhonoMod.handler;

public class HandlerManager {

    public static EventHandler eventHandler;

    public static void preInit() {

        eventHandler = new EventHandler();
    }
}
