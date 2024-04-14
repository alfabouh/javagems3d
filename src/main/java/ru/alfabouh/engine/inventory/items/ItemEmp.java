package ru.alfabouh.engine.inventory.items;

import ru.alfabouh.engine.audio.sound.GameSound;
import ru.alfabouh.engine.audio.sound.data.SoundType;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.map.Map01;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.math.MathHelper;
import ru.alfabouh.engine.physics.world.IWorld;
import ru.alfabouh.engine.physics.world.object.WorldItem;

public class ItemEmp extends InventoryItem {
    private final GameSound beep;
    private int level;

    public ItemEmp() {
        super("emp");
        this.level = 0;
        this.beep = Game.getGame().getSoundManager().createSound(ResourceManager.soundAssetsLoader.beep, SoundType.BACKGROUND_AMBIENT_SOUND, 0.5f, 0.7f, 1.0f);
        this.setDescription("[Anomaly indicator]");
    }

    @Override
    public void onLeftClick(IWorld world) {
    }

    @Override
    public void onRightClick(IWorld world) {
    }

    @Override
    public void onUpdate(IWorld world, boolean isCurrent) {
        WorldItem worldItem = (WorldItem) this.itemOwner();
        double dist = worldItem.getPosition().distance(Map01.entityManiac.getPosition());
        int maxDist = 60;
        this.level = MathHelper.clamp((int) ((maxDist - dist) / 10.0f), 0, 5);
        if (this.getLevel() > 0 && isCurrent) {
            this.beep.setPitch(this.getLevel() * 0.8f + 0.1f);
            if (!this.beep.isPlaying()) {
                this.beep.playSound();
            }
        } else {
            this.beep.pauseSound();
        }
    }

    public int getLevel() {
        return this.level;
    }
}
