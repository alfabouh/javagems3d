package ru.alfabouh.engine.render.scene.gui;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.controller.binding.Binding;
import ru.alfabouh.engine.game.controller.input.Keyboard;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.inventory.Inventory;
import ru.alfabouh.engine.physics.entities.player.KinematicPlayerSP;
import ru.alfabouh.engine.physics.entities.prop.PhysPlank;
import ru.alfabouh.engine.physics.world.object.WorldItem;
import ru.alfabouh.engine.render.scene.fabric.render.data.inventory.RenderInventoryItemData;
import ru.alfabouh.engine.render.scene.gui.base.GUI;
import ru.alfabouh.engine.render.scene.gui.ui.*;
import ru.alfabouh.engine.render.scene.world.SceneWorld;
import ru.alfabouh.engine.render.screen.Screen;

public class InGameGUI extends AbstractGUI {
    public static boolean show_wasd = true;
    public static boolean show_shift = true;

    private boolean isVisible;
    private TextUI fps;
    private TextUI entities;
    private TextUI coordinates;
    private TextUI info1;
    private TextUI speed;
    private TextUI tick;
    private TextUI staminaText;
    private TextUI mindText;
    private TextUI itemDescText;
    private TextUI cdText;
    private TextUI cassetteText;
    private ImageSizedUI crosshair;
    private ImageStaticUI stamina_over;
    private ImageStaticUI stamina_real;
    private ImageStaticUI mind_over;
    private ImageStaticUI mind_real;

    public InGameGUI() {
        super(null);
        this.isVisible = true;
    }

    @Override
    public void onRender(double partialTicks) {
        this.renderTextOnScreen(partialTicks);
        this.renderImagesOnScreen(partialTicks);
    }

    @Override
    public void onStartRender() {
        this.cdText = new TextUI(ResourceManager.renderAssets.standardFont);
        this.cassetteText = new TextUI(ResourceManager.renderAssets.standardFont);

        this.fps = new TextUI(ResourceManager.renderAssets.standardFont);
        this.itemDescText = new TextUI(ResourceManager.renderAssets.standardFont, new Vector3f(80.0f, 80.0f, 0.5f));
        this.entities = new TextUI(ResourceManager.renderAssets.standardFont, new Vector3f(0.0f, 20.0f, 0.5f));
        this.coordinates = new TextUI(ResourceManager.renderAssets.standardFont, new Vector3f(0.0f, 40.0f, 0.5f));

        this.info1 = new TextUI("Управление LCTRL", ResourceManager.renderAssets.standardFont, new Vector3f(0.0f, 60.0f, 0.5f));
        this.staminaText = new TextUI("[Stamina]", ResourceManager.renderAssets.standardFont, new Vector3f(0.0f, 0.0f, 0.5f));
        this.staminaText.setShaderManager(ResourceManager.shaderAssets.gui_noised);

        this.mindText = new TextUI("[Mind]", ResourceManager.renderAssets.standardFont, new Vector3f(0.0f, 0.0f, 0.5f));
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

        if (InGameGUI.show_wasd) {
            if (Game.getGame().getScreen().getControllerDispatcher().getCurrentController().getNormalizedPositionInput().mul(1, 0, 1).length() > 0) {
                InGameGUI.show_wasd = false;
            }

            TextUI textUI = new TextUI("[W A S D] MOVE", ResourceManager.renderAssets.standardFont, new Vector3f(0.0f));
            textUI.setPosition(new Vector3f((float) (width / 2.0f - textUI.getTextWidth() / 2.0f), (float) (height / 2.0f - textUI.getTextHeight() / 2.0f - 100.0f), 0.5f));
            textUI.render(partialTicks);
            textUI.clear();
        }

        if (InGameGUI.show_shift) {
            if (Game.getGame().getScreen().getControllerDispatcher().getCurrentController().getNormalizedPositionInput().y < 0) {
                InGameGUI.show_shift = false;
            }

            TextUI textUI = new TextUI("[LEFT SHIFT] RUN", ResourceManager.renderAssets.standardFont, new Vector3f(0.0f));
            textUI.setPosition(new Vector3f((float) (width / 2.0f - textUI.getTextWidth() / 2.0f), (float) (height / 2.0f - textUI.getTextHeight() / 2.0f - 130.0f), 0.5f));
            textUI.render(partialTicks);
            textUI.clear();
        }

        if (Game.getGame().getScreen().getScene().getSceneRender().getCurrentDebugMode() == 1) {
            if (entityPlayerSP instanceof KinematicPlayerSP) {
                KinematicPlayerSP kinematicPlayerSP = (KinematicPlayerSP) entityPlayerSP;
                this.coordinates.setText(String.format("%s %s %s | %s %s %s", (float) entityPlayerSP.getPosition().x, (float) entityPlayerSP.getPosition().y, (float) entityPlayerSP.getPosition().z, (float) kinematicPlayerSP.getCurrentHitScanCoordinate().x, (float) kinematicPlayerSP.getCurrentHitScanCoordinate().y, (float) kinematicPlayerSP.getCurrentHitScanCoordinate().z));
            }
        } else {
            this.coordinates.setText(String.format("%s %s %s", (int) entityPlayerSP.getPosition().x, (int) entityPlayerSP.getPosition().y, (int) entityPlayerSP.getPosition().z));
        }

        if (Game.DEBUG_MODE) {
            this.fps.render(partialTicks);
        }

        if (Game.getGame().getScreen().getScene().getSceneRender().getCurrentDebugMode() == 1) {

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
                    i1 += 30;
                }
            }

