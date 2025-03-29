package javagems3d.system.resources.assets.models.parsing.space;

import javagems3d.system.resources.assets.models.parsing.IParser;
import javagems3d.system.resources.assets.models.parsing.gltf2.GLTF2Parser;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import org.jetbrains.annotations.NotNull;

public final class ParserSpace {
    private ParsedMesh parsedMesh;
    private final Type type;
    private final JGemsPath pathToModelMainFile;

    public ParserSpace(@NotNull Type type, @NotNull JGemsPath pathToModelMainFile) {
        this.parsedMesh = null;
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
        try {
            this.parsedMesh = parser.parse();
        } catch (Exception e) {
            Log.get().error("An error occured, while loading " + this.getPathToModelMainFile());
            throw e;
        }
        return this;
    }

    public JGemsPath getPathToModelMainFile() {
        return this.pathToModelMainFile;
    }

    public ParsedMesh getParsedMeshTable() {
        return this.parsedMesh;
    }

    public Type getType() {
        return this.type;
    }

    public enum Type {
        GLTF2
    }
}
