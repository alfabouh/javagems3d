package ru.alfabouh.jgems3d.engine.system.settings;

import ru.alfabouh.jgems3d.engine.system.resources.localisation.Localisation;
import ru.alfabouh.jgems3d.engine.system.settings.objects.SettingChooseLanguage;
import ru.alfabouh.jgems3d.engine.system.settings.objects.SettingFloatBar;
import ru.alfabouh.jgems3d.engine.system.settings.objects.SettingIntSlots;

import java.io.File;

public class JGemsSettings extends Settings {
    public SettingFloatBar soundGain;
    public SettingIntSlots shadowQuality;
    public SettingIntSlots windowMode;
    public SettingIntSlots vSync;
    public SettingIntSlots anisotropic;
    public SettingIntSlots fxaa;
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
        this.texturesQuality = new SettingIntSlots("texturesQuality", 0, 0, 2);
        this.bloom = new SettingIntSlots("bloom", 0, 0, 1);
        this.texturesFiltering = new SettingIntSlots("texturesFiltering", 0, 0, 1);

        this.language = new SettingChooseLanguage("lang", Localisation.defaultLang);

        this.soundGain = new SettingFloatBar("sound_gain", 1.0f);

        this.texturesFiltering.addName(0, "settings.off", true);
        this.texturesFiltering.addName(1, "settings.on", true);

        this.bloom.addName(0, "settings.off", true);
        this.bloom.addName(1, "settings.on", true);

        this.anisotropic.addName(0, "settings.off", true);
        this.anisotropic.addName(1, "settings.on", true);

        this.vSync.addName(0, "settings.off", true);
        this.vSync.addName(1, "settings.on", true);

        this.windowMode.addName(0, "settings.fullScreen", true);
        this.windowMode.addName(1, "settings.windowed", true);

        this.shadowQuality.addName(0, "settings.low", true);
        this.shadowQuality.addName(1, "settings.medium", true);
        this.shadowQuality.addName(2, "settings.high", true);

        this.texturesQuality.addName(0, "settings.low", true);
        this.texturesQuality.addName(1, "settings.medium", true);
        this.texturesQuality.addName(2, "settings.high", true);

        this.fxaa.addName(0, "settings.off", true);
        this.fxaa.addName(1, "2x", false);
        this.fxaa.addName(2, "4x", false);
        this.fxaa.addName(3, "8x", false);
        this.fxaa.addName(4, "16x", false);

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