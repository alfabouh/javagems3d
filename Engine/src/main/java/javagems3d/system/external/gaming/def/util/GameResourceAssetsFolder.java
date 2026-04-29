package javagems3d.system.external.gaming.def.util;

import javagems3d.system.external.gaming.def.IAsset;
import javagems3d.system.service.files.VirtualObjectsFolder;
import javagems3d.system.service.files.JGemsPath;
import org.jetbrains.annotations.NotNull;

public class GameResourceAssetsFolder<T extends IAsset> extends VirtualObjectsFolder<T> {
    public GameResourceAssetsFolder(@NotNull JGemsPath name) {
        super(name.fullPath());
    }

    public GameResourceAssetsFolder(@NotNull String name) {
        super(name);
    }
}
