package javagems3d.graphics.environment.shadows;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.JGemsEnvironment;
import javagems3d.graphics.rendering.scene.renderer.IResourceInit;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public abstract class Shadow implements IResourceInit {
    private final IEnvironment environment;
    private Vector2i shadowMapResolution;

    public Shadow(@NotNull IEnvironment environment, @NotNull Vector2i shadowMapResolution) {
        this.environment = environment;
        this.shadowMapResolution = shadowMapResolution;
    }

    public void setShadowMapResolution(@NotNull Vector2i shadowMapResolution) {
        this.shadowMapResolution = shadowMapResolution;
    }

    public Vector2i getShadowMapResolution() {
        return this.shadowMapResolution;
    }

    public IEnvironment getEnvironment() {
        return this.environment;
    }
}
