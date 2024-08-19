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

package ru.jgems3d.engine_api.events;

public interface IAppEventSubscriber {
    /**
     * Using this method, you can mark all Event-containing classes of your project.
     * With Event, you can execute any of your code in certain engine execution locations
     * @see ru.jgems3d.engine_api.events.bus.Events
     * @see ru.jgems3d.engine_api.events.bus.Events.Cancellable
     * @see ru.jgems3d.engine_api.events.bus.Events.IEvent
     * @example
     * <pre>
     *{@code
     *     @SubscribeEvent
     *     public static void onWorldTick(Events.PhysWorldTickPre event) {
     *         event.setCancelled(true);
     *         System.out.println("F");
     *     }
     *}
     * </pre>
     */
    void addClassWithEvents(Class<?> clazz);
}
