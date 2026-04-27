package javagems3d.system.external.gaming.def.misc;

import javagems3d.audio.sound.SoundBuffer;
import javagems3d.system.external.gaming.def.IAsset;

public record GameResourceSoundAsset(String name, String relativePath, SoundBuffer soundBuffer) implements IAsset {
}
