package ru.BouH.engine.render.scene.objects.gui;

import org.bytedeco.bullet.BulletDynamics.btKinematicCharacterController;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.binding.Binding;
import ru.BouH.engine.game.controller.input.Keyboard;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.materials.textures.TextureSample;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.basic.MeshHelper;
import ru.BouH.engine.game.resources.assets.models.formats.Format2D;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.physics.entities.player.KinematicPlayerSP;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.objects.gui.hud.GuiPicture;
import ru.BouH.engine.render.scene.objects.gui.hud.GuiText;
import ru.BouH.engine.render.scene.world.SceneWorld;
import ru.BouH.engine.render.screen.Screen;

public class GUI {
    public static void renderGUI(SceneRenderBase sceneRenderBase, double partialTicks) {
        double width = Game.getGame().getScreen().getWidth();
        double height = Game.getGame().getScreen().getHeight();
        final WorldItem entityPlayerSP = (WorldItem) Game.getGame().getPlayerSP();
        GUI.renderText(sceneRenderBase, partialTicks, 0, 0, "FPS: " + Screen.FPS + " | TPS: " + Screen.PHYS2_TPS, 0xffffff);
        GUI.renderText(sceneRenderBase, partialTicks, 0, 20, "entities: " + Game.getGame().getPhysicsWorld().countItems(), 0xffffff);
        GUI.renderText(sceneRenderBase, partialTicks, 0, 40, String.format("%s %s %s", (int) entityPlayerSP.getPosition().x, (int) entityPlayerSP.getPosition().y, (int) entityPlayerSP.getPosition().z), 0xffffff);
        int i1 = 60;
        if (!Keyboard.isPressedKey(GLFW.GLFW_KEY_LEFT_CONTROL)) {
            GUI.renderText(sceneRenderBase, partialTicks, 0, i1, "Управление LCTRL", 0xffffff);
        } else {
            for (Binding keyBinding : Binding.getBindingList()) {
                GUI.renderText(sceneRenderBase, partialTicks, 0, i1, keyBinding.toString(), 0xffffff);
                i1 += 20;
            }
        }
        if (entityPlayerSP instanceof KinematicPlayerSP) {
            GUI.renderText(sceneRenderBase, partialTicks, 0, i1 + 20, "speed: " + String.format("%.4f", ((KinematicPlayerSP) entityPlayerSP).getKinematicCharacterController().getLinearVelocity().length()), 0xffffff);
        }
        GUI.renderText(sceneRenderBase, partialTicks, 0, i1 + 40, "tick: " + sceneRenderBase.getSceneWorld().getTicks(), 0xffffff);

        Vector2d vector2d = GUI.getScaledPictureDimensions(ResourceManager.renderAssets.crosshair, 0.0625f);
        GUI.renderPicture(sceneRenderBase, partialTicks, (int) (width / 2.0d) - 8, (int) (height / 2.0d) - 8, (int) vector2d.x, (int) vector2d.y, ResourceManager.renderAssets.crosshair);
    }

    private static Vector2d getScaledPictureDimensions(TextureSample textureSample, float scale) {
        if (textureSample == null || !textureSample.isValid()) {
            return new Vector2d(0.0f);
        }
        double width = Game.getGame().getScreen().getWidth();
        double height = Game.getGame().getScreen().getHeight();
        Vector2d WH = new Vector2d(width / Screen.defaultW, height / Screen.defaultH).mul(scale);
        double picScale = Math.min(WH.x, WH.y);
        return new Vector2d(textureSample.getWidth() * picScale, textureSample.getHeight() * picScale);
    }

    private static void renderPicture(SceneRenderBase sceneRenderBase, double partialTicks, int x, int y, int w, int h, TextureSample textureSample) {
        GL30.glDisable(GL30.GL_DEPTH_TEST);
        Model<Format2D> model = MeshHelper.generatePlane2DModel(new Vector2d(x, y), new Vector2d(x + w, y + h), 0);
        ResourceManager.shaderAssets.guiShader.bind();
        ResourceManager.shaderAssets.guiShader.performUniform("texture_sampler", 0);
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        textureSample.bindTexture();
        ResourceManager.shaderAssets.guiShader.getUtils().performProjectionMatrix2d(model);
        Scene.renderModel(model, GL30.GL_TRIANGLES);
        ResourceManager.shaderAssets.guiShader.unBind();
        model.clean();
        GL30.glEnable(GL30.GL_DEPTH_TEST);
    }

    private static void renderText(SceneRenderBase sceneRenderBase, double partialTicks, int x, int y, String s, int HEX) {
        GuiText guiText = new GuiText(s, ResourceManager.shaderAssets.guiShader, x, y);
        guiText.getShaderManager().bind();
        guiText.getShaderManager().getUtils().performProjectionMatrix2d(guiText.getModel2DInfo());
        float[] hex = GUI.HEX2RGB(HEX);
        guiText.getShaderManager().performUniform("colour", new Vector4d(hex[0], hex[1], hex[2], 1.0f));
        guiText.renderFabric().onRender(partialTicks, sceneRenderBase, guiText);
        guiText.getShaderManager().unBind();
        guiText.getModel2DInfo().clean();
    }

    public static float[] HEX2RGB(int hex) {
        int r = (hex & 0xFFFFFF) >> 16;
        int g = (hex & 0xFFFF) >> 8;
        int b = hex & 0xFF;
        return new float[]{r / 255.0f, g / 255.0f, b / 255.0f};
    }
}
