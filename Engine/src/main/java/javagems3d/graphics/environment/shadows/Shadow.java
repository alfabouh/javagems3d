package javagems3d.graphics.environment.shadows;

import javagems3d.graphics.environment.Environment;
import javagems3d.graphics.rendering.scene.renderer.IResourceInit;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public abstract class Shadow implements IResourceInit {
    private final Environment environment;
    private Vector2i shadowMapResolution;

    public Shadow(@NotNull Environment environment, @NotNull Vector2i shadowMapResolution) {
        this.environment = environment;
        this.shadowMapResolution = shadowMapResolution;
    }

    public void setShadowMapResolution(@NotNull Vector2i shadowMapResolution) {
        this.shadowMapResolution = shadowMapResolution;
    }

    public Vector2i getShadowMapResolution() {
        return this.shadowMapResolution;
    }

    public Environment getEnvironment() {
        return this.environment;
    }
}
