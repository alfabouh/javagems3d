package javagems3d.system.service.graph;

import org.joml.Vector2i;
import org.joml.Vector3f;

import java.io.Serializable;

public record GraphChunk(Vector2i chunkIJ) implements Serializable {
    public static final int CHUNK_SIZE_XZ = 8;
    private static final long serialVersionUID = -228L;

    public static Vector3f getChunkPos(GraphChunk graphChunk, float y) {
        return new Vector3f(graphChunk.chunkIJ().x * GraphChunk.CHUNK_SIZE_XZ, y, graphChunk.chunkIJ().y * GraphChunk.CHUNK_SIZE_XZ);
    }

    public static GraphChunk getChunkIJByCoordinates(Vector3f vector3f) {
        return new GraphChunk(new Vector2i((int) vector3f.x, (int) vector3f.z).div(GraphChunk.CHUNK_SIZE_XZ));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GraphChunk chunk)) {
            return false;
        }
        return chunk.chunkIJ().equals(this.chunkIJ());
    }

    @Override
    public int hashCode() {
        return this.chunkIJ().hashCode();
    }

    @Override
    public String toString() {
        return this.chunkIJ().toString();
    }
}
