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
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class ItemCross extends InventoryItem {
    public ItemCross() {
        super("cross");
        this.setDescription(JGems3D.get().I18n("item.description.cross"));
    }

    @Override
    public void onLeftClick(IWorld world) {
        for (int i = 0; i < 16; i++) {
            Vector3f color = new Vector3f(JGems3D.random.nextFloat(), JGems3D.random.nextFloat(), JGems3D.random.nextFloat());
            Vector3f pos = ((WorldItem) this.itemOwner()).getPosition().add(this.ranFloat(), this.ranFloat(), this.ranFloat());
            JGemsHelper.PARTICLES.emitParticle(JGemsHelper.PARTICLES.createSimpleColoredParticle(ParticleAttributes.defaultParticleAttributes(), color, pos, new Vector2f(0.3f)).setMaxLivingSeconds(JGems3D.random.nextFloat() + 1.0f));
        }
        HorrorGamePlayerState.wantsToBeSafe = true;
        JGemsHelper.getSoundManager().playLocalSound(HorrorGame.get().horrorSoundsLoader.magic, SoundType.BACKGROUND_SOUND, 0.5f, 1.0f);
        this.itemOwner().inventory().consumeItem(this);
    }

    private float ranFloat() {
        return (JGems3D.random.nextFloat() - JGems3D.random.nextFloat()) * 1.5f;
    }

    @Override
    public void onRightClick(IWorld world) {

    }

    @Override
    public void onUpdate(IWorld world, boolean isCurrent) {
    }
}
