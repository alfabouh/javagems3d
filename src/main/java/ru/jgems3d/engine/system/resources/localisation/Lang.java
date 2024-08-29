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

package ru.jgems3d.engine.system.resources.localisation;

import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.service.path.JGemsPath;

import java.util.*;

public final class Lang {
    private static final Map<String, Lang> values = new HashMap<>();
    public static Lang DefaultEnglish = new Lang("English", new JGemsPath(JGems3D.Paths.LANG));

    static {
        Lang.values.put(DefaultEnglish.getFullName(), Lang.DefaultEnglish);
    }

    private final String fullName;
    private JGemsPath fileDirectoryPath;

    private Lang(String fullName, JGemsPath fileDirectoryPath) {
        this.fullName = fullName;
        this.fileDirectoryPath = fileDirectoryPath;
    }

    public static boolean checkLangInSet(String fullName) {
        return Lang.values.get(fullName) != null;
    }

    public static Lang defaultLang() {
        return Lang.DefaultEnglish;
    }

    public static Lang createLang(String fullName, JGemsPath filePath) {
        Lang lang = new Lang(fullName, filePath);
        Lang.values.put(fullName, lang);
        JGemsHelper.getLogger().log("Created lang: " + fullName);
        return lang;
    }

    public static void clearLangSet() {
        Lang.values.clear();
        Lang.values.put(Lang.DefaultEnglish.getFullName(), Lang.DefaultEnglish);
    }

    public static List<Lang> getAllLanguages() {
        return new ArrayList<>(Lang.values.values());
    }

    public static Lang getLangByName(String name) {
        Lang lang = Lang.values.get(name);
        return lang == null ? Lang.defaultLang() : lang;
    }

    public static void setFilePath(Lang lang, JGemsPath path) {
        Lang lang1 = Lang.values.get(lang.getFullName());
        if (lang1 == null) {
            JGemsHelper.getLogger().warn("Couldn't find language " + lang + " in lang-list!");
            return;
        }
        lang1.setFileDirectoryPath(path);
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFileDirectoryPath(JGemsPath fileDirectoryPath) {
        this.fileDirectoryPath = fileDirectoryPath;
    }

    public JGemsPath getFileDirectoryPath() {
        return this.fileDirectoryPath;
    }

    @Override
    public int hashCode() {
        return this.getFullName().toLowerCase().trim().hashCode();
    }
}