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

package javagems3d.system.resources.localisation;

import javagems3d.system.service.files.source.JGemsPathSource;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class JGemsLocalization {
    private LocalizationManager.Lang currentLang;
    private final LocalizationManager localizationManager;

    public JGemsLocalization() {
        this.localizationManager = new LocalizationManager();
        this.currentLang = LocalizationManager.ENGLISH;
    }

    public LocalizationManager.Lang getLangByID(int id) {
        return this.localizationManager.getIndexedLanguages().get(id);
    }

    public LocalizationManager.LangMap getLangMapByID(int id) {
        return this.localizationManager.getLanguages().get(this.localizationManager.getIndexedLanguages().get(id));
    }

    public int max() {
        return this.localizationManager.getLanguages().size();
    }

    public void readLanguageMap(LocalizationManager.Lang lang, @NotNull JGemsPathSource path) throws IOException {
        this.localizationManager.readLanguageMap(lang, path);
    }

    public String format(String key, Object... args) {
        return this.localizationManager.format(this.currentLang, key, args);
    }

    public LocalizationManager getLocalisationManager() {
        return this.localizationManager;
    }

    public LocalizationManager.Lang getCurrentLang() {
        return this.currentLang;
    }

    public void setCurrentLang(LocalizationManager.Lang currentLang) {
        this.currentLang = currentLang;
    }
}
