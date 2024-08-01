package ru.jgems3d.engine.graphics.opengl.screen.window;

import com.google.common.io.ByteStreams;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.exceptions.JGemsException;
import ru.jgems3d.engine.system.misc.JGPath;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Window implements IWindow {
    public static final int defaultW = 1280;
    public static final int defaultH = 720;
    private final long window;
    private final WindowProperties windowProperties;
    private long currentMonitor;
    private boolean isInFocus;

    public Window(WindowProperties windowProperties, JGPath iconPath) {
        this.isInFocus = false;
        this.window = GLFW.glfwCreateWindow(windowProperties.getWidth(), windowProperties.getHeight(), windowProperties.getTitle(), MemoryUtil.NULL, MemoryUtil.NULL);
        this.windowProperties = windowProperties;
        this.currentMonitor = GLFW.glfwGetPrimaryMonitor();

        if (iconPath != null) {
            this.loadIcon(iconPath);
        }
    }

    private void loadIcon(@NotNull JGPath iconPath) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
            byte[] stream = ByteStreams.toByteArray(JGems3D.loadFileJar(iconPath));
            ByteBuffer buffer = MemoryUtil.memAlloc(stream.length);
            buffer.put(stream);
            buffer.flip();
            ByteBuffer imageBuffer = STBImage.stbi_load_from_memory(buffer, width, height, channels, STBImage.STBI_rgb_alpha);
            if (imageBuffer == null) {
                throw new NullPointerException("Window icon is NULL");
            }
            GLFWImage.Buffer iconBuffer = GLFWImage.malloc(1);
            iconBuffer.width(width.get(0));
            iconBuffer.height(height.get(0));
            iconBuffer.pixels(imageBuffer);
            GLFW.glfwSetWindowIcon(window, iconBuffer);
            STBImage.stbi_image_free(imageBuffer);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
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

    public void hideWindow() {
        GLFW.glfwHideWindow(this.getDescriptor());
    }

    public void showWindow() {
        GLFW.glfwShowWindow(this.getDescriptor());
        GLFW.glfwFocusWindow(this.getDescriptor());
    }

    public boolean isInFocus() {
        return this.isActive() && this.isInFocus;
    }

    public void setInFocus(boolean inFocus) {
        isInFocus = inFocus;
    }

    public void switchFocus() {
        this.isInFocus = !this.isInFocus;
    }

    public void refreshFocusState() {
        GLFW.glfwSetInputMode(this.getDescriptor(), GLFW.GLFW_CURSOR, !this.isInFocus() ? GLFW.GLFW_CURSOR_NORMAL : GLFW.GLFW_CURSOR_DISABLED);
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

    public Vector4f getWindowFrameSize() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer l = stack.mallocInt(1);
            IntBuffer t = stack.mallocInt(1);
            IntBuffer r = stack.mallocInt(1);
            IntBuffer b = stack.mallocInt(1);
            GLFW.glfwGetWindowFrameSize(this.window, l, t, r, b);
            return new Vector4f(l.get(0), t.get(0), r.get(0), b.get(0));
        }
    }

    public Vector2f getWindowPos() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer x = stack.mallocInt(1);
            IntBuffer y = stack.mallocInt(1);
            GLFW.glfwGetWindowSize(this.window, x, y);
            return new Vector2f(x.get(0), y.get(0));
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

    public boolean isFullScreen() {
        return GLFW.glfwGetWindowMonitor(this.getDescriptor()) != 0;
    }

    public void enableVSync() {
        GLFW.glfwSwapInterval(1);
    }

    public void disableVSync() {
        GLFW.glfwSwapInterval(0);
    }

    public void makeFullScreen() {
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        if (vidMode != null) {
            GLFW.glfwSetWindowMonitor(this.getDescriptor(), GLFW.glfwGetPrimaryMonitor(), 0, 0, vidMode.width(), vidMode.height(), GLFW.GLFW_DONT_CARE);
            JGemsHelper.getLogger().log("FullScreen mode");
        } else {
            throw new JGemsException("Monitor None");
        }
    }

    public void removeFullScreen() {
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        if (vidMode != null) {
            int x = (vidMode.width() - Window.defaultW) / 2;
            int y = (vidMode.height() - Window.defaultH) / 2;
            GLFW.glfwSetWindowMonitor(this.getDescriptor(), 0, x, y, Window.defaultW, Window.defaultH, GLFW.GLFW_DONT_CARE);
            JGemsHelper.getLogger().log("DefaultScreen mode");
        } else {
            throw new JGemsException("Monitor None");
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