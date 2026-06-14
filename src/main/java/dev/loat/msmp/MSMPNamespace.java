package dev.loat.msmp;

import dev.loat.msmp.builder.MethodBuilder;
import dev.loat.msmp.builder.NotificationBuilder;
import net.minecraft.server.MinecraftServer;

/**
 * Represents a custom MSMP namespace under which methods and notifications can be registered.
 *
 * <p>Instances are created directly via {@code new MsmpNamespace("my_mod")} and should be
 * registered in {@code onInitialize()} before the server starts. Use {@link #attach(MinecraftServer)}
 * in {@code SERVER_STARTED} to bind the server instance, and {@link #detach()} in
 * {@code SERVER_STOPPED} to release it.</p>
 *
 * <pre>{@code
 * private static final MsmpNamespace NS = new MsmpNamespace("my_mod");
 *
 * public void onInitialize() {
 *     NS.method("echo")
 *         .requestSchema(EchoPayload.SCHEMA)
 *         .responseSchema(EchoPayload.SCHEMA)
 *         .description("Echoes a message back to the client")
 *         .register((server, client, params) -> params);
 *
 *     NS.notification("ping")
 *         .responseSchema(PingPayload.SCHEMA)
 *         .description("A ping notification")
 *         .register();
 *
 *     ServerLifecycleEvents.SERVER_STARTED.register(server -> NS.attach(server));
 *     ServerLifecycleEvents.SERVER_STOPPED.register(server -> NS.detach());
 * }
 * }</pre>
 */
public final class MSMPNamespace {

    private final String namespace;
    private MinecraftServer server;

    /**
     * Creates a new namespace with the given identifier.
     *
     * @param namespace The namespace identifier (e.g. {@code "my_mod"})
     */
    public MSMPNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Binds the given {@link MinecraftServer} to this namespace.
     *
     * <p>Should be called in the {@code SERVER_STARTED} lifecycle event.
     * Required for method handlers to access the server instance.</p>
     *
     * @param server The running {@link MinecraftServer} instance
     */
    public void attach(MinecraftServer server) {
        this.server = server;
    }

    /**
     * Releases the bound {@link MinecraftServer} from this namespace.
     *
     * <p>Should be called in the {@code SERVER_STOPPED} lifecycle event.</p>
     */
    public void detach() {
        this.server = null;
    }

    /**
     * Returns the currently bound {@link MinecraftServer} instance.
     * Package-private — used by builder classes to resolve the server lazily.
     *
     * @return the bound server, or {@code null} if not attached
     */
    public MinecraftServer getServer() {
        return server;
    }

    /**
     * Creates a new method builder for the given name.
     *
     * @param name The name of this method (e.g. {@code "echo"}),
     * resulting in the identifier {@code namespace:method/name}
     * @return a {@link MethodBuilder} to configure and register the method
     */
    public MethodBuilder method(String name) {
        return new MethodBuilder(this, namespace, name);
    }

    /**
     * Creates a new notification builder for the given name.
     *
     * @param name The name of this notification (e.g. {@code "ping"}),
     * resulting in the identifier {@code namespace:notification/name}
     * @return a {@link NotificationBuilder} to configure and register the notification
     */
    public NotificationBuilder notification(String name) {
        return new NotificationBuilder(namespace, name);
    }
}
