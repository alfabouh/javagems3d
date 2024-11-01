package javagems3d.graphics.opengl.rendering.items;

import javagems3d.system.resources.assets.models.animation.AnimationData;

public interface IAnimated {
    AnimationData getAnimationData();
    void setAnimationData(AnimationData animationData);
    AnimationData setAnimationByID(int id);

    default void setAnimationDataFrame(int i) {
        this.getAnimationData().setFrame(i);
    }

    default AnimationData initAnimation() {
        return this.setAnimationByID(0);
    }

    default boolean hasAnimations() {
        return this.getAnimationData() != null && this.getAnimationData().isValid();
    }
}
