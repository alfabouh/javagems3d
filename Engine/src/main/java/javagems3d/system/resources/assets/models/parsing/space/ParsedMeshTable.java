package javagems3d.system.resources.assets.models.parsing.space;

import java.util.*;

public final class ParsedMeshTable {
    private final Map<String, List<ParsedVertexData>> map;

    public ParsedMeshTable() {
        this.map = new HashMap<>();
    }

    public void createRow(String name) {
        this.getMap().put(name, new ArrayList<>());
    }

    public void add(String name, List<ParsedVertexData> data) {
        this.getMap().get(name).addAll(data);
    }

    public Collection<List<ParsedVertexData>> values() {
        return this.getMap().values();
    }

    public Map<String, List<ParsedVertexData>> getMap() {
        return this.map;
    }
}
