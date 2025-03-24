package javagems3d.system.resources.assets.loading.models;

import javagems3d.help.JGemsUtils;
import javagems3d.system.resources.assets.loading.ILoadingHelper;
import javagems3d.system.resources.assets.loading.models.utils.ModelLoadingUtils;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.animation.components.SkeletonData;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.IntegerVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.models.parsing.space.ParsedMeshTable;
import javagems3d.system.resources.assets.models.parsing.space.ParsedVertexData;
import javagems3d.system.resources.assets.models.parsing.space.ParserSpace;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ModelMeshLoader2 implements ILoadingHelper {
    private final JGemsPath path;
    private final ParserSpace.Type type;

    public ModelMeshLoader2(@NotNull ParserSpace.Type type, @NotNull JGemsPath modelPath) {
        this.path = modelPath;
        this.type = type;
    }

    public MeshGroup createMeshGroup() {
        ParserSpace parserSpace = new ParserSpace(this.getType(), this.getPath()).parse();
        ParsedMeshTable parsedMeshTable = parserSpace.getParsedMeshTable();

        MeshGroup meshGroup = new MeshGroup();

        for (List<ParsedVertexData> list : parsedMeshTable.values()) {
            for (ParsedVertexData parsedVertexData1 : list) {
                RenderMesh meshData = this.createRenderMesh(parsedVertexData1);
                meshGroup.putNode(0, new MeshNode3D<>(meshData, new Material(null)));
            }
        }

        JGemsUtils.createMeshAABBData(meshGroup);
        return meshGroup;
    }

    private RenderMesh createRenderMesh(ParsedVertexData parsedVertexData) {
        List<Integer> vertices = parsedVertexData.getIndexes();
       // this.countVertexes += vertices.size();
       // if (this.countVertexes > JGemsConfig.SYSTEM.MAX_VERTEXES_IN_MODEL) {
       //     throw new JGemsRuntimeException("Reached max vertexes in model: " + JGemsConfig.SYSTEM.MAX_VERTEXES_IN_MODEL);
       // }
        List<Float> textureCoordinates = parsedVertexData.getUvPositions();
        List<Float> positions = parsedVertexData.getVertexes();
        List<Float> normals = parsedVertexData.getNormals();
        List<Float> tangents = parsedVertexData.getTangents();
        List<Float> biTangents = parsedVertexData.getBiTangents();

        if (textureCoordinates.isEmpty()) {
            int totalElements = (positions.size() / 3) * 2;
            textureCoordinates = new ArrayList<>(totalElements);
        }

        RenderMesh renderMesh = new RenderMesh();
       // renderMesh.setSkeletonData(skeletonData);

        FloatVertexAttribute vaPositions = new FloatVertexAttribute(DefaultAttributePointers.ATTR_POSITIONS);
        FloatVertexAttribute vaTextureCoordinates = new FloatVertexAttribute(DefaultAttributePointers.ATTR_TEXTURE_COORDINATES);
        FloatVertexAttribute vaNormals = new FloatVertexAttribute(DefaultAttributePointers.ATTR_NORMALS);
        FloatVertexAttribute vaTangents = new FloatVertexAttribute(DefaultAttributePointers.ATTR_TANGENTS);
        FloatVertexAttribute vaBiTangents = new FloatVertexAttribute(DefaultAttributePointers.ATTR_BI_TANGENTS);

        renderMesh.putVertexIndexes(vertices);
        vaPositions.put(positions);
        vaNormals.put(normals);
        vaTextureCoordinates.put(textureCoordinates);
        vaTangents.put(tangents);
        vaBiTangents.put(biTangents);

        renderMesh.putVertexAttribute(vaPositions);
        renderMesh.putVertexAttribute(vaTextureCoordinates);
        renderMesh.putVertexAttribute(vaNormals);
        renderMesh.putVertexAttribute(vaTangents);
        renderMesh.putVertexAttribute(vaBiTangents);

     //   if (skeletonData != null) {
     //       IntegerVertexAttribute vaBonesIndexes = new IntegerVertexAttribute(DefaultAttributePointers.ATTR_BONES_INDEXES);
     //       FloatVertexAttribute vaBonesWeights = new FloatVertexAttribute(DefaultAttributePointers.ATTR_BONES_WEIGHTS);
     //       vaBonesIndexes.put(skeletonData.getBoneIds());
     //       vaBonesWeights.put(skeletonData.getWeights());
//
     //       renderMesh.putVertexAttribute(vaBonesIndexes);
     //       renderMesh.putVertexAttribute(vaBonesWeights);
     //   }

        renderMesh.bakeMesh();
        return renderMesh;
    }

    public JGemsPath getPath() {
        return this.path;
    }

    public ParserSpace.Type getType() {
        return this.type;
    }

    @Override
    public ResourceCache getResourceCache() {
        return null;
    }
}
