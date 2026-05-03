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
 * {@link MSMPNamespace#method(String, Schema, Schema, java.util.function.BiFunction)} or
 * {@link MSMPNamespace#method(String, Schema, Schema, String, java.util.function.BiFunction)}
 * and are registered in the MSMP registry immediately upon creation.</p>
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
 *
 * @param <Param>  the type of the payload received from the client
 * @param <Result> the type of the payload returned to the client
 */
public final class MSMPMethod<Param, Result> {

    /**
     * Creates and registers a new incoming method under the given namespace.
     * Package-private — use
     * {@link MSMPNamespace#method(String, Schema, Schema, java.util.function.BiFunction)} or
     * {@link MSMPNamespace#method(String, Schema, Schema, String, java.util.function.BiFunction)}.
     *
     * @param namespace The namespace to register this method under (e.g. {@code "my_mod"})
     * @param name The name of this method (e.g. {@code "get_time"}),
     * resulting in the identifier {@code namespace:method/name}
     * @param paramSchema The schema describing the payload received from the client
     * @param resultSchema The schema describing the payload returned to the client
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
