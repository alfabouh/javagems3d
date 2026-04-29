package javagems3d.graphics.rendering.scene.culling.rules;

import javagems3d.system.resources.managing.resources.data.ICopyable;
import org.jetbrains.annotations.NotNull;

public class CullingRules implements ICopyable<CullingRules> {
    private boolean ignoreDistanceCulling;
    private boolean ignoreFrustumCulling;

    public CullingRules(boolean ignoreDistanceCulling, boolean ignoreFrustumCulling) {
        this.ignoreDistanceCulling = ignoreDistanceCulling;
        this.ignoreFrustumCulling = ignoreFrustumCulling;
    }

    public CullingRules() {
        this(false, false);
    }

    public @NotNull static CullingRules get() {
        return new CullingRules();
    }

    public boolean isIgnoreDistanceCulling() {
        return ignoreDistanceCulling;
    }

    public void setIgnoreDistanceCulling(boolean ignoreDistanceCulling) {
        this.ignoreDistanceCulling = ignoreDistanceCulling;
    }

    public boolean isIgnoreFrustumCulling() {
        return ignoreFrustumCulling;
    }

    public void setIgnoreFrustumCulling(boolean ignoreFrustumCulling) {
        this.ignoreFrustumCulling = ignoreFrustumCulling;
    }

    public CullingRules copy() {
        return new CullingRules(this.isIgnoreDistanceCulling(), this.isIgnoreFrustumCulling());
    }
}