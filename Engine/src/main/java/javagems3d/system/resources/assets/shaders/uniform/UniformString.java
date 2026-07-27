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

package javagems3d.system.resources.assets.shaders.uniform;

public final class UniformString {
    private final String uniformRoot;
    private final String uniformPostfix;
    private final int uniformArrayIndex;

    public UniformString(String uniformRoot) {
        this(uniformRoot, "", -1);
    }

    public UniformString(String uniformRoot, int uniformArrayIndex) {
        this(uniformRoot, "", uniformArrayIndex);
    }

    public UniformString(String uniformRoot, String uniformPostfix, int uniformArrayIndex) {
        this.uniformRoot = uniformRoot;
        this.uniformPostfix = uniformPostfix;
        this.uniformArrayIndex = uniformArrayIndex;
    }

    public UniformString(DefaultUniformDefinitions uniformRoot) {
        this(uniformRoot.getS(), "", -1);
    }

    public UniformString(DefaultUniformDefinitions uniformRoot, int uniformArrayIndex) {
        this(uniformRoot.getS(), "", uniformArrayIndex);
    }

    public UniformString(DefaultUniformDefinitions uniformRoot, String uniformPostfix, int uniformArrayIndex) {
        this.uniformRoot = uniformRoot.getS();
        this.uniformPostfix = uniformPostfix;
        this.uniformArrayIndex = uniformArrayIndex;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        return this.hashCode() == obj.hashCode();
    }

    @Override
    public String toString() {
        String uniformRoot = this.getUniformRoot();
        int uniformArrayIndex = this.getUniformArrayIndex();
        String uniformPostfix = this.getUniformPostfix();

        if (uniformArrayIndex < 0) {
            return uniformRoot;
        } else {
            return uniformRoot + "[" + uniformArrayIndex + "]" + uniformPostfix;
        }
    }

    private int getUniformArrayIndex() {
        return this.uniformArrayIndex;
    }

    private String getUniformPostfix() {
        return this.uniformPostfix;
    }

    private String getUniformRoot() {
        return this.uniformRoot;
    }
}
