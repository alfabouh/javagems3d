package javagems3d.system.resources.localisation;

import javagems3d.system.service.files.source.JGemsPathSource;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class JGemsLocalisation {
    private LocalisationManager.Lang currentLang;
    private final LocalisationManager localisationManager;

    public JGemsLocalisation() {
        this.localisationManager = new LocalisationManager();
        this.currentLang = LocalisationManager.ENGLISH;
    }

    public LocalisationManager.Lang getLangByID(int id) {
        return this.localisationManager.getIndexedLanguages().get(id);
    }

    public LocalisationManager.LangMap getLangMapByID(int id) {
        return this.localisationManager.getLanguages().get(this.localisationManager.getIndexedLanguages().get(id));
    }

    public int max() {
        return this.localisationManager.getLanguages().size();
    }

    public void readLanguageMap(LocalisationManager.Lang lang, @NotNull JGemsPathSource path) throws IOException {
        this.localisationManager.readLanguageMap(lang, path);
    }

    public String format(String key, Object... args) {
        return this.localisationManager.format(this.currentLang, key, args);
    }

    public LocalisationManager getLocalisationManager() {
        return this.localisationManager;
    }

    public LocalisationManager.Lang getCurrentLang() {
        return this.currentLang;
    }

    public void setCurrentLang(LocalisationManager.Lang currentLang) {
        this.currentLang = currentLang;
    }
}
