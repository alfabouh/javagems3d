package api.application.workbench.resources.data;

import javagems3d.mapping.tags.base.AxisConstraints;
import javagems3d.mapping.tags.base.TranslationConstraints;

public enum DefaultMarker {
    CONE(new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.NULL, AxisConstraints.NULL)),
    CURSOR_CONE(new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_Y, AxisConstraints.NULL)),
    POINT(new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.NULL, AxisConstraints.NULL)),
    AABB_ZONE(new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.NULL, AxisConstraints.AXIS_XYZ));

    private final TranslationConstraints translationConstraints;

    DefaultMarker(TranslationConstraints translationConstraints) {
        this.translationConstraints = translationConstraints;
    }

    public TranslationConstraints getTranslationConstraints() {
        return this.translationConstraints;
    }
}