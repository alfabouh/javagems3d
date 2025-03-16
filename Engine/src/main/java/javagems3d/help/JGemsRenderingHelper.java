package javagems3d.help;

import javagems3d.graphics.objects.rendering.attributes.JGemsRenderProperties;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.rendering.programs.textures.base.ITextureProgram;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode2D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL46;

import java.util.List;

public abstract class JGemsRenderingHelper {
    public static int getMaxTextureUnits() {
        return GL46.glGetInteger(GL46.GL_MAX_TEXTURE_IMAGE_UNITS);
    }

    public static Model2D createScreenModel() {
        return MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), new Vector2f(JGemsCoreHelper.getScreen().getWindowDimensions()), 0);
    }

    public static void renderMeshNode(RenderMesh renderMesh) {
        GL46.glBindVertexArray(renderMesh.getVao());
        renderMesh.enableAllMeshAttributes();
        GL46.glDrawElements(GL46.GL_TRIANGLES, renderMesh.getTotalVertices(), GL46.GL_UNSIGNED_INT, 0);
        renderMesh.disableAllMeshAttributes();
        GL46.glBindVertexArray(0);
    }

    public static void renderModel2D(Model2D model2D, int code) {
        renderMeshList2D(model2D.getMeshStructure().getNodes(), code);
    }

    public static void renderMeshList2D(List<MeshNode2D> list, int code) {
        for (MeshNode2D meshNode2D : list) {
            renderMeshNode(meshNode2D.getMeshData());
        }
    }

    public static void renderModel3D(Model3D model3D, int layer, int code) {
        renderMeshList3D(model3D.<MeshStructure3D<RenderMesh>>getMeshStructureCast().getNodes(layer), code);
    }

    public static void renderMeshList3D(List<MeshNode3D<RenderMesh>> list, int code) {
        for (MeshNode3D<RenderMesh> meshNode3D : list) {
            renderMeshNode(meshNode3D.getMeshData());
        }
    }

    public static int getLightingCodeForShader(RenderAttributes configuration) {
        int code = 0;
        if (configuration.getProperties().getBool(JGemsRenderProperties.KEY_LIGHT_BRIGHTNESS)) {
            code |= 1 << 2;
        }
        return code;
    }

    public static int getTexturingCodeForShader(Material material) {
        int code = 0;
        if (material.getDiffuse() instanceof ITextureProgram) {
            code |= 1 << 2;
        }
        if (material.getNormalsMap() != null) {
            code |= 1 << 3;
        }
        if (material.getEmissionMap() != null) {
            code |= 1 << 4;
        }
        if (material.getSpecularMap() != null) {
            code |= 1 << 5;
        }
        if (material.getMetallicMap() != null) {
            code |= 1 << 6;
        }
        return code;
    }
}
