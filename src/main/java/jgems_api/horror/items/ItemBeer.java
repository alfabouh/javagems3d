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

package jgems_api.horror.items;

import jgems_api.horror.HorrorGame;
import jgems_api.horror.HorrorGamePlayerState;
import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.audio.sound.data.SoundType;
import ru.jgems3d.engine.graphics.opengl.particles.attributes.ParticleAttributes;
import ru.jgems3d.engine.physics.world.IWorld;
import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine.system.inventory.items.InventoryItem;

public class ItemBeer extends InventoryItem {
    public ItemBeer() {
        super("cross");
    }

    @Override
    public void onLeftClick(IWorld world) {
        HorrorGamePlayerState.runStamina = 1.0f;
        JGemsHelper.getSoundManager().playLocalSound(HorrorGame.get().horrorSoundsLoader.beer, SoundType.BACKGROUND_SOUND, 1.0f, 1.0f);
        this.itemOwner().inventory().consumeItem(this);
    }

    @Override
    public void onRightClick(IWorld world) {

    }

    @Override
    public void onUpdate(IWorld world, boolean isCurrent) {
    }
}
