package javagems3d.system.external.gaming.def.misc.set;

import javagems3d.system.external.gaming.def.misc.GameResourceModelAsset;
import javagems3d.system.external.gaming.def.misc.GameResourceScriptAsset;
import javagems3d.system.external.gaming.def.misc.GameResourceSoundAsset;
import javagems3d.system.external.gaming.def.misc.GameResourceTextureAsset;
import javagems3d.system.external.gaming.def.util.GameResourceAssetsFolder;

public record GameResourcesSet(GameResourceAssetsFolder<GameResourceModelAsset> models, GameResourceAssetsFolder<GameResourceTextureAsset> textures, GameResourceAssetsFolder<GameResourceSoundAsset> sounds) {
}
