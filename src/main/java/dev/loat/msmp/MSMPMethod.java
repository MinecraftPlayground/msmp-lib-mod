package dev.loat.msmp;

import dev.loat.msmp.mixin.IncomingRpcMethodBuilderAccessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.jsonrpc.IncomingRpcMethod;
import net.minecraft.server.jsonrpc.api.Schema;


/**
 * Represents an incoming MSMP method that can be called by connected clients.
 *
 * <p>Instances are created via
 * {@link MSMPNamespace#method(String, Schema, Schema, IncomingRpcMethod.RpcMethodFunction)} or
 * {@link MSMPNamespace#method(String, Schema, Schema, String, IncomingRpcMethod.RpcMethodFunction)}
 * and are registered in the MSMP registry immediately upon creation.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * public static final MSMPMethod<EntityIdPayload, PositionPayload> GET_POSITION = NS.method(
 *     "get_position",
 *     EntityIdPayload.SCHEMA,
 *     PositionPayload.SCHEMA,
 *     "Returns the position of an entity",
 *     (api, params, client) -> getEntityPosition(params.entityId())
 * );
 * }</pre>
 *
 * @param <Param>  the type of the payload received from the client
 * @param <Result> the type of the payload returned to the client
 */
public final class MSMPMethod<Param, Result> {

    /**
     * Creates and registers a new incoming method under the given namespace.
     *
     * @param namespace The namespace to register this method under (e.g. {@code "entity_data"})
     * @param name The name of this method (e.g. {@code "get_position"}),
     * resulting in the identifier {@code namespace:name}
     * @param paramSchema The schema describing the payload structure received from the client
     * @param resultSchema The schema describing the payload structure returned to the client
     * @param description A human-readable description of this method
     * @param handler The function to invoke when this method is called by a client
     */
    @SuppressWarnings("unchecked")
    MSMPMethod(
        String namespace,
        String name,
        Schema<Param> paramSchema,
        Schema<Result> resultSchema,
        String description,
        IncomingRpcMethod.RpcMethodFunction<Param, Result> handler
    ) {
        Identifier id = Identifier.fromNamespaceAndPath(namespace, name);
        ((IncomingRpcMethodBuilderAccessor<Param, Result>) IncomingRpcMethod
            .<Param, Result>method(handler)
            .description(description)
            .param(name, paramSchema)
            .response(name, resultSchema)
        ).invokeRegister(BuiltInRegistries.INCOMING_RPC_METHOD, id);
    }
}
