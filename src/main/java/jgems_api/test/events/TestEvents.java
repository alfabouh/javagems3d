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

package jgems_api.test.events;

import ru.jgems3d.engine_api.events.SubscribeEvent;
import ru.jgems3d.engine_api.events.bus.Events;

public class TestEvents {
    @SubscribeEvent
    public static void onWorldTick(Events.PhysWorldTickPre event) {
        event.setCancelled(false);
    }
}
