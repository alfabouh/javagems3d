package javagems3d.graphics.rendering.programs.textures.cache;

import org.lwjgl.opengl.GL46;

import java.util.HashMap;
import java.util.Map;

public abstract class TexturesSamplersCachingProgram {
    private static Map<Class<?>, SamplersHashTable> tables;

    static {
        TexturesSamplersCachingProgram.tables = new HashMap<>();
    }

    public static int createSamplerId(Class<?> determinantClass, int hashValue) {
        if (!TexturesSamplersCachingProgram.tables.containsKey(determinantClass)) {
            TexturesSamplersCachingProgram.tables.put(determinantClass, new SamplersHashTable());
        }
        SamplersHashTable samplersHashTable = TexturesSamplersCachingProgram.tables.get(determinantClass);
        if (samplersHashTable.getHashTable().containsKey(hashValue)) {
            return samplersHashTable.getHashTable().get(hashValue);
        }
        int samplerId = GL46.glGenSamplers();
        samplersHashTable.getHashTable().put(hashValue, samplerId);
        return samplerId;
    }

    public static void clearAll() {
        for (SamplersHashTable s : TexturesSamplersCachingProgram.tables.values()) {
            for (int i : s.getHashTable().values()) {
                GL46.glDeleteSamplers(i);
            }
        }
    }

    public static class SamplersHashTable {
        private final Map<Integer, Integer> hashTable;

        public SamplersHashTable() {
            this.hashTable = new HashMap<>();
        }

        public Map<Integer, Integer> getHashTable() {
            return this.hashTable;
        }
    }
}
