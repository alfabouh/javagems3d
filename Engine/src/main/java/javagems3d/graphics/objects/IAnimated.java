package javagems3d.graphics.objects;

import javagems3d.system.resources.assets.models.animation.AnimationData;

@SuppressWarnings("all")
public interface IAnimated {
    AnimationData getAnimationData();
    AnimationData setAnimationByID(int id);
    void setAnimationData(AnimationData animationData);

    default float animationSpeedMultiplier() {
        return 1.0f;
    }

    default void setAnimationDataFrame(int i) {
        this.getAnimationData().setFrame(i);
    }

    default AnimationData initAnimation() {
        return this.setAnimationByID(0);
    }

    default boolean isAnimated() {
        return this.getAnimationData() != null && this.getAnimationData().isValid();
    }
}
