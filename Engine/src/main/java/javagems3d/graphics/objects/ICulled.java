package javagems3d.graphics.objects;

import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.rendering.scene.culling.rules.CullingRules;
import org.jetbrains.annotations.NotNull;

public interface ICulled {
    @NotNull CullingRules getCullingRules();

    CullingAABB getCullingData();

    default boolean isCanBeCulled() {
        return this.getCullingData() != null;
    }
}
