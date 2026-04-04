package javagems3d.system.resources.localisation;

import javagems3d.system.service.files.source.JGemsPathSource;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class JGemsLocalisation {
    private LocalizationManager.Lang currentLang;
    private final LocalizationManager localizationManager;

    public JGemsLocalisation() {
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
