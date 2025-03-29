package javagems3d.system.resources.assets.loading.models;

import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.help.JGemsUtils;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.loading.ILoadingHelper;
import javagems3d.system.resources.assets.loading.models.utils.ModelLoadingUtils;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.animation.components.SkeletonData;
import javagems3d.system.resources.assets.models.mesh.DataMesh;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.models.parsing.space.ParsedMaterialData;
import javagems3d.system.resources.assets.models.parsing.space.ParsedMesh;
import javagems3d.system.resources.assets.models.parsing.space.ParsedVertexData;
import javagems3d.system.resources.assets.models.parsing.space.ParserSpace;
import javagems3d.system.resources.assets.texturing.colors.Color3Texture;
import javagems3d.system.resources.assets.texturing.colors.Color4Texture;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.exceptions.JGemsException;
import javagems3d.system.service.exceptions.JGemsNullException;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;
import org.lwjgl.assimp.AIMesh;

import java.util.ArrayList;
import java.util.List;

public class ModelMeshLoader2 implements ILoadingHelper {
    private final JGemsPath path;
    private final ParserSpace.Type type;

    public ModelMeshLoader2(@NotNull ParserSpace.Type type, @NotNull JGemsPath modelPath) {
        this.path = modelPath;
        this.type = type;
    }

    public String getStr(String postfix) {
        return ModelMeshLoader.getModelStr(this.getPath(), postfix);
    }

    public static String getModelStr(JGemsPath path, String postfix) {
        return path + postfix;
    }

    public MeshGroup createMeshGroup(@NotNull SystemResources systemResources, int Flags, boolean attachMeshBuffer, boolean keepNodesInMemory) {
        boolean animated = (Flags & ModelLoaderFlags.LOAD_ANIMATIONS) != 0;
        boolean createCollision = (Flags & ModelLoaderFlags.CREATE_COLLISION_UD) != 0;
        boolean createAabb = (Flags & ModelLoaderFlags.CREATE_AABB_UD) != 0;
        boolean determineTransparency = (Flags & ModelLoaderFlags.DETERMINE_TEXTURES_WITH_TRANSPARENCY) != 0;

        MeshGroup meshGroup = null;
        String grString = this.getStr(MeshGroup.POSTFIX);
        if (systemResources.getResourceCache().checkObjectInCache(grString)) {
            meshGroup = systemResources.getResourceCache().getCachedObjectUnSafeCast(grString);
            Log.get().info("Mesh " + this.getPath() + " picked from cache");
        } else {
            meshGroup = this.processMeshGroup(systemResources, attachMeshBuffer, keepNodesInMemory);
            systemResources.getResourceCache().addObjectInBuffer(grString, meshGroup);
            Log.get().info("Mesh " + this.getPath() + " successfully created");
        }
        if (meshGroup == null) {
            throw new JGemsNullException("There was an error, while loading the model");
        }
        if (createCollision) {
            JGemsUtils.createMeshCollisionData(meshGroup);
        }
        if (createAabb) {
            JGemsUtils.createMeshAABBData(meshGroup);
        }
        if (!keepNodesInMemory) {
            meshGroup.clearNodesData();
        }
        return meshGroup;
    }

    public MeshBuffer createMeshBuffer(@NotNull SystemResources systemResources, int Flags, boolean keepNodesInMemory) {
        boolean animated = (Flags & ModelLoaderFlags.LOAD_ANIMATIONS) != 0;
        boolean createCollision = (Flags & ModelLoaderFlags.CREATE_COLLISION_UD) != 0;
        boolean createAabb = (Flags & ModelLoaderFlags.CREATE_AABB_UD) != 0;
        boolean determineTransparency = (Flags & ModelLoaderFlags.DETERMINE_TEXTURES_WITH_TRANSPARENCY) != 0;

        String bffString = this.getStr(MeshBuffer.POSTFIX);
        MeshBuffer meshBuffer = null;
        if (this.isCacheValid() && systemResources.getResourceCache().checkObjectInCache(bffString)) {
            meshBuffer = systemResources.getResourceCache().getCachedObjectUnSafeCast(bffString);
            Log.get().info("Mesh " + this.getPath() + " picked from cache");
        } else {
            meshBuffer = this.processMeshBuffer(systemResources);
            systemResources.getResourceCache().addObjectInBuffer(bffString, meshBuffer);
            Log.get().info("Mesh " + this.getPath() + " successfully created");
        }
        if (meshBuffer == null) {
            throw new JGemsNullException("There was an error, while loading the model");
        }
        if (createCollision) {
            JGemsUtils.createMeshCollisionData(meshBuffer);
        }
        if (createAabb) {
            JGemsUtils.createMeshAABBData(meshBuffer);
        }
        meshBuffer.setKeepNodesInMemory(keepNodesInMemory);
        return meshBuffer;
    }