            if (entityPlayerSP instanceof KinematicPlayerSP) {
                KinematicPlayerSP kinematicPlayerSP = (KinematicPlayerSP) entityPlayerSP;
                this.speed.setText("speed: " + String.format("%.4f", kinematicPlayerSP.getScalarSpeed()));
                this.speed.setPosition(new Vector3f(0.0f, i1 + 20.0f, 0.0f));
                this.speed.render(partialTicks);
            }

            this.tick.setText("tick: " + sceneWorld.getTicks());
            this.tick.setPosition(new Vector3f(0.0f, i1 + 40.0f, 0.0f));
            this.tick.render(partialTicks);
        }

        if (entityPlayerSP instanceof KinematicPlayerSP) {
            KinematicPlayerSP kinematicPlayerSP = (KinematicPlayerSP) entityPlayerSP;

            if (kinematicPlayerSP.getCurrentSelectedItem() instanceof PhysPlank) {
                TextUI textUI = new TextUI("[Use crowbar to break it]", ResourceManager.renderAssets.standardFont, 0xffc8c8);
                textUI.setPosition(new Vector3f((float) width / 2.0f - textUI.getTextWidth() / 2.0f, (float) height / 2.0f - textUI.getTextHeight() / 2.0f - 100.0f, 0.5f));
                textUI.render(partialTicks);
                textUI.clear();
            }

            Inventory inventory = kinematicPlayerSP.inventory();
            int j = 0;

            String cdText = "[" + kinematicPlayerSP.getPickedCds() + " / " + kinematicPlayerSP.getMaxCds() + "] Disks";
            String cassetteText = "[" + kinematicPlayerSP.getPrickedCassettes() + " / " + kinematicPlayerSP.getMaxCassettes() + "] Cassettes";
            this.cdText.setPosition(new Vector3f((float) (width - BasicUI.getTextWidth(this.cdText.getFont(), cassetteText)) - 20.0f, 200.0f, 0.0f));
            this.cdText.setText(cdText);
            this.cdText.setHexColor(0xffee22);
            this.cdText.render(partialTicks);

            this.cassetteText.setPosition(new Vector3f((float) (width - BasicUI.getTextWidth(this.cdText.getFont(), cassetteText)) - 20.0f, 240.0f, 0.0f));
            this.cassetteText.setText(cassetteText);
            this.cassetteText.setHexColor(0xffee22);
            this.cassetteText.render(partialTicks);

            if (Game.getGame().getScreen().getScene().getSceneRender().getCurrentDebugMode() == 0) {
                if (inventory.getCurrentItem() != null && inventory.getCurrentItem().getDescription() != null) {
                    this.itemDescText.setText(inventory.getCurrentItem().getDescription());
                    this.itemDescText.render(partialTicks);
                }
            }

            for (Inventory.Slot slot : inventory.getInventorySlots()) {
                if (slot.getInventoryItem() == null) {
                    continue;
                }
                RenderInventoryItemData renderInventoryItemData = ResourceManager.renderDataAssets.inventoryItemRenderTable.getMap().get(slot.getInventoryItem().getClass());
                ImageSizedUI imageSizedUI = new ImageSizedUI(renderInventoryItemData.getInventoryIcon(), new Vector3f(64.0f + (96.0f) * j++, (float) (height - 112.0d), 0.0f), new Vector2f(96.0f, 96.0f));
                imageSizedUI.render(partialTicks);
                imageSizedUI.clear();

                TextUI textUI = new TextUI("[" + j + "]", ResourceManager.renderAssets.standardFont, inventory.getCurrentSlot() == slot.getId() ? 0xff0000 : 0xffffff, new Vector3f((94.0f) * j, (float) (height - 132.0d), 0.0f));
                textUI.render(partialTicks);
                textUI.clear();
            }

            if (kinematicPlayerSP.isHasSoda()) {
                ImageSizedUI imageSizedUI = new ImageSizedUI(ResourceManager.renderAssets.soda_inventory, new Vector3f((float) (width - 120.0f), 50.0f, 0.0f), new Vector2f(96.0f, 96.0f));
                imageSizedUI.render(partialTicks);
                imageSizedUI.clear();

                TextUI textUI = new TextUI("[Press X]", ResourceManager.renderAssets.standardFont, 0xffffff, new Vector3f((float) (width - 140.0f), 26.0f, 0.0f));
                textUI.render(partialTicks);
                textUI.clear();
            }
        }

        this.staminaText.setPosition(new Vector3f(42.0f, (float) (height - 190.0f), 0.0f));
        this.staminaText.render(partialTicks);

        this.mindText.setPosition(new Vector3f(42.0f, (float) (height - 250.0f), 0.0f));
        this.mindText.render(partialTicks);
    }

    private void renderImagesOnScreen(double partialTicks) {
        double width = Game.getGame().getScreen().getWidth();
        double height = Game.getGame().getScreen().getHeight();
        float stamina = ((KinematicPlayerSP) Game.getGame().getPlayerSP()).getStamina();
        float mind = ((KinematicPlayerSP) Game.getGame().getPlayerSP()).getMind();

        this.stamina_over.setPosition(new Vector3f(42.0f, (float) (height - 160.0f), 0.0f));
        this.stamina_over.render(partialTicks);

        this.stamina_real.setTextureWH(new Vector2f(101.0f * stamina, 6.0f));
        this.stamina_real.setPosition(new Vector3f(42.0f, (float) (height - 160.0f), 0.0f));
        this.stamina_real.render(partialTicks);

        this.mind_over.setPosition(new Vector3f(42.0f, (float) (height - 220.0f), 0.0f));
        this.mind_over.render(partialTicks);

        this.mind_real.setTextureWH(new Vector2f(101.0f * mind, 6.0f));
        this.mind_real.setPosition(new Vector3f(42.0f, (float) (height - 220.0f), 0.0f));
        this.mind_real.render(partialTicks);

        this.crosshair.setPosition(new Vector3f((int) (width / 2.0d) - this.crosshair.getSize().x / 2.0f, (int) (height / 2.0d) - this.crosshair.getSize().y / 2.0f, 0.0f));
        this.crosshair.render(partialTicks);
    }
}