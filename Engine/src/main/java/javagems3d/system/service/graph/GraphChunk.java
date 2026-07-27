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
