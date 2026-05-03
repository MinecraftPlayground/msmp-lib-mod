package dev.loat.msmp;

import dev.loat.msmp.mixin.OutgoingRpcMethodBuilderAccessor;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.jsonrpc.OutgoingRpcMethod;
import net.minecraft.server.jsonrpc.api.Schema;

/**
 * Represents an outgoing MSMP notification that can be broadcast to all connected clients.
 *
 * <p>Instances are created via {@link MSMPNamespace#notification(String, Schema)} or
 * {@link MSMPNamespace#notification(String, Schema, String)} and are registered
 * in the MSMP registry immediately upon creation.</p>
 *
 * <p>Use {@link MSMPServer#send(MSMPNotification, Object)} to broadcast this notification.</p>
 *
 * <pre>{@code
 * MSMPNotification<PingPayload> ping = msmp.namespace("my_mod")
 *     .notification("ping", PingPayload.SCHEMA, "A ping notification");
 *
 * msmp.send(ping, new PingPayload("hello"));
 * }</pre>
 *
 * @param <Payload> The type of the payload sent with this notification
 */
public final class MSMPNotification<Payload> {

    private final Holder.Reference<OutgoingRpcMethod<Payload, Void>> holder;

    /**
     * Creates and registers a new outgoing notification under the given namespace.
     * Package-private — use {@link MSMPNamespace#notification(String, Schema)} or
     * {@link MSMPNamespace#notification(String, Schema, String)}.
     *
     * @param namespace The namespace to register this notification under (e.g. {@code "my_mod"})
     * @param name The name of this notification (e.g. {@code "ping"}),
     * resulting in the identifier {@code namespace:notification/name}
     * @param schema The schema describing the payload structure
     * @param description A human-readable description of this notification
     */
    @SuppressWarnings("unchecked")
    MSMPNotification(String namespace, String name, Schema<Payload> schema, String description) {
        this.holder = ((OutgoingRpcMethodBuilderAccessor<Payload, Void>) OutgoingRpcMethod
            .<Payload>notificationWithParams()
            .description(description)
            .param(name, schema)
        ).invokeRegister(Identifier.fromNamespaceAndPath(namespace, "notification/" + name));
    }

    /**
     * Returns the registered {@link OutgoingRpcMethod} holder for this notification.
     *
     * <p>Used internally by {@link MSMPServer#send(MSMPNotification, Object)}
     * to broadcast this notification to all connected clients.</p>
     *
     * @return The holder reference to the registered outgoing RPC method
     */
    Holder.Reference<OutgoingRpcMethod<Payload, Void>> holder() {
        return holder;
    }
}
