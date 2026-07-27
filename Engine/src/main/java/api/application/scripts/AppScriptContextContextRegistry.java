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

package api.application.scripts;

import java.util.HashSet;
import java.util.Set;

public final class AppScriptContextContextRegistry implements IAppScriptContextRegistry {
    public Set<String> globalGameContextScripts;
    public Set<String> gLocalMapContextScripts;

    public AppScriptContextContextRegistry() {
        this.globalGameContextScripts = new HashSet<>();
        this.gLocalMapContextScripts = new HashSet<>();
    }

    @Override
    public void addGlobalGameContextScript(String packageWithScripts) {
        this.globalGameContextScripts.add(packageWithScripts);
    }

    @Override
    public void addLocalMapContextScript(String packageWithScripts) {
        this.gLocalMapContextScripts.add(packageWithScripts);
    }
}
