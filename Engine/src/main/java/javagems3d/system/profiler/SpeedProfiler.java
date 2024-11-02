package javagems3d.system.profiler;

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
            this.sectionMap.get(name).close();
        }

        public double getResult(String name) {
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
