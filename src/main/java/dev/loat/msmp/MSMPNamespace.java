package dev.loat.msmp;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.jsonrpc.api.Schema;


/**
 * Represents a custom MSMP namespace under which methods and notifications can be registered.
 *
 * <p>Instances are created directly via {@code new MSMPNamespace("my_mod")} and should be
 * registered in {@code onInitialize()} before the server starts. Use {@link #attach(MinecraftServer)}
 * in {@code SERVER_STARTED} to bind the server instance, and {@link #detach()} in
 * {@code SERVER_STOPPED} to release it.</p>
 *
 * <pre>{@code
 * private static final MSMPNamespace NS = new MSMPNamespace("my_mod");
 *
 * public void onInitialize() {
 *     NS.method("echo", EchoPayload.SCHEMA, EchoPayload.SCHEMA,
 *         (server, params, client) -> params
 *     );
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
     * Creates and registers a new outgoing notification without a description.
     *
     * @param <Payload> The type of the payload sent with this notification
     * @param name The name of this notification (e.g. {@code "ping"}),
     * resulting in the identifier {@code namespace:notification/name}
     * @param schema The schema describing the payload structure
     * 
     * @return The registered {@link MSMPNotification}
     */
    public <Payload> MSMPNotification<Payload> notification(
        String name,
        Schema<Payload> schema
    ) {
        return new MSMPNotification<>(namespace, name, schema, "");
    }

    /**
     * Creates and registers a new outgoing notification with a description.
     *
     * @param <Payload> The type of the payload sent with this notification
     * @param name The name of this notification (e.g. {@code "ping"}),
     * resulting in the identifier {@code namespace:notification/name}
     * @param schema The schema describing the payload structure
     * @param description A human-readable description of this notification
     * 
     * @return The registered {@link MSMPNotification}
     */
    public <Payload> MSMPNotification<Payload> notification(
        String name,
        Schema<Payload> schema,
        String description
    ) {
        return new MSMPNotification<>(namespace, name, schema, description);
    }

    /**
     * Creates and registers a new incoming method without a description.
     *
     * <p>The handler is invoked when a client calls this method. The {@link MinecraftServer}
     * is resolved lazily at call time — {@link #attach(MinecraftServer)} must have been
     * called before any client can invoke this method.</p>
     *
     * @param <Param> The type of the payload received from the client
     * @param <Result> The type of the payload returned to the client
     * @param name The name of this method (e.g. {@code "echo"}),
     * resulting in the identifier {@code namespace:name}
     * @param paramSchema The schema describing the payload received from the client
     * @param resultSchema The schema describing the payload returned to the client
     * @param handler The handler to invoke when this method is called by a client
     * 
     * @return The registered {@link MSMPMethod}
     */
    public <Param, Result> MSMPMethod<Param, Result> method(
        String name,
        Schema<Param> paramSchema,
        Schema<Result> resultSchema,
        MSMPMethodHandler<Param, Result> handler
    ) {
        return new MSMPMethod<>(namespace, name, paramSchema, resultSchema, "",
            (api, params, client) -> {
                if (server == null) throw new IllegalStateException(
                    "MSMPNamespace '%s' has no server attached. Call attach(server) in SERVER_STARTED.".formatted(namespace)
                );
                return handler.apply(server, params, client);
            });
    }

    /**
     * Creates and registers a new incoming method with a description.
     *
     * <p>The handler is invoked when a client calls this method. The {@link MinecraftServer}
     * is resolved lazily at call time — {@link #attach(MinecraftServer)} must have been
     * called before any client can invoke this method.</p>
     *
     * @param <Param> The type of the payload received from the client
     * @param <Result> The type of the payload returned to the client
     * @param name The name of this method (e.g. {@code "echo"}),
     * resulting in the identifier {@code namespace:name}
     * @param paramSchema The schema describing the payload received from the client
     * @param resultSchema The schema describing the payload returned to the client
     * @param description A human-readable description of this method
     * @param handler The handler to invoke when this method is called by a client
     * 
     * @return The registered {@link MSMPMethod}
     */
    public <Param, Result> MSMPMethod<Param, Result> method(
        String name,
        Schema<Param> paramSchema,
        Schema<Result> resultSchema,
        String description,
        MSMPMethodHandler<Param, Result> handler
    ) {
        return new MSMPMethod<>(namespace, name, paramSchema, resultSchema, description,
            (api, params, client) -> {
                if (server == null) throw new IllegalStateException(
                    "MSMPNamespace '%s' has no server attached. Call attach(server) in SERVER_STARTED.".formatted(namespace)
                );
                return handler.apply(server, params, client);
            });
    }
}