    protected MeshGroup processMeshGroup(@NotNull SystemResources systemResources, boolean attachMeshBuffer, boolean keepNodesInMemory) {
        ParserSpace parserSpace = new ParserSpace(this.getType(), this.getPath()).parse();
        ParsedMesh parsedMesh = parserSpace.getParsedMeshTable();
        MeshBuffer meshBuffer = attachMeshBuffer ? new MeshBuffer() : null;
        MeshGroup meshGroup = new MeshGroup();

        List<Material> materialList = new ArrayList<>();
        for (ParsedMaterialData parsedMaterialData : parsedMesh.getMaterialsData()) {
            Material material = this.readMaterial(systemResources, parsedMaterialData);
            if (meshBuffer != null) {
                systemResources.getResourceArrays().getMeshBuffersDataArray().addMaterial(material);
            }
            materialList.add(material);
        }

        for (List<ParsedVertexData> list : parsedMesh.values()) {
            for (ParsedVertexData parsedVertexData1 : list) {
                RenderMesh renderMesh = this.createRenderMesh(parsedVertexData1, keepNodesInMemory);
                Material material = new Material();
                if (parsedVertexData1.getMaterialId() >= 0) {
                    material = materialList.get(parsedVertexData1.getMaterialId());
                }
                meshGroup.putNode(MeshStructure3D.chooseLayer(material), new MeshNode3D<>(renderMesh, material));
                if (meshBuffer != null) {
                    DataMesh dataMesh = this.createDataMesh(parsedVertexData1);
                    meshBuffer.putNode(MeshStructure3D.chooseLayer(material), new MeshNode3D<>(dataMesh, material));
                }
            }
        }
        if (meshBuffer != null) {
            meshBuffer.setKeepNodesInMemory(false);
            systemResources.getResourceArrays().getMeshBuffersDataArray().addMeshBuffer(meshBuffer);
            meshGroup.setLinkedMeshBuffer(meshBuffer);
        }
        return meshGroup;
    }

    protected MeshBuffer processMeshBuffer(@NotNull SystemResources systemResources) {
        ParserSpace parserSpace = new ParserSpace(this.getType(), this.getPath()).parse();
        ParsedMesh parsedMesh = parserSpace.getParsedMeshTable();
        MeshBuffer meshBuffer = new MeshBuffer();

        List<Material> materialList = new ArrayList<>();
        for (ParsedMaterialData parsedMaterialData : parsedMesh.getMaterialsData()) {
            Material material = this.readMaterial(systemResources, parsedMaterialData);
            systemResources.getResourceArrays().getMeshBuffersDataArray().addMaterial(material);
            materialList.add(material);
        }

        for (List<ParsedVertexData> list : parsedMesh.values()) {
            for (ParsedVertexData parsedVertexData1 : list) {
                DataMesh dataMesh = this.createDataMesh(parsedVertexData1);
                Material material = new Material();
                if (parsedVertexData1.getMaterialId() >= 0) {
                    material = materialList.get(parsedVertexData1.getMaterialId());
                }
                meshBuffer.putNode(MeshStructure3D.chooseLayer(material), new MeshNode3D<>(dataMesh, material));
            }
        }

        systemResources.getResourceArrays().getMeshBuffersDataArray().addMeshBuffer(meshBuffer);
        return meshBuffer;
    }

