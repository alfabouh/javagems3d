package ru.BouH.engine.render.scene.gui;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.binding.Binding;
import ru.BouH.engine.game.controller.input.Keyboard;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.inventory.Inventory;
import ru.BouH.engine.physics.entities.player.KinematicPlayerSP;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.scene.gui.base.GUI;
import ru.BouH.engine.render.scene.gui.ui.BasicUI;
import ru.BouH.engine.render.scene.gui.ui.ImageUI;
import ru.BouH.engine.render.scene.gui.ui.TextUI;
import ru.BouH.engine.render.scene.world.SceneWorld;
import ru.BouH.engine.render.screen.Screen;

public class InGameGUI implements GUI {
    private boolean isVisible;
    private TextUI fps;
    private TextUI entities;
    private TextUI coordinates;
    private TextUI info1;
    private TextUI speed;
    private TextUI tick;
    private ImageUI crosshair;

    public InGameGUI() {
        this.isVisible = true;
    }

    @Override
    public void onRender(double partialTicks) {
        this.renderTextOnScreen(partialTicks);
        this.renderImagesOnScreen(partialTicks);
    }

    private void renderTextOnScreen(double partialTicks) {
        SceneWorld sceneWorld = Game.getGame().getSceneWorld();
        final WorldItem entityPlayerSP = (WorldItem) Game.getGame().getPlayerSP();
        this.fps.setText("FPS: " + Screen.FPS + " | TPS: " + Screen.PHYS2_TPS);
        this.entities.setText("entities: " + Game.getGame().getPhysicsWorld().countItems());
        this.coordinates.setText(String.format("%s %s %s", (int) entityPlayerSP.getPosition().x, (int) entityPlayerSP.getPosition().y, (int) entityPlayerSP.getPosition().z));

        this.fps.render(partialTicks);
        this.entities.render(partialTicks);
        this.coordinates.render(partialTicks);

        int i1 = 60;
        if (!Keyboard.isPressedKey(GLFW.GLFW_KEY_LEFT_CONTROL)) {
            this.info1.render(partialTicks);
        } else {
            for (Binding keyBinding : Binding.getBindingList()) {
                TextUI textUI = new TextUI(keyBinding.toString(), ResourceManager.renderAssets.standardFont, new Vector3f(0.0f, i1, 0.0f));
                textUI.render(partialTicks);
                textUI.clear();
                i1 += 20;
            }
        }

        if (entityPlayerSP instanceof KinematicPlayerSP) {
            this.speed.setText("speed: " + String.format("%.4f", ((KinematicPlayerSP) entityPlayerSP).getKinematicCharacterController().getLinearVelocity().length()));
            this.speed.setPosition(new Vector3f(0.0f, i1 + 20.0f, 0.0f));
            this.speed.render(partialTicks);
            KinematicPlayerSP kinematicPlayerSP = (KinematicPlayerSP) entityPlayerSP;
            Inventory inventory = kinematicPlayerSP.inventory();
            int j = 0;
            for (Inventory.Slot slot : inventory.getInventorySlots()) {
                TextUI textUI = new TextUI(slot.getInventoryItem() == null ? "null" : slot.getInventoryItem().getName(), ResourceManager.renderAssets.standardFont, inventory.getCurrentSlot() == slot.getId() ? 0xff0000 : 0x00ff00, new Vector3f(0.0f, i1 + 100 + (j++) * 20, 0.0f));
                textUI.render(partialTicks);
                textUI.clear();
            }
        }
        this.tick.setText("tick: " + sceneWorld.getTicks());
        this.tick.setPosition(new Vector3f(0.0f, i1 + 40.0f, 0.0f));
        this.tick.render(partialTicks);
    }

    private void renderImagesOnScreen(double partialTicks) {
        double width = Game.getGame().getScreen().getWidth();
        double height = Game.getGame().getScreen().getHeight();

        Vector2d vector2d = BasicUI.getScaledPictureDimensions(ResourceManager.renderAssets.crosshair, 0.0625f);
        this.crosshair.setSize(new Vector2f((float) vector2d.x, (float) vector2d.y));
        this.crosshair.setPosition(new Vector3f((int) (width / 2.0d) - 8, (int) (height / 2.0d) - 8, 0.0f));
        this.crosshair.render(partialTicks);
    }

    @Override
    public void onStartRender() {
        this.fps = new TextUI(ResourceManager.renderAssets.standardFont);
        this.entities = new TextUI(ResourceManager.renderAssets.standardFont, new Vector3f(0.0f, 20.0f, 0.5f));
        this.coordinates = new TextUI(ResourceManager.renderAssets.standardFont, new Vector3f(0.0f, 40.0f, 0.5f));
        this.info1 = new TextUI("Управление LCTRL", ResourceManager.renderAssets.standardFont, new Vector3f(0.0f, 60.0f, 0.5f));
        this.speed = new TextUI(ResourceManager.renderAssets.standardFont);
        this.tick = new TextUI(ResourceManager.renderAssets.standardFont);

        this.crosshair = new ImageUI(ResourceManager.renderAssets.crosshair, new Vector3f(0.0f), new Vector2f(0.0f));
    }

    @Override
    public void onStopRender() {
        this.fps.clear();
        this.entities.clear();
        this.coordinates.clear();
        this.info1.clear();
        this.speed.clear();
        this.tick.clear();
        this.crosshair.clear();
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    @Override
    public boolean isVisible() {
        return this.isVisible;
    }
}
