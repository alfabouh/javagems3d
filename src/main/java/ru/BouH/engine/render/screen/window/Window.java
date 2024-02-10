package ru.BouH.engine.render.screen.window;

import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector4d;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

public class Window {
    private final long window;
    private final WindowProperties windowProperties;
    private long currentMonitor;

    public Window(WindowProperties windowProperties) {
        this.window = GLFW.glfwCreateWindow(windowProperties.getWidth(), windowProperties.getHeight(), windowProperties.getTitle(), MemoryUtil.NULL, MemoryUtil.NULL);
        this.windowProperties = windowProperties;
        GLFW.glfwSetWindowSizeCallback(this.getDescriptor(), (win, w, h) -> {

        });
        GLFW.glfwSetWindowSizeCallback(this.getDescriptor(), (win, w, h) -> {

        });
        this.currentMonitor = GLFW.glfwGetPrimaryMonitor();
    }

    public void onWindowChanged() {
        IntBuffer xPos = BufferUtils.createIntBuffer(1);
        IntBuffer yPos = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetWindowPos(window, xPos, yPos);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetWindowSize(window, width, height);

        int centerX = xPos.get(0) + width.get(0) / 2;
        int centerY = yPos.get(0) + height.get(0) / 2;

        this.currentMonitor = GLFW.glfwGetPrimaryMonitor();
        PointerBuffer pointerBuffer = GLFW.glfwGetMonitors();
        if (pointerBuffer != null) {
            for (int i = 0; i < pointerBuffer.capacity(); i++) {
                long m = pointerBuffer.get(i);
                GLFWVidMode vidMode = GLFW.glfwGetVideoMode(m);
                if (vidMode == null) {
                    continue;
                }
                IntBuffer mxPos = BufferUtils.createIntBuffer(1);
                IntBuffer myPos = BufferUtils.createIntBuffer(1);
                GLFW.glfwGetMonitorPos(m, mxPos, myPos);
                if (mxPos.get(0) <= centerX && centerX < mxPos.get(0) + vidMode.width() && myPos.get(0) <= centerY && centerY < myPos.get(0) + vidMode.height()) {
                    this.currentMonitor = m;
                    break;
                }
            }
        }
    }

    public int monitorRefreshRate() {
        GLFWVidMode glfwVidMode = GLFW.glfwGetVideoMode(this.getCurrentMonitor());
        assert glfwVidMode != null;
        return glfwVidMode.refreshRate();
    }

    public int getPosX() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer x = stack.mallocInt(1);
            GLFW.glfwGetWindowPos(this.window, x, null);
            return x.get(0);
        }
    }

    public int getPosY() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer y = stack.mallocInt(1);
            GLFW.glfwGetWindowPos(this.window, null, y);
            return y.get(0);
        }
    }

    public Vector4d getWindowFrameSize() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer l = stack.mallocInt(1);
            IntBuffer t = stack.mallocInt(1);
            IntBuffer r = stack.mallocInt(1);
            IntBuffer b = stack.mallocInt(1);
            GLFW.glfwGetWindowFrameSize(this.window, l, t, r, b);
            return new Vector4d(l.get(0), t.get(0), r.get(0), b.get(0));
        }
    }

    public Vector2d getWindowPos() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer x = stack.mallocInt(1);
            IntBuffer y = stack.mallocInt(1);
            GLFW.glfwGetWindowSize(this.window, x, y);
            return new Vector2d(x.get(0), y.get(0));
        }
    }

    public int getWidth() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            GLFW.glfwGetWindowSize(this.window, width, null);
            return width.get(0);
        }
    }

    public int getHeight() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer height = stack.mallocInt(1);
            GLFW.glfwGetWindowSize(this.window, null, height);
            return height.get(0);
        }
    }

    public Vector2i getWindowDimensions() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            GLFW.glfwGetWindowSize(this.window, width, height);
            return new Vector2i(width.get(0), height.get(0));
        }
    }

    public WindowProperties getWindowProperties() {
        return this.windowProperties;
    }

    public long getCurrentMonitor() {
        return this.currentMonitor;
    }

    public long getDescriptor() {
        return this.window;
    }

    public static class WindowProperties {
        private int width;
        private int height;
        private String title;

        public WindowProperties(int width, int height, String title) {
            this.width = width;
            this.height = height;
            this.title = title;
        }

        public int getWidth() {
            return this.width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return this.height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getTitle() {
            return this.title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