    protected Material readMaterial(@NotNull SystemResources systemResources, ParsedMaterialData parsedMaterialData) {
        boolean emptyTexture = parsedMaterialData.getDiffuseColor() == null && parsedMaterialData.getDiffuseMapPath() == null;

        Color4Texture diffuseColor = parsedMaterialData.getDiffuseColor() == null ? new Color4Texture(new Vector4f(1.0f)) : new Color4Texture(parsedMaterialData.getDiffuseColor());
        Color3Texture emissionColor = parsedMaterialData.getEmissionColor() == null ? null : new Color3Texture(parsedMaterialData.getEmissionColor());

        ITexture2DProgram diffuseMap = null;
        ITexture2DProgram emissionMap = null;
        ITexture2DProgram normalsMap = null;
        ITexture2DProgram metallicRoughnessMap = null;

        float metallicFactor = parsedMaterialData.getMetallicFactor();
        float roughnessFactor = parsedMaterialData.getRoughnessFactor();

        final ImageTexture.Properties imageProperties = new ImageTexture.Properties(true, true);
        try {
            if (parsedMaterialData.getDiffuseMapPath() != null) {
                diffuseMap = systemResources.createTexture(null, new JGemsPath(this.getPath().getDirectory(), parsedMaterialData.getDiffuseMapPath()), imageProperties);
            } else if (emptyTexture) {
                diffuseMap = ResourceManager.DEFAULT_TEXTURE();
            }

            if (parsedMaterialData.getEmissionMapPath() != null) {
                emissionMap = systemResources.createTexture(null, new JGemsPath(this.getPath().getDirectory(), parsedMaterialData.getEmissionMapPath()), imageProperties);
            }

            if (parsedMaterialData.getMetallicRoughnessMapPath() != null) {
                metallicRoughnessMap = systemResources.createTexture(null, new JGemsPath(this.getPath().getDirectory(), parsedMaterialData.getMetallicRoughnessMapPath()), imageProperties);
            }

            if (parsedMaterialData.getNormalsMapPath() != null) {
                normalsMap = systemResources.createTexture(null, new JGemsPath(this.getPath().getDirectory(), parsedMaterialData.getNormalsMapPath()), imageProperties);
            }
        } catch (JGemsException e) {
            Log.get().exception(e);
        }

        return new Material(diffuseMap, diffuseColor, emissionMap, emissionColor, metallicRoughnessMap, normalsMap, metallicFactor, roughnessFactor);
    }

    private RenderMesh createRenderMesh(ParsedVertexData parsedVertexData, boolean keepNodesInMemory) {
        if (parsedVertexData.getTotalVertexes() > JGemsConfig.SYSTEM.MAX_VERTEXES_IN_MODEL) {
            throw new JGemsRuntimeException("Reached max vertexes in model: " + JGemsConfig.SYSTEM.MAX_VERTEXES_IN_MODEL);
        }
        List<Integer> vertices = parsedVertexData.getIndexes();
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

        renderMesh.setVertexIndexes(vertices);
        vaPositions.set(positions);
        vaNormals.set(normals);
        vaTextureCoordinates.set(textureCoordinates);
        vaTangents.set(tangents);
        vaBiTangents.set(biTangents);

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

    private DataMesh createDataMesh(ParsedVertexData parsedVertexData) {
        if (parsedVertexData.getTotalVertexes() > JGemsConfig.SYSTEM.MAX_VERTEXES_IN_MODEL) {
            throw new JGemsRuntimeException("Reached max vertexes in model: " + JGemsConfig.SYSTEM.MAX_VERTEXES_IN_MODEL);
        }
        List<Integer> vertices = parsedVertexData.getIndexes();
        List<Float> textureCoordinates = parsedVertexData.getUvPositions();
        List<Float> positions = parsedVertexData.getVertexes();
        List<Float> normals = parsedVertexData.getNormals();
        List<Float> tangents = parsedVertexData.getTangents();
        List<Float> biTangents = parsedVertexData.getBiTangents();

        if (textureCoordinates.isEmpty()) {
            int totalElements = (positions.size() / 3) * 2;
            textureCoordinates = new ArrayList<>(totalElements);
        }

        DataMesh dataMesh = new DataMesh();
        //dataMesh.setSkeletonData(skeletonData);

        dataMesh.putVertexIndexes(vertices);
        dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_POSITIONS, positions);
        dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_TEXTURE_COORDINATES, textureCoordinates);
        dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_NORMALS, normals);
        dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_TANGENTS, tangents);
        dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_BI_TANGENTS, biTangents);

        //if (skeletonData != null) {
        //    dataMesh.putVertexBufferI(DefaultAttributePointers.ATTR_BONES_INDEXES, skeletonData.getBoneIds());
        //    dataMesh.putVertexBufferF(DefaultAttributePointers.ATTR_BONES_WEIGHTS, skeletonData.getWeights());
        //}

        return dataMesh;
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
