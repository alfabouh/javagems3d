package ru.jgems3d.engine.system.settings;

import ru.jgems3d.engine.system.resources.localisation.Localisation;
import ru.jgems3d.engine.system.settings.base.Settings;
import ru.jgems3d.engine.system.settings.objects.SettingChooseLanguage;
import ru.jgems3d.engine.system.settings.objects.SettingFloatBar;
import ru.jgems3d.engine.system.settings.objects.SettingIntSlots;

import java.io.File;

public class JGemsSettings extends Settings {
    public SettingFloatBar soundGain;
    public SettingIntSlots shadowQuality;
    public SettingIntSlots windowMode;
    public SettingIntSlots vSync;
    public SettingIntSlots anisotropic;
    public SettingIntSlots fxaa;
    public SettingIntSlots ssao;
    public SettingIntSlots texturesQuality;
    public SettingIntSlots texturesFiltering;
    public SettingIntSlots bloom;
    public SettingChooseLanguage language;

    public JGemsSettings(File file) {
        super(file);
        this.soundGain = new SettingFloatBar("sound_gain", 1.0f);

        this.windowMode = new SettingIntSlots("windowMode", 0, 0, 1);
        this.shadowQuality = new SettingIntSlots("shadowQuality", 0, 0, 2);
        this.vSync = new SettingIntSlots("vSync", 1, 0, 1);
        this.anisotropic = new SettingIntSlots("anisotropic", 0, 0, 1);
        this.fxaa = new SettingIntSlots("fxaa", 0, 0, 4);
        this.ssao = new SettingIntSlots("ssao", 0, 0, 3);
        this.texturesQuality = new SettingIntSlots("texturesQuality", 0, 0, 2);
        this.bloom = new SettingIntSlots("bloom", 0, 0, 1);
        this.texturesFiltering = new SettingIntSlots("texturesFiltering", 0, 0, 1);

        this.language = new SettingChooseLanguage("lang", Localisation.defaultSystemLang);

        this.soundGain = new SettingFloatBar("sound_gain", 1.0f);

        this.texturesFiltering.addArticle(0, "settings.off", true);
        this.texturesFiltering.addArticle(1, "settings.on", true);

        this.bloom.addArticle(0, "settings.off", true);
        this.bloom.addArticle(1, "settings.on", true);

        this.anisotropic.addArticle(0, "settings.off", true);
        this.anisotropic.addArticle(1, "settings.on", true);

        this.vSync.addArticle(0, "settings.off", true);
        this.vSync.addArticle(1, "settings.on", true);

        this.windowMode.addArticle(0, "settings.fullScreen", true);
        this.windowMode.addArticle(1, "settings.windowed", true);

        this.shadowQuality.addArticle(0, "settings.low", true);
        this.shadowQuality.addArticle(1, "settings.medium", true);
        this.shadowQuality.addArticle(2, "settings.high", true);

        this.texturesQuality.addArticle(0, "settings.low", true);
        this.texturesQuality.addArticle(1, "settings.medium", true);
        this.texturesQuality.addArticle(2, "settings.high", true);

        this.fxaa.addArticle(0, "settings.off", true);
        this.fxaa.addArticle(1, "2x", false);
        this.fxaa.addArticle(2, "4x", false);
        this.fxaa.addArticle(3, "8x", false);
        this.fxaa.addArticle(4, "16x", false);

        this.ssao.addArticle(0, "settings.off", true);
        this.ssao.addArticle(1, "settings.low", true);
        this.ssao.addArticle(2, "settings.medium", true);
        this.ssao.addArticle(3, "settings.high", true);

        this.addSetting(this.ssao);
        this.addSetting(this.windowMode);
        this.addSetting(this.soundGain);
        this.addSetting(this.shadowQuality);
        this.addSetting(this.vSync);
        this.addSetting(this.anisotropic);
        this.addSetting(this.fxaa);
        this.addSetting(this.texturesQuality);
        this.addSetting(this.bloom);
        this.addSetting(this.texturesFiltering);
        this.addSetting(this.language);
    }
}