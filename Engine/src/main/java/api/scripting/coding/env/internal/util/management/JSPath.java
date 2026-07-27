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

package api.scripting.coding.env.internal.util.management;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.service.files.JGemsPath;

import java.util.Arrays;

@JSCodingClass(binding = "JSPath", description = "Wrapper for file system paths used in scripting, allowing hierarchical construction and access to Java-side path objects.")
public class JSPath {
    private final JGemsPath jGemsPath;

    @JSCodingConstructor(description = "Create a JSPath from an existing JGemsPath instance.", paramNames = {"path"})
    public JSPath(JGemsPath path) {
        this.jGemsPath = path;
    }

    @JSCodingConstructor(description = "Create a JSPath from a string representing the path.", paramNames = {"path"})
    public JSPath(String path) {
        this.jGemsPath = new JGemsPath(path);
    }

    @JSCodingConstructor(description = "Create a JSPath from a root string and a sequence of folder names.", paramNames = {"root", "foldersTrace"})
    public JSPath(String root, String... foldersTrace) {
        this.jGemsPath = new JGemsPath(root, foldersTrace);
    }

    @JSCodingConstructor(description = "Create a JSPath from another JSPath as root and additional folder names.", paramNames = {"root", "foldersTrace"})
    public JSPath(JSPath root, String... foldersTrace) {
        this.jGemsPath = new JGemsPath(root.getJavaPath(), foldersTrace);
    }

    @JSCodingFunctionOrMethod(description = "Get the underlying Java JGemsPath object.")
    public JGemsPath getJavaPath() {
        return this.jGemsPath;
    }

    @JSHideFromDoc
    @Override
    public String toString() {
        return "JSPath{" +
                "jGemsPath=" + jGemsPath +
                '}';
    }
}