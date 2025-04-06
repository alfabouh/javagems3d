package javagems3d.mapping;

import java.util.HashSet;
import java.util.Set;

public abstract class MappingSystem {
    public static final String DATA_INFO = "JAVAGEMS3D MAP FILE";
    public static final String DATA_VERSION = "1.0";
    public static final String MAP_PROJECT_FILE = ".jgs3d";
    public static final String MAP_DATA_FILE = ".mapdata";
    public static final String MAP_SCRIPT_FILE = ".js";

    public static final Set<String> SUPPORTED_VERSIONS = new HashSet<String>() {{
        add("1.0");
    }};
}