package dev.loat.msmp;

import dev.loat.msmp.mixin.ManagementServerAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.jsonrpc.ManagementServer;

import java.lang.reflect.Field;


/**
 * Main entry point for interacting with the MSMP library.
 *
 * <p>Create an instance in the {@code SERVER_STARTED} lifecycle event and discard it
 * in {@code SERVER_STOPPED}. Use {@link #namespace(String)} to register methods and
 * notifications under a custom namespace, and {@link #send(MSMPNotification, Object)}
 * to broadcast notifications to all connected clients.</p>
 *
 * <pre>{@code
 * private static MSMPServer msmp;
 * private static MSMPNotification<PingPayload> ping;
 *
 * ServerLifecycleEvents.SERVER_STARTED.register(server -> {
 *     msmp = new MSMPServer(server);
 *     ping = msmp.namespace("my_mod")
 *         .notification("ping", PingPayload.SCHEMA, "A ping notification");
 * });
 *
 * ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
 *     msmp = null;
 * });
 *
 * // Later:
 * msmp.send(ping, new PingPayload("hello"));
 * }</pre>
 */
public final class MSMPServer {

    private final MinecraftServer server;
    private final ManagementServer managementServer;

    /**
     * Creates a new {@code MSMPServer} wrapping the {@link ManagementServer}
     * of the given {@link MinecraftServer}.
     *
     * <p>The {@link ManagementServer} is located via reflection since it is not
     * publicly accessible on {@link MinecraftServer}.</p>
     *
     * @param server The running {@link MinecraftServer} instance
     */
    public MSMPServer(MinecraftServer server) {
        this.server = server;
        this.managementServer = findManagementServer(server);
    }

    /**
     * Creates a new {@link MSMPNamespace} for registering methods and notifications
     * under the given namespace.
     *
     * @param namespace The namespace identifier (e.g. {@code "my_mod"})
     * 
     * @return A new {@link MSMPNamespace} instance
     */
    public MSMPNamespace namespace(String namespace) {
        return new MSMPNamespace(server, namespace);
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
     * @param server The running {@link MinecraftServer} instance
     * 
     * @return The {@link ManagementServer} instance, or {@code null} if not found
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
