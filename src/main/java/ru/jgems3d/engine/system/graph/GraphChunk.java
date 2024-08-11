package ru.jgems3d.engine.system.graph;

import org.checkerframework.checker.units.qual.C;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.io.Serializable;

public class GraphChunk implements Serializable {
    private static final long serialVersionUID = -228L;
    public static final int CHUNK_SIZE_XZ = 16;
    private final Vector2i chunkIJ;

    public GraphChunk(Vector2i chunkIJ) {
        this.chunkIJ = chunkIJ;
    }

    public static Vector3f getChunkPos(GraphChunk graphChunk, float y) {
        return new Vector3f(graphChunk.getChunkIJ().x * GraphChunk.CHUNK_SIZE_XZ, y, graphChunk.getChunkIJ().y * GraphChunk.CHUNK_SIZE_XZ);
    }

    public static GraphChunk getChunkIJByCoordinates(Vector3f vector3f) {
        return new GraphChunk(new Vector2i((int) vector3f.x, (int) vector3f.z).div(GraphChunk.CHUNK_SIZE_XZ));
    }

    @Override
    public int hashCode() {
        return this.getChunkIJ().hashCode();
    }

    public Vector2i getChunkIJ() {
        return this.chunkIJ;
    }
}
