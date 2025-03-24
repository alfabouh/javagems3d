package javagems3d.system.resources.assets.models.parsing.space;

import javagems3d.system.resources.assets.models.parsing.IParser;
import javagems3d.system.resources.assets.models.parsing.gltf2.GLTF2Parser;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;

public final class ParserSpace {
    private ParsedMeshTable parsedMeshTable;
    private final Type type;
    private final JGemsPath pathToModelMainFile;

    public ParserSpace(@NotNull Type type, @NotNull JGemsPath pathToModelMainFile) {
        this.parsedMeshTable = null;
        this.pathToModelMainFile = pathToModelMainFile;
        this.type = type;
    }

    @SuppressWarnings("all")
    public ParserSpace parse() {
        IParser parser = null;
        switch (this.getType()) {
            case GLTF2: {
                parser = new GLTF2Parser(this.getPathToModelMainFile());
            }
        }
        if (parser == null) {
            throw new JGemsRuntimeException("Where was an error, while parsing model: " + this.getPathToModelMainFile() + ". " + this.getType());
        }
        this.parsedMeshTable = parser.parse();
        return this;
    }

    public JGemsPath getPathToModelMainFile() {
        return this.pathToModelMainFile;
    }

    public ParsedMeshTable getParsedMeshTable() {
        return this.parsedMeshTable;
    }

    public Type getType() {
        return this.type;
    }

    public enum Type {
        GLTF2
    }
}
