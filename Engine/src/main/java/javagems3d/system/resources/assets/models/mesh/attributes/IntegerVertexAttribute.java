package javagems3d.system.resources.assets.models.mesh.attributes;

import javagems3d.JGemsHelper;
import javagems3d.system.resources.assets.models.mesh.attributes.pointer.AttributePointer;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.IntBuffer;

public final class IntegerVertexAttribute extends VertexAttribute<Integer> {
    private IntBuffer intBuffer;

    public IntegerVertexAttribute(AttributePointer attributePointer) {
        super(attributePointer);
    }

    @Override
    public void pushGLBuffer() {
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, (IntBuffer) this.getBuffer(), GL30.GL_STATIC_DRAW);
    }

    public void putArray(int[] i) {
        for (int a : i) {
            this.getValues().add(a);
        }
    }

    @Override
    public void bake() {
        this.intBuffer = MemoryUtil.memAllocInt(this.getValues().size());
        this.intBuffer.put(JGemsHelper.UTILS.convertIntsArray(this.getValues())).flip();
    }

    @Override
    public Buffer getBuffer() {
        return this.intBuffer;
    }

    @Override
    public int attributeType() {
        return GL30.GL_INT;
    }
}
