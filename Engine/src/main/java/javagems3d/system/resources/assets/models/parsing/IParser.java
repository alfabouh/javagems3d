package javagems3d.system.resources.assets.models.parsing;

import javagems3d.system.resources.assets.models.parsing.space.ParsedMeshTable;
import javagems3d.system.service.exceptions.JGemsIOException;

public interface IParser {
    ParsedMeshTable parse() throws JGemsIOException;
    String descriptor();
    default JGemsIOException newException(Exception e) {
        throw new JGemsIOException("Couldn't read GLTF model: " + this.descriptor(), e);
    }

    default JGemsIOException newException(String e) {
        throw new JGemsIOException("Couldn't read GLTF model: " + this.descriptor() + ". " + e);
    }
}
