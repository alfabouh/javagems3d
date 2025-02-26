package javagems3d.help;

import javagems3d.JGems3D;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.resources.managing.resources.SystemResources;

public abstract class JGemsResourcesHelper {
    public static SystemResources getGlobalResources() {
        return JGemsResourceManager.getGlobalGameResources();
    }

    public static SystemResources getLocalResources() {
        return JGemsResourceManager.getLocalGameResources();
    }

    public static JGemsResourceManager getJGemsResourceManager() {
        return JGems3D.get().getResourceManager();
    }

    public static void reloadResources() {
        JGems3D.get().reloadResources();
    }
}
