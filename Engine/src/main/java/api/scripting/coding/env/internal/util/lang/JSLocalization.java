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

package api.scripting.coding.env.internal.util.lang;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.management.JSPath;
import javagems3d.system.resources.localisation.JGemsLocalization;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;

@JSCodingClass(binding = "JSLocalization", description = "Main localisation API for scripts.")
public class JSLocalization {
    @JSHideFromDoc
    private final JGemsLocalization localisation;

    @JSHideFromDoc
    public JSLocalization(JGemsLocalization localisation) {
        this.localisation = localisation;
    }

    @JSCodingFunctionOrMethod(description = "Get total languages count")
    public int size() {
        return this.localisation.max();
    }

    @JSCodingFunctionOrMethod(description = "Get language by index")
    public JSLang getLang(int id) {
        return new JSLang(this.localisation.getLangByID(id));
    }

    @JSCodingFunctionOrMethod(description = "Set current language")
    public void setLang(JSLang lang) {
        this.localisation.setCurrentLang(lang.getRaw());
    }

    @JSCodingFunctionOrMethod(description = "Get current language")
    public JSLang getCurrentLang() {
        return new JSLang(this.localisation.getCurrentLang());
    }

    @JSCodingFunctionOrMethod(description = "Format localized string by key")
    public String format(String key, Object... args) {
        return this.localisation.format(key, args);
    }

    @JSCodingFunctionOrMethod(description = "Load language file from path")
    public void load(JSLang lang, JSPath path) throws Exception {
        this.localisation.readLanguageMap(lang.getRaw(), new JGemsPathSource(path.getJavaPath(), ISource.Source.OUTSIDE_JAR));
    }

    @JSCodingFunctionOrMethod(description = "Real java object")
    public JGemsLocalization getLocalisation() {
        return this.localisation;
    }
}