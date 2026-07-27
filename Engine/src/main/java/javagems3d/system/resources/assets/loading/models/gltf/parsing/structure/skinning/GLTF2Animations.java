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

package javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.skinning;

import javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.GLTF2Accessor;
import javagems3d.system.service.exceptions.JGemsIOException;

import java.util.ArrayList;
import java.util.List;

public record GLTF2Animations(String name, List<Channel> channels, List<Sampler> samplers) {
    public GLTF2Animations(String name) {
        this(name, new ArrayList<>(), new ArrayList<>());
    }

    public record Channel(Sampler sampler, int targetNode, Path path) {

        public enum Path {
            ROTATION,
            TRANSLATE,
            SCALE,
            WEIGHTS;

            public static Path choose(String path) {
                switch (path) {
                    case "rotation": {
                        return ROTATION;
                    }
                    case "translation": {
                        return TRANSLATE;
                    }
                    case "scale": {
                        return SCALE;
                    }
                    case "weight": {
                        return WEIGHTS;
                    }
                    default: {
                        throw new JGemsIOException("Couldn't find files: " + path);
                    }
                }
            }
        }
    }

    public record Sampler(GLTF2Accessor<Float> timeStamps, GLTF2Accessor<Float> dataOnTime,
                          Interpolation interpolation) {

        public enum Interpolation {
            LINEAR,
            STEP,
            CUBIC_SPLINE;

            public static Interpolation choose(String interpolation) {
                switch (interpolation) {
                    case "STEP": {
                        return STEP;
                    }
                    case "SPLINE": {
                        return CUBIC_SPLINE;
                    }
                    default: {
                        return LINEAR;
                    }
                }
            }
        }
    }
}
