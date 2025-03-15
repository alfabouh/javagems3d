package javagems3d.mapping.tags.items;

import javagems3d.system.resources.managing.resources.data.ICopyable;
import javagems3d.system.service.json.JSONFileManaging;

public interface TagItem extends ICopyable<TagItem> {
    JSONFileManaging.SerializationRules<TagItem> getSerializationRules();
}
