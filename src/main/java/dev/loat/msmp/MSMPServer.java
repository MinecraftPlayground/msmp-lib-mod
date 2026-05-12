package dev.loat.msmp;

import dev.loat.msmp.mixin.ManagementServerAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.jsonrpc.ManagementServer;

import java.lang.reflect.Field;

/**
 * Provides access to the {@link ManagementServer} of a running {@link MinecraftServer}
 * and allows broadcasting MSMP notifications to all connected clients.
 *
 * <p>An instance should be created in the {@code SERVER_STARTED} lifecycle event
 * and discarded in the {@code SERVER_STOPPED} event:</p>
 *
 * <pre>{@code
 * private static MSMPServer msmp;
 *
 * ServerLifecycleEvents.SERVER_STARTED.register(server -> {
 *     msmp = new MSMPServer(server);
 * });
 *
 * ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
 *     msmp = null;
 * });
 *
 * // Broadcast a notification:
 * msmp.send(MY_NOTIFICATION, new MyPayload("hello"));
 * }</pre>
 */
public final class MSMPServer {

    private final ManagementServer managementServer;

    /**
     * Creates a new {@code MSMPServer} wrapping the {@link ManagementServer}
     * of the given {@link MinecraftServer}.
     *
     * <p>The {@link ManagementServer} is located via reflection since it is not
     * publicly accessible on {@link MinecraftServer}.</p>
     *
     * @param server the running {@link MinecraftServer} instance
     */
    public MSMPServer(MinecraftServer server) {
        this.managementServer = findManagementServer(server);
    }

    /**
     * Broadcasts a notification to all connected MSMP clients.
     *
     * <p>Does nothing if no {@link ManagementServer} was found during construction.</p>
     *
     * @param <Payload> The type of the payload
     * @param notification The notification to broadcast
     * @param payload The payload to send with the notification
     */
    public <Payload> void send(MSMPNotification<Payload> notification, Payload payload) {
        if (managementServer == null) return;
        ((ManagementServerAccessor) managementServer)
            .invokeForEachConnection(conn ->
                conn.sendNotification(notification.holder(), payload)
            );
    }

    /**
     * Finds the {@link ManagementServer} instance held by the given {@link MinecraftServer}
     * by traversing its class hierarchy via reflection.
     *
     * @param server the running {@link MinecraftServer} instance
     * 
     * @return the {@link ManagementServer} instance, or {@code null} if not found
     */
    private static ManagementServer findManagementServer(MinecraftServer server) {
        Class<?> clazz = server.getClass();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getType() == ManagementServer.class) {
                    try {
                        field.setAccessible(true);
                        ManagementServer ms = (ManagementServer) field.get(server);
                        if (ms != null) return ms;
                    } catch (Exception ignored) {}
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }
}
