package javagems3d.system.external.gaming.def.misc;

import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.system.external.gaming.def.IAsset;

public record GameResourceTextureAsset(String name, String relativePath, ITexture2DProgram texture2DProgram) implements IAsset {
}
