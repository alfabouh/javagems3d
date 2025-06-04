package javagems3d.system.resources.assets.loading.models.gltf.parsing.structure;

import javagems3d.system.service.exceptions.JGemsNullException;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.util.List;

public final class GLTF2Accessor<T> {
    private final List<T> objects;
    private final GLTF2AccessorData gltf2AccessorData;

    public GLTF2Accessor(List<T> objects, @Nullable GLTF2AccessorData gltf2AccessorData) {
        this.objects = objects;
        this.gltf2AccessorData = gltf2AccessorData;
    }

    public GLTF2AccessorData getGltf2AccessorData() {
        return this.gltf2AccessorData;
    }

    public int size() {
        return this.getObjects().size();
    }

    public List<T> getObjects() {
        return this.objects;
    }

    public enum ValueType {
        SCALAR(1),
        VEC2(2),
        VEC3(3),
        VEC4(4),
        MAT2(4),
        MAT3(9),
        MAT4(16);

        public static int getTypeSize(String type) {
            switch (type) {
                case "SCALAR": {
                    return SCALAR.getCount();
                }
                case "VEC2": {
                    return VEC2.getCount();
                }
                case "VEC3": {
                    return VEC3.getCount();
                }
                case "VEC4": {
                    return VEC4.getCount();
                }
                case "MAT2": {
                    return MAT2.getCount();
                }
                case "MAT3": {
                    return MAT3.getCount();
                }
                case "MAT4": {
                    return MAT4.getCount();
                }
                default: {
                    throw new JGemsNullException("Couldn't create value: " + type);
                }
            }
        }

        private final int count;

        ValueType(int count) {
            this.count = count;
        }

        public int getCount() {
            return this.count;
        }
    }
}
