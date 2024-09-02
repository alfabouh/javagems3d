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

package javagems3d.system.resources.assets.shaders;

public class UniformBufferObject {
    private final String id;
    private final int binding;
    private final int bufferSize;

    public UniformBufferObject(String id, int binding, int bufferSize) {
        this.id = id;
        this.binding = binding;
        this.bufferSize = bufferSize;
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public String getId() {
        return this.id;
    }

    public int getBinding() {
        return this.binding;
    }
}