package javagems3d.graphics.rendering.scene.buffers;

import javagems3d.JGemsHelper;
import javagems3d.system.resources.assets.models.mesh.DataMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure;
import javagems3d.system.resources.assets.models.mesh.vertex.buffers.VertexBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.RenderAttributePointer;
import javagems3d.system.resources.managing.resources.data.arrays.MeshBuffersDataArray;
import javagems3d.system.resources.managing.resources.data.cache.MeshBuffersDataCache;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.stream.Collectors;

public final class IndirectRenderBuffer {
    private int staticVao;
    private final List<Integer> vboList;

    private List<MeshBuffer> allStaticMeshBuffers;
    private List<MeshBuffer> allAnimatedMeshBuffers;

    private final Layout layout;

    public IndirectRenderBuffer(RenderAttributePointer... attributePointers) {
        this(new Layout(attributePointers));
    }

    public IndirectRenderBuffer(@NotNull Layout layout) {
        this.vboList = new ArrayList<>();
        this.layout = layout;
    }

    public void init(MeshBuffersDataCache meshBuffersDataCache) {
        Map<Boolean, List<MeshBuffer>> partitionedModels = meshBuffersDataCache.getMeshBuffers().stream().collect(Collectors.partitioningBy(MeshStructure::isAnimationsNotEmpty));

        this.allStaticMeshBuffers = partitionedModels.get(false);
        this.allAnimatedMeshBuffers = partitionedModels.get(true);

        this.forStatic(this.getAllStaticMeshBuffers());
        //this.forStatic(this.getAllAnimatedMeshBuffers());
    }

    private void forStatic(List<MeshBuffer> obj) {
        if (obj.isEmpty()) {
            return;
        }
        JGemsHelper.getLogger().log("Creating static indirect buffer: " + obj.size());
        this.staticVao = GL46.glGenVertexArrays();
        GL46.glBindVertexArray(this.getStaticVao());

        int structSize = 0;
        int indexesSize = 0;
        int positionsSize = 0;
        int offset = 0;
        int firstIndexOffset = 0;

        for (MeshBuffer meshBuffer : obj) {
            meshBuffer.getPassData().clear();
            int collect = 0;
            for (MeshBuffer.MeshBufferNode node : meshBuffer.getMeshNodes()) {
                DataMesh dataMesh = node.getMesh();
                int posLength = dataMesh.numPositions();
                indexesSize += dataMesh.numVertices();
                positionsSize += posLength;

                for (VertexBuffer<Float> attributePointer : dataMesh.getBufferMap().values()) {
                    structSize += attributePointer.getLength();
                }

                int meshSizeInBytes = 0;
                for (int i = 0; i < this.getLayout().getAttributesNum(); i++) {
                    RenderAttributePointer renderAttributePointer = this.getLayout().getRenderAttributePointers().get(i);
                    meshSizeInBytes += renderAttributePointer.getLengthInMemory() * renderAttributePointer.getBytes();
                }
                meshSizeInBytes *= posLength;

                meshBuffer.getPassData().add(new MeshBuffer.PassData(firstIndexOffset, meshSizeInBytes, node.getMaterialId(), offset, dataMesh.numVertices()));
                offset = positionsSize / 3;
                collect += node.getMesh().numVertices();
            }
            firstIndexOffset += collect;
        }

        int vboId = GL46.glGenBuffers();
        this.getVboList().add(vboId);
        FloatBuffer meshesBuffer = MemoryUtil.memAllocFloat(structSize);
        for (MeshBuffer meshBuffer : obj) {
            List<MeshBuffer.MeshBufferNode> nodes = meshBuffer.getMeshNodes();
            for (MeshBuffer.MeshBufferNode node : nodes) {
                DataMesh dataMesh = node.getMesh();
                this.populateMeshBuffer(meshesBuffer, dataMesh);
            }
        }
        meshesBuffer.flip();
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, vboId);
        GL46.glBufferData(GL46.GL_ARRAY_BUFFER, meshesBuffer, GL46.GL_STATIC_DRAW);
        MemoryUtil.memFree(meshesBuffer);

