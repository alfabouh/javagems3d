package ru.jgems3d.engine.system.resources.localisation;

import ru.jgems3d.engine.JGems;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.files.JGPath;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Lang {
    private static final Map<String, Lang> values = new HashMap<>();
    public static Lang English = new Lang("English", new JGPath(JGems.Paths.LANG));

    static {
        Lang.values.put(English.getFullName(), Lang.English);
    }

    private final String fullName;
    private JGPath filePath;

    private Lang(String fullName, JGPath filePath) {
        this.fullName = fullName;
        this.filePath = filePath;
    }

    public static boolean checkLangInSet(String fullName) {
        return Lang.values.get(fullName) != null;
    }

    public static Lang defaultLang() {
        return Lang.English;
    }

    public static Lang createLang(String fullName, JGPath filePath) {
        Lang lang = new Lang(fullName, filePath);
        Lang.values.put(fullName, lang);
        JGemsHelper.getLogger().log("Created lang: " + fullName);
        return lang;
    }

    public static void clearLangSet() {
        Lang.values.clear();
        Lang.values.put(Lang.English.getFullName(), Lang.English);
    }

    public static Set<Lang> getAllLanguages() {
        return new HashSet<>(Lang.values.values());
    }

    public static Lang getLangByName(String name) {
        Lang lang = Lang.values.get(name);
        return lang == null ? Lang.defaultLang() : lang;
    }

    public static void setFilePath(Lang lang, JGPath path) {
        Lang lang1 = Lang.values.get(lang.getFullName());
        if (lang1 == null) {
            JGemsHelper.getLogger().warn("Couldn't find language " + lang + " in lang-list!");
            return;
        }
        lang1.setFilePath(path);
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFilePath(JGPath filePath) {
        this.filePath = filePath;
    }

    public JGPath getFilePath() {
        return this.filePath;
    }

    @Override
    public int hashCode() {
        return this.getFullName().toLowerCase().trim().hashCode();
    }
}