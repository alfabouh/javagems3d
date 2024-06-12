package ru.alfabouh.jgems3d.engine.render.opengl.screen.window;

import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector4d;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import ru.alfabouh.jgems3d.proxy.exception.JGemsException;
import ru.alfabouh.jgems3d.proxy.logger.SystemLogging;

import java.nio.IntBuffer;

public class Window implements IWindow {
    public static final int defaultW = 1280;
    public static final int defaultH = 720;
    private final long window;
    private final WindowProperties windowProperties;
    private long currentMonitor;
    private boolean isInFocus;

    public Window(WindowProperties windowProperties) {
        this.isInFocus = false;
        this.window = GLFW.glfwCreateWindow(windowProperties.getWidth(), windowProperties.getHeight(), windowProperties.getTitle(), MemoryUtil.NULL, MemoryUtil.NULL);
        this.windowProperties = windowProperties;
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

    public boolean isActive() {
        if (this.getWindowDimensions().x == 0 || this.getWindowDimensions().y == 0) {
            return false;
        }
        return GLFW.glfwGetWindowAttrib(this.getDescriptor(), GLFW.GLFW_ICONIFIED) == 0;
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
            SystemLogging.get().getLogManager().log("FullScreen mode");
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
            SystemLogging.get().getLogManager().log("DefaultScreen mode");
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
