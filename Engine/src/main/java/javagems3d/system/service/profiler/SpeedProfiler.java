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

package javagems3d.system.service.profiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class SpeedProfiler {
    private static final SpeedProfiler INSTANCE = new SpeedProfiler();

    private final Map<String, Group> groupMap;

    private SpeedProfiler() {
        this.groupMap = new HashMap<>();
    }

    public static void clear() {
        SpeedProfiler.INSTANCE.groupMap.clear();
    }

    public static void removeGroup(String name) {
        if (!SpeedProfiler.INSTANCE.groupMap.containsKey(name)) {
            return;
        }
        SpeedProfiler.INSTANCE.groupMap.remove(name);
    }

    public static Group getGroup(String name) {
        if (SpeedProfiler.INSTANCE.groupMap.containsKey(name)) {
            return SpeedProfiler.INSTANCE.groupMap.get(name);
        }
        Group group = new Group();
        SpeedProfiler.INSTANCE.groupMap.put(name, group);
        return group;
    }

    public static Set<Map.Entry<String, Group>> getAllProfilerGroups() {
        return SpeedProfiler.INSTANCE.groupMap.entrySet();
    }

    public static class Group {
        private final Map<String, Section> sectionMap;

        public Group() {
            this.sectionMap = new HashMap<>();
        }

        public double totalTimeInAllSections() {
            double d = 0.0f;
            for (Map.Entry<String, SpeedProfiler.Section> sectionEntry : this.getAllEntries()) {
                d += sectionEntry.getValue().getTotalTime();
            }
            return d;
        }

        public Set<Map.Entry<String, Section>> getAllEntries() {
            return this.sectionMap.entrySet();
        }

        public void clearAllSections() {
            this.sectionMap.clear();
        }

        public void clear(String name) {
            this.sectionMap.get(name).close();
            this.sectionMap.remove(name);
        }

        public Section profile(String name) {
            Section section = new Section();
            this.sectionMap.put(name, section);
            return section;
        }

        public void close(String name) {
            if (!this.sectionMap.containsKey(name)) {
                return;
            }
            this.sectionMap.get(name).close();
        }

        public double getTotalTime(String name) {
            if (!this.sectionMap.containsKey(name)) {
                return -1.0f;
            }
            return this.sectionMap.get(name).getTotalTime();
        }
    }

    public static class Section implements AutoCloseable {
        private double startTime;
        private double totalTime;

        public Section() {
            this.open();
        }

        public void open() {
            this.startTime = System.nanoTime();
        }

        public double getTotalTime() {
            return this.totalTime;
        }

        @Override
        public void close() {
            this.totalTime = (System.nanoTime() - this.startTime) / 1000000.0d;
        }
    }
}
