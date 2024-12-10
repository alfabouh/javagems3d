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

package javagems3d.system.resources.assets.shaders.buffers;

public final class ShaderStorageBufferObject {
    private final int binding;
    private final int bufferSize;

    public ShaderStorageBufferObject(int binding) {
        this(binding, -1);
    }

    public ShaderStorageBufferObject(int binding, int bufferSize) {
        this.binding = binding;
        this.bufferSize = bufferSize;
    }

    @Override
    public int hashCode() {
        return this.getBinding();
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public int getBinding() {
        return this.binding;
    }
}