        this.defineVertexAttributes();

        vboId = GL46.glGenBuffers();
        this.getVboList().add(vboId);
        IntBuffer indexesBuffer = MemoryUtil.memAllocInt(indexesSize);
        for (MeshBuffer meshBuffer : obj) {
            List<MeshBuffer.MeshBufferNode> nodes = meshBuffer.getMeshNodes();
            for (MeshBuffer.MeshBufferNode node : nodes) {
                DataMesh dataMesh = node.getMesh();
                for (int i : dataMesh.getIndexesBuffer().getValues()) {
                    indexesBuffer.put(i);
                }
            }
        }
        indexesBuffer.flip();

        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, vboId);
        GL46.glBufferData(GL46.GL_ELEMENT_ARRAY_BUFFER, indexesBuffer, GL46.GL_STATIC_DRAW);
        MemoryUtil.memFree(indexesBuffer);

        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, 0);
        GL46.glBindVertexArray(0);
    }

    private void forAnimated(List<MeshBuffer> obj) {
        //TODO
    }

    private void populateMeshBuffer(FloatBuffer floatBuffer, DataMesh dataMesh) {
        List<VertexBuffer<Float>> values = new ArrayList<>(dataMesh.getBufferMap().values());
        values.sort(Comparator.comparingInt(e -> e.getRenderAttributePointer().getIndex()));

        for (int row = 0; row < dataMesh.numPositions() / 3; row += 1) {
            for (int j = 0; j < this.getLayout().getAttributesNum(); j++) {
                RenderAttributePointer renderAttributePointer = this.getLayout().getRenderAttributePointers().get(j);
                for (int i = 0; i < renderAttributePointer.getLengthInMemory(); i++) {
                    floatBuffer.put(dataMesh.getBufferById(j).getValues().get(row * renderAttributePointer.getLengthInMemory() + i));
                }
            }
        }
    }

    private void defineVertexAttributes() {
        int stride = 0;
        int pointer = 0;

        for (int i = 0; i < this.getLayout().getAttributesNum(); i++) {
            RenderAttributePointer renderAttributePointer = this.getLayout().getRenderAttributePointers().get(i);
            stride += renderAttributePointer.getLengthInMemory() * renderAttributePointer.getBytes();
        }

        for (int i = 0; i < this.getLayout().getAttributesNum(); i++) {
            RenderAttributePointer renderAttributePointer = this.getLayout().getRenderAttributePointers().get(i);
            int idx = renderAttributePointer.getIndex();
            int size = renderAttributePointer.getLengthInMemory();

            GL46.glEnableVertexAttribArray(idx);
            GL46.glVertexAttribPointer(idx, size, GL46.GL_FLOAT, false, stride, pointer);
            pointer += renderAttributePointer.getLengthInMemory() * renderAttributePointer.getBytes();
        }
    }

    public void clear() {
        GL46.glDeleteVertexArrays(this.getStaticVao());
        this.getVboList().forEach(GL46::glDeleteBuffers);
        this.getVboList().clear();
        this.staticVao = 0;
    }

    public Layout getLayout() {
        return this.layout;
    }

    public List<MeshBuffer> getAllStaticMeshBuffers() {
        return this.allStaticMeshBuffers;
    }

    public List<MeshBuffer> getAllAnimatedMeshBuffers() {
        return this.allAnimatedMeshBuffers;
    }

    public int getStaticVao() {
        return this.staticVao;
    }

    public List<Integer> getVboList() {
        return this.vboList;
    }

    public static class Layout {
        private final List<RenderAttributePointer> renderAttributePointers;

        public Layout(RenderAttributePointer... attributePointers) {
            this.renderAttributePointers = new ArrayList<>(Arrays.asList(attributePointers));
            this.renderAttributePointers.sort(Comparator.comparingInt(RenderAttributePointer::getIndex));
        }

        public List<RenderAttributePointer> getRenderAttributePointers() {
            return this.renderAttributePointers;
        }

        public int getAttributesNum() {
            return this.getRenderAttributePointers().size();
        }
    }
}
