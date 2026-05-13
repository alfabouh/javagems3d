package api.application.workbench.resources.data;

import javagems3d.system.external.mapping.tags.base.AxisConstraints;
import javagems3d.system.external.mapping.tags.base.TranslationConstraints;

public enum DefaultMarker {
    CONE(new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.NONE, AxisConstraints.NONE)),
    CURSOR_CONE(new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_Y, AxisConstraints.NONE)),
    POINT(new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.NONE, AxisConstraints.NONE)),
    POINT_DIR(new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ, AxisConstraints.NONE)),
    AABB_ZONE(new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.NONE, AxisConstraints.AXIS_XYZ));

    private final TranslationConstraints translationConstraints;

    DefaultMarker(TranslationConstraints translationConstraints) {
        this.translationConstraints = translationConstraints;
    }

    public TranslationConstraints getTranslationConstraints() {
        return this.translationConstraints;
    }
}