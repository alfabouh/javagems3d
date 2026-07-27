/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.system.resources.assets.initialization;

import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
import org.lwjgl.openal.AL10;
import javagems3d.JGems3D;
import javagems3d.audio.SoundBuffer;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.files.JGemsPath;

public class SoundAssetsInitializer implements IAssetsInitializer {
    public SoundBuffer zippo_o;
    public SoundBuffer zippo_c;
    public SoundBuffer pick;
    public SoundBuffer button;

    public SoundBuffer[] pl_step;

    @Override
    public void load(SystemResources systemResources) {
        this.pl_step = new SoundBuffer[4];

        this.zippo_o = systemResources.createSoundBuffer(new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SOUNDS, "ui/zippo_o.ogg"), ISource.Source.INSIDE_JAR), AL10.AL_FORMAT_MONO16);
        this.zippo_c = systemResources.createSoundBuffer(new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SOUNDS, "ui/zippo_c.ogg"), ISource.Source.INSIDE_JAR), AL10.AL_FORMAT_MONO16);
        this.pick = systemResources.createSoundBuffer(new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SOUNDS, "player/pick.ogg"), ISource.Source.INSIDE_JAR), AL10.AL_FORMAT_MONO16);
        this.button = systemResources.createSoundBuffer(new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SOUNDS, "player/pick.ogg"), ISource.Source.INSIDE_JAR), AL10.AL_FORMAT_MONO16);

        for (int i = 0; i < 4; i++) {
            this.pl_step[i] = systemResources.createSoundBuffer(new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.SOUNDS, "player/pl_step" + (i + 1) + ".ogg"), ISource.Source.INSIDE_JAR), AL10.AL_FORMAT_STEREO16);
        }
    }

    @Override
    public LaunchMode loadMode() {
        return LaunchMode.ASYNC;
    }

    @Override
    public LoadPriority loadPriority() {
        return LoadPriority.LOW;
    }
}
