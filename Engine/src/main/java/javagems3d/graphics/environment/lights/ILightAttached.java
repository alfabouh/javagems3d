package javagems3d.graphics.environment.lights;

import javagems3d.graphics.objects.ILighted;
import org.jetbrains.annotations.Nullable;

public interface ILightAttached {
    default void detach() {
        this.attachTo(null);
    }

    void attachTo(@Nullable ILighted lighted);
    @Nullable ILighted getAttachedTo();
    ActionOnDetach getActionOnDeath();

    enum ActionOnDetach {
        DESTROY,
        KEEP_IN_WORLD
    }
}
