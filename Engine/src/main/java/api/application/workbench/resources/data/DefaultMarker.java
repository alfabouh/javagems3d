/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package api.application.workbench.resources.data;

import javagems3d.system.external.mapping.tags.base.AxisConstraints;
import javagems3d.system.external.mapping.tags.base.TranslationConstraints;

public enum DefaultMarker {
    CONE(new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.NONE, AxisConstraints.NONE)),
    CURSOR_CONE(new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_Y, AxisConstraints.NONE)),
    POINT(new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.NONE, AxisConstraints.NONE)),
    POINT_DIR(new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ, AxisConstraints.NONE)),
    AABB_ZONE(new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.NONE, AxisConstraints.AXIS_XYZ)),
    AABB_ZONE_NO_CNSTR(new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ));

    private final TranslationConstraints translationConstraints;

    DefaultMarker(TranslationConstraints translationConstraints) {
        this.translationConstraints = translationConstraints;
    }

    public TranslationConstraints getTranslationConstraints() {
        return this.translationConstraints;
    }
}