/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.engine.system.service.stat;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.NVXGPUMemoryInfo;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

public abstract class PerformanceStat {
    public static Result getSystemStat() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        int[] maxTextureSizeA = new int[1];
        GL11.glGetIntegerv(GL11.GL_MAX_TEXTURE_SIZE, maxTextureSizeA);
        int[] dedicatedMemory = new int[1];
        GL11.glGetIntegerv(NVXGPUMemoryInfo.GL_GPU_MEMORY_INFO_DEDICATED_VIDMEM_NVX, dedicatedMemory);

        int maxMemoryMB = (int) (Runtime.getRuntime().maxMemory() / (long) (1024 * 1024));
        int maxTextureSize = maxTextureSizeA[0];
        int videoMem = dedicatedMemory[0] / 1024;

        int cpuThreads = osBean.getAvailableProcessors();
        int textureDegree = (int) (Math.log(maxTextureSize) / Math.log(2));
        int oMemoryGB = maxMemoryMB / 1024;
        int gMemoryGB = videoMem / 1024;

        final int maxCost = 11;
        int totalCost = PerformanceStat.gMemGBCost(gMemoryGB) + PerformanceStat.oMemGBCost(oMemoryGB) + PerformanceStat.textureDegreeCost(textureDegree) + PerformanceStat.threadsCost(cpuThreads);

        return Result.values()[(int) (((float) totalCost / maxCost) * Result.values().length) - 1];
    }

    private static int threadsCost(int threads) {
        if (threads < 4) {
            return 0;
        }
        if (threads <= 6) {
            return 1;
        }
        if (threads <= 10) {
            return 2;
        }
        return 3;
    }

    private static int gMemGBCost(int mem) {
        if (mem <= 1) {
            return 0;
        }
        if (mem == 2) {
            return 1;
        }
        if (mem <= 4) {
            return 2;
        }
        return 3;
    }

    private static int oMemGBCost(int mem) {
        if (mem <= 3) {
            return 0;
        }
        if (mem <= 6) {
            return 1;
        }
        return 2;
    }

    private static int textureDegreeCost(int degree) {
        if (degree <= 4) {
            return 0;
        }
        if (degree <= 8) {
            return 2;
        }
        return 3;
    }

    public enum Result {
        POTATO,
        LOW,
        MEDIUM,
        HIGH,
        GREAT
    }
}
