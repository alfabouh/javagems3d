package javagems3d.system.external.gaming.def.world;

import javagems3d.system.external.mapping.tags.base.TranslationConstraints;

public interface IWBenchAssetWithTranslationConstraints {
    IWBenchAssetWithTranslationConstraints setAxisConstraints(TranslationConstraints axisConstraints);
    TranslationConstraints getAxisConstraints();
}
