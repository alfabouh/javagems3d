package javagems3d.graphics.environment.lights;

import javagems3d.graphics.objects.IObjectWithLights;
import org.jetbrains.annotations.Nullable;

public interface ILightAttachable {
    default void detach() {
        this.attachTo(null);
    }

    void attachTo(@Nullable IObjectWithLights lighted);
    @Nullable IObjectWithLights getAttachedTo();
    ActionOnDetach getActionOnDeath();

    enum ActionOnDetach {
        DESTROY,
        KEEP_IN_WORLD
    }
}
