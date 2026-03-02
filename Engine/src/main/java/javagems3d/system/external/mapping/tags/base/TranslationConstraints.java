package javagems3d.system.external.mapping.tags.base;

import org.jetbrains.annotations.NotNull;

public final class TranslationConstraints {
    private final AxisConstraints positionConstraints;
    private final AxisConstraints rotationConstraints;
    private final AxisConstraints scalingConstraints;

    public TranslationConstraints(@NotNull AxisConstraints positionConstraints, @NotNull AxisConstraints rotationConstraints, @NotNull AxisConstraints scalingConstraints) {
        this.positionConstraints = positionConstraints;
        this.rotationConstraints = rotationConstraints;
        this.scalingConstraints = scalingConstraints;
    }

    public AxisConstraints getPositionConstraints() {
        return this.positionConstraints;
    }

    public AxisConstraints getRotationConstraints() {
        return this.rotationConstraints;
    }

    public AxisConstraints getScalingConstraints() {
        return this.scalingConstraints;
    }
}
