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

package javagems3d.toolbox.map_table.object;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class ObjectCategory {
    public static final Set<ObjectCategory> values = new HashSet<>();

    public static ObjectCategory GENERIC = new ObjectCategory("Generic");
    public static ObjectCategory ZONES = new ObjectCategory("Zones/Liquids");

    private final String groupName;

    public ObjectCategory(String name) {
        this.groupName = name;
        ObjectCategory.values.add(this);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.groupName);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        return this.getGroupName().equals(((ObjectCategory) obj).getGroupName());
    }

    public String getGroupName() {
        return this.groupName;
    }
}
