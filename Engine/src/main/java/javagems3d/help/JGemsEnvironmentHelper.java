package javagems3d.help;

import javagems3d.graphics.environment.JGemsEnvironment;
import javagems3d.graphics.environment.fog.FogManager;
import javagems3d.graphics.environment.skybox.SkyBox;

public abstract class JGemsEnvironmentHelper {
    public static SkyBox getSky() {
        return getWorldEnvironment().getSkyBox();
    }

    public static FogManager getFog() {
        return getWorldEnvironment().getFogManager();
    }

    public static JGemsEnvironment getWorldEnvironment() {
        return JGemsCoreHelper.getSceneWorld().getEnvironment();
    }
}
