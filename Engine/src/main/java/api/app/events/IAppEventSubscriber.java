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

package api.app.events;

import api.app.events.bus.Events;

public interface IAppEventSubscriber {
    /**
     * Using this method, you can mark all Event-containing classes of your project.
     * With Event, you can execute any of your code in certain engine execution locations
     *
     * @example <pre>
     * {@code
     *     @SubscribeEvent
     *     public static void onWorldTick(Events.PhysWorldTickPre event) {
     *         event.setCancelled(true);
     *         System.out.println("F");
     *     }
     * }
     * </pre>
     * @see Events
     * @see Events.Cancellable
     * @see Events.IEvent
     */
    void addClassWithEvents(Class<?> clazz);
}
