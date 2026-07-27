/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.system.service.stat;

import org.lwjgl.opengl.GL46;
import org.lwjgl.opengl.NVXGPUMemoryInfo;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

public abstract class PerformanceStat {
    public static Result getSystemStat() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        int[] maxTextureSizeA = new int[1];
        GL46.glGetIntegerv(GL46.GL_MAX_TEXTURE_SIZE, maxTextureSizeA);
        int[] dedicatedMemory = new int[1];
        GL46.glGetIntegerv(NVXGPUMemoryInfo.GL_GPU_MEMORY_INFO_DEDICATED_VIDMEM_NVX, dedicatedMemory);

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
