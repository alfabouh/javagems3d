package ru.BouH.engine.render.scene.gui;

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
import ru.BouH.engine.render.scene.fabric.render.data.inventory.RenderInventoryItemData;
import ru.BouH.engine.render.scene.gui.base.GUI;
import ru.BouH.engine.render.scene.gui.ui.ImageSizedUI;
import ru.BouH.engine.render.scene.gui.ui.ImageStaticUI;
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
    private TextUI staminaText;
    private TextUI mindText;
    private ImageSizedUI crosshair;
    private ImageStaticUI stamina_over;
    private ImageStaticUI stamina_real;
    private ImageStaticUI mind_over;
    private ImageStaticUI mind_real;

    public InGameGUI() {
        this.isVisible = true;
    }

    @Override
    public void onRender(double partialTicks) {
        this.renderTextOnScreen(partialTicks);
        this.renderImagesOnScreen(partialTicks);
    }

    @Override
    public void onStartRender() {
        this.fps = new TextUI(ResourceManager.renderAssets.standardFont);
        this.entities = new TextUI(ResourceManager.renderAssets.standardFont, new Vector3f(0.0f, 20.0f, 0.5f));
        this.coordinates = new TextUI(ResourceManager.renderAssets.standardFont, new Vector3f(0.0f, 40.0f, 0.5f));

        this.info1 = new TextUI("Управление LCTRL", ResourceManager.renderAssets.standardFont, new Vector3f(0.0f, 60.0f, 0.5f));
        this.staminaText = new TextUI("[Stamina]", ResourceManager.renderAssets.standardFont, new Vector3f(00.0f, 0.0f, 0.5f));
        this.staminaText.setShaderManager(ResourceManager.shaderAssets.gui_noised);

        this.mindText = new TextUI("[Mind]", ResourceManager.renderAssets.standardFont, new Vector3f(00.0f, 0.0f, 0.5f));
        this.mindText.setShaderManager(ResourceManager.shaderAssets.gui_noised);

        this.speed = new TextUI(ResourceManager.renderAssets.standardFont);
        this.tick = new TextUI(ResourceManager.renderAssets.standardFont);

        this.crosshair = new ImageSizedUI(ResourceManager.renderAssets.crosshair, new Vector3f(0.0f), new Vector2f(16.0f));
        this.crosshair.setNormalizeByScreen(true);

        this.stamina_over = new ImageStaticUI(ResourceManager.renderAssets.gui1, new Vector3f(0.0f), new Vector2f(0.0f), new Vector2f(101.0f, 6.0f));
        this.stamina_over.setShaderManager(ResourceManager.shaderAssets.gui_noised);
        this.stamina_over.setScaling(2.0f);
        this.stamina_over.setNormalizeByScreen(true);

        this.stamina_real = new ImageStaticUI(ResourceManager.renderAssets.gui1, new Vector3f(0.0f), new Vector2f(0.0f, 6.0f), new Vector2f(101.0f, 6.0f));
        this.stamina_real.setShaderManager(ResourceManager.shaderAssets.gui_noised);
        this.stamina_real.setScaling(2.0f);
        this.stamina_real.setNormalizeByScreen(true);

        this.mind_over = new ImageStaticUI(ResourceManager.renderAssets.gui1, new Vector3f(0.0f), new Vector2f(0.0f), new Vector2f(101.0f, 6.0f));
        this.mind_over.setShaderManager(ResourceManager.shaderAssets.gui_noised);
        this.mind_over.setScaling(2.0f);
        this.mind_over.setNormalizeByScreen(true);

        this.mind_real = new ImageStaticUI(ResourceManager.renderAssets.gui1, new Vector3f(0.0f), new Vector2f(0.0f, 6.0f), new Vector2f(101.0f, 6.0f));
        this.mind_real.setShaderManager(ResourceManager.shaderAssets.gui_noised);
        this.mind_real.setScaling(2.0f);
        this.mind_real.setNormalizeByScreen(true);
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

    @Override
    public boolean isVisible() {
        return this.isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    private void renderTextOnScreen(double partialTicks) {
        double width = Game.getGame().getScreen().getWidth();
        double height = Game.getGame().getScreen().getHeight();
        SceneWorld sceneWorld = Game.getGame().getSceneWorld();
        final WorldItem entityPlayerSP = (WorldItem) Game.getGame().getPlayerSP();
        this.fps.setText("FPS: " + Screen.FPS + " | TPS: " + Screen.PHYS2_TPS);
        this.entities.setText("entities: " + Game.getGame().getPhysicsWorld().countItems());

        if (Game.getGame().getScreen().getScene().getSceneRender().getCurrentDebugMode() == 1) {
            if (entityPlayerSP instanceof KinematicPlayerSP) {
                KinematicPlayerSP kinematicPlayerSP = (KinematicPlayerSP) entityPlayerSP;
                this.coordinates.setText(String.format("%s %s %s | %s %s %s", (float) entityPlayerSP.getPosition().x, (float) entityPlayerSP.getPosition().y, (float) entityPlayerSP.getPosition().z, (float) kinematicPlayerSP.getCurrentHitScanCoordinate().x, (float) kinematicPlayerSP.getCurrentHitScanCoordinate().y, (float) kinematicPlayerSP.getCurrentHitScanCoordinate().z));
            }
        } else {
            this.coordinates.setText(String.format("%s %s %s", (int) entityPlayerSP.getPosition().x, (int) entityPlayerSP.getPosition().y, (int) entityPlayerSP.getPosition().z));
        }

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
            KinematicPlayerSP kinematicPlayerSP = (KinematicPlayerSP) entityPlayerSP;
            this.speed.setText("speed: " + String.format("%.4f", kinematicPlayerSP.getScalarSpeed()));
            this.speed.setPosition(new Vector3f(0.0f, i1 + 20.0f, 0.0f));
            this.speed.render(partialTicks);
            Inventory inventory = kinematicPlayerSP.inventory();
            int j = 0;
            for (Inventory.Slot slot : inventory.getInventorySlots()) {
                if (slot.getInventoryItem() == null) {
                    continue;
                }
                RenderInventoryItemData renderInventoryItemData = ResourceManager.renderDataAssets.inventoryItemRenderTable.getMap().get(slot.getInventoryItem().getClass());
                ImageSizedUI imageSizedUI = new ImageSizedUI(renderInventoryItemData.getInventoryIcon(), new Vector3f(64.0f + (96.0f) * j++, (float) (height - 96.0d), 0.0f), new Vector2f(96.0f, 96.0f));
                imageSizedUI.render(partialTicks);
                imageSizedUI.clear();

                TextUI textUI = new TextUI("[" + j + "]", ResourceManager.renderAssets.standardFont, inventory.getCurrentSlot() == slot.getId() ? 0xff0000 : 0xffffff, new Vector3f((96.0f) * j, (float) (height - 112.0d), 0.0f));
                textUI.render(partialTicks);
                textUI.clear();
            }
        }
        this.tick.setText("tick: " + sceneWorld.getTicks());
        this.tick.setPosition(new Vector3f(0.0f, i1 + 40.0f, 0.0f));
        this.tick.render(partialTicks);

        this.staminaText.setPosition(new Vector3f(20.0f, (float) (height - 180.0f), 0.0f));
        this.staminaText.render(partialTicks);

        this.mindText.setPosition(new Vector3f(20.0f, (float) (height - 240.0f), 0.0f));
        this.mindText.render(partialTicks);
    }

    private void renderImagesOnScreen(double partialTicks) {
        double width = Game.getGame().getScreen().getWidth();
        double height = Game.getGame().getScreen().getHeight();
        float stamina = ((KinematicPlayerSP) Game.getGame().getPlayerSP()).getStamina();
        float mind = ((KinematicPlayerSP) Game.getGame().getPlayerSP()).getMind();

        this.stamina_over.setPosition(new Vector3f(20.0f, (float) (height - 160.0f), 0.0f));
        this.stamina_over.render(partialTicks);

        this.stamina_real.setTextureWH(new Vector2f(101.0f * stamina, 6.0f));
        this.stamina_real.setPosition(new Vector3f(20.0f, (float) (height - 160.0f), 0.0f));
        this.stamina_real.render(partialTicks);

        this.mind_over.setPosition(new Vector3f(20.0f, (float) (height - 220.0f), 0.0f));
        this.mind_over.render(partialTicks);

        this.mind_real.setTextureWH(new Vector2f(101.0f * mind, 6.0f));
        this.mind_real.setPosition(new Vector3f(20.0f, (float) (height - 220.0f), 0.0f));
        this.mind_real.render(partialTicks);

        this.crosshair.setPosition(new Vector3f((int) (width / 2.0d) - this.crosshair.getSize().x / 2.0f, (int) (height / 2.0d) - this.crosshair.getSize().y / 2.0f, 0.0f));
        this.crosshair.render(partialTicks);
    }
}