/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.engine.system.graph;

import org.joml.Vector2i;
import org.joml.Vector3f;

import java.io.Serializable;

public class GraphChunk implements Serializable {
    public static final int CHUNK_SIZE_XZ = 8;
    private static final long serialVersionUID = -228L;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GraphChunk)) {
            return false;
        }
        GraphChunk chunk = (GraphChunk) o;
        return chunk.getChunkIJ().equals(this.getChunkIJ());
    }

    @Override
    public int hashCode() {
        return this.getChunkIJ().hashCode();
    }

    public Vector2i getChunkIJ() {
        return this.chunkIJ;
    }

    @Override
    public String toString() {
        return this.getChunkIJ().toString();
    }
}
