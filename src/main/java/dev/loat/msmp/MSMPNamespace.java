package dev.loat.msmp;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.jsonrpc.api.Schema;

import java.util.function.BiFunction;


/**
 * Represents a custom MSMP namespace under which methods and notifications can be registered.
 *
 * <p>Instances are created via {@link MSMPServer#namespace(String)} and have access to the
 * running {@link MinecraftServer} instance, which is passed to method handlers.</p>
 *
 * <pre>{@code
 * msmp.namespace("my_mod")
 *     .method("get_time",
 *         VoidPayload.SCHEMA,
 *         TimePayload.SCHEMA,
 *         "Returns the current game time",
 *         (server, params) -> new TimePayload(server.getGameTime())
 *     );
 * }</pre>
 */
public final class MSMPNamespace {

    private final MinecraftServer server;
    private final String namespace;

    /**
     * Creates a new namespace. Package-private — use {@link MSMPServer#namespace(String)}.
     *
     * @param server The running {@link MinecraftServer} instance
     * @param namespace The namespace identifier (e.g. {@code "my_mod"})
     */
    MSMPNamespace(MinecraftServer server, String namespace) {
        this.server = server;
        this.namespace = namespace;
    }

    /**
     * Creates and registers a new outgoing notification without a description.
     *
     * @param <Payload> The type of the payload sent with this notification
     * @param name The name of this notification (e.g. {@code "ping"}),
     * resulting in the identifier {@code namespace:notification/name}
     * @param schema The schema describing the payload structure
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
     * <p>The handler receives the running {@link MinecraftServer} instance and the
     * request payload from the client, and returns a response payload.</p>
     *
     * @param <Param> The type of the payload received from the client
     * @param <Result> The type of the payload returned to the client
     * @param name The name of this method (e.g. {@code "get_time"}),
     * resulting in the identifier {@code namespace:method/name}
     * @param paramSchema The schema describing the payload received from the client
     * @param resultSchema The schema describing the payload returned to the client
     * @param handler The function to invoke when this method is called by a client,
     * receiving the {@link MinecraftServer} and the request payload
     * @return The registered {@link MSMPMethod}
     */
    public <Param, Result> MSMPMethod<Param, Result> method(
        String name,
        Schema<Param> paramSchema,
        Schema<Result> resultSchema,
        BiFunction<MinecraftServer, Param, Result> handler
    ) {
        return new MSMPMethod<>(
            namespace,
            name,
            paramSchema,
            resultSchema,
            "",
            (api, params, client) -> handler.apply(server, params)
        );
    }

    /**
     * Creates and registers a new incoming method with a description.
     *
     * <p>The handler receives the running {@link MinecraftServer} instance and the
     * request payload from the client, and returns a response payload.</p>
     *
     * @param <Param> The type of the payload received from the client
     * @param <Result> The type of the payload returned to the client
     * @param name The name of this method (e.g. {@code "get_time"}),
     * resulting in the identifier {@code namespace:method/name}
     * @param paramSchema The schema describing the payload received from the client
     * @param resultSchema The schema describing the payload returned to the client
     * @param description A human-readable description of this method
     * @param handler The function to invoke when this method is called by a client,
     * receiving the {@link MinecraftServer} and the request payload
     * @return The registered {@link MSMPMethod}
     */
    public <Param, Result> MSMPMethod<Param, Result> method(
        String name,
        Schema<Param> paramSchema,
        Schema<Result> resultSchema,
        String description,
        BiFunction<MinecraftServer, Param, Result> handler
    ) {
        return new MSMPMethod<>(
            namespace,
            name,
            paramSchema,
            resultSchema,
            description,
            (api, params, client) -> handler.apply(server, params)
        );
    }
}
