package javagems3d.graphics.environment.decals;

import javagems3d.system.resources.assets.texturing.colors.Color3Texture;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record DecalMaterial(@NotNull ImageTexture textureMap, @NotNull Color3Texture diffuseColor, float emissionFactor) {
}
