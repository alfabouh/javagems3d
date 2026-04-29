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
