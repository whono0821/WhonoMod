package WhonoMod.integration;

import WhonoMod.integration.waila.WailaCompatModule;

public class IntegrationManager {

    public static void preInit() {

    }

    public static void init() {

        WailaCompatModule.init();
    }

    public static void postInit() {

    }
}
