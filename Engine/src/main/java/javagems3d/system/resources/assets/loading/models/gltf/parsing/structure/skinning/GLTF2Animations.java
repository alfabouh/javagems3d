package javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.skinning;

import javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.GLTF2Accessor;
import javagems3d.system.service.exceptions.JGemsIOException;

import java.util.ArrayList;
import java.util.List;

public final class GLTF2Animations {
    private final String name;
    private final List<Channel> channels;
    private final List<Sampler> samplers;

    public GLTF2Animations(String name) {
        this.name = name;
        this.channels = new ArrayList<>();
        this.samplers = new ArrayList<>();
    }

    public GLTF2Animations(String name, List<Channel> channels, List<Sampler> samplers) {
        this.name = name;
        this.channels = channels;
        this.samplers = samplers;
    }

    public String getName() {
        return this.name;
    }

    public List<Channel> getChannels() {
        return this.channels;
    }

    public List<Sampler> getSamplers() {
        return this.samplers;
    }

    public static class Channel {
        private final Sampler sampler;
        private final int targetNode;
        private final Path path;

        public Channel(Sampler sampler, int targetNode, Path path) {
            this.sampler = sampler;
            this.targetNode = targetNode;
            this.path = path;
        }

        public Sampler getSampler() {
            return this.sampler;
        }

        public int getTargetNode() {
            return this.targetNode;
        }

        public Path getPath() {
            return this.path;
        }

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
                        throw new JGemsIOException("Couldn't find path: " + path);
                    }
                }
            }
        }
    }

    public static class Sampler {
        private final GLTF2Accessor<Float> timeStamps;
        private final GLTF2Accessor<Float> dataOnTime;
        private final Interpolation interpolation;

        public Sampler(GLTF2Accessor<Float> timeStamps, GLTF2Accessor<Float> dataOnTime, Interpolation interpolation) {
            this.timeStamps = timeStamps;
            this.dataOnTime = dataOnTime;
            this.interpolation = interpolation;
        }

        public GLTF2Accessor<Float> getTimeStamps() {
            return this.timeStamps;
        }

        public GLTF2Accessor<Float> getDataOnTime() {
            return this.dataOnTime;
        }

        public Interpolation getInterpolation() {
            return this.interpolation;
        }

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
