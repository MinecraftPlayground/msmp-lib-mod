package dev.loat.msmp;

import net.minecraft.server.jsonrpc.IncomingRpcMethod;
import net.minecraft.server.jsonrpc.api.Schema;


/**
 * Represents a custom MSMP namespace under which notifications and methods can be registered.
 *
 * <p>A namespace groups related notifications and methods under a common identifier prefix.
 * All registrations are performed immediately when {@link #notification} or {@link #method}
 * is called, making them suitable for use as {@code static final} fields.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * public class MyModMSMP {
 *     private static final MSMPNamespace NS = new MSMPNamespace("entity_data");
 *
 *     public static final MSMPNotification<PingPayload> PING =
 *         NS.notification("ping", PingPayload.SCHEMA, "Sends a ping");
 *
 *     public static final MSMPMethod<EntityIdPayload, PositionPayload> GET_POSITION =
 *         NS.method(
 *             "get_position",
 *             EntityIdPayload.SCHEMA,
 *             PositionPayload.SCHEMA,
 *             "Returns the position of an entity",
 *             (api, params, client) -> getEntityPosition(params.entityId())
 *         );
 * }
 * }</pre>
 */
public final class MSMPNamespace {

    private final String namespace;

    /**
     * Creates a new namespace with the given identifier.
     *
     * @param namespace The namespace identifier (e.g. {@code "entity_data"})
     */
    public MSMPNamespace(String namespace) {
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
     * @param <Param> The type of the payload received from the client
     * @param <Result> The type of the payload returned to the client
     * @param name The name of this method (e.g. {@code "get_position"}),
     * resulting in the identifier {@code namespace:name}
     * @param paramSchema The schema describing the payload structure received from the client
     * @param resultSchema The schema describing the payload structure returned to the client
     * @param handler The function to invoke when this method is called by a client
     * 
     * @return The registered {@link MSMPMethod}
     */
    public <Param, Result> MSMPMethod<Param, Result> method(
        String name,
        Schema<Param> paramSchema,
        Schema<Result> resultSchema,
        IncomingRpcMethod.RpcMethodFunction<Param, Result> handler
    ) {
        return new MSMPMethod<>(namespace, name, paramSchema, resultSchema, "", handler);
    }

    /**
     * Creates and registers a new incoming method with a description.
     *
     * @param <Param> The type of the payload received from the client
     * @param <Result> The type of the payload returned to the client
     * @param name The name of this method (e.g. {@code "get_position"}),
     * resulting in the identifier {@code namespace:name}
     * @param paramSchema The schema describing the payload structure received from the client
     * @param resultSchema The schema describing the payload structure returned to the client
     * @param description A human-readable description of this method
     * @param handler The function to invoke when this method is called by a client
     * 
     * @return The registered {@link MSMPMethod}
     */
    public <Param, Result> MSMPMethod<Param, Result> method(
        String name,
        Schema<Param> paramSchema,
        Schema<Result> resultSchema,
        String description,
        IncomingRpcMethod.RpcMethodFunction<Param, Result> handler
    ) {
        return new MSMPMethod<>(namespace, name, paramSchema, resultSchema, description, handler);
    }
}
