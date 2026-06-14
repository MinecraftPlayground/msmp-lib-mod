package dev.loat.msmp;

import dev.loat.msmp.mixin.IncomingRpcMethodBuilderAccessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.jsonrpc.IncomingRpcMethod;
import net.minecraft.server.jsonrpc.api.Schema;

/**
 * Represents an incoming MSMP method that can be called by connected clients.
 *
 * <p>Instances are created via {@link dev.loat.msmp.builder.MethodBuilderWithSchemas#register(MSMPMethodHandler)}
 * or {@link dev.loat.msmp.builder.MethodBuilderWithoutParameters#register(MSMPMethodHandlerWithoutParameters)}
 * and are registered in the MSMP registry immediately upon creation.</p>
 *
 * <pre>{@code
 * // With request payload:
 * NS.method("echo")
 *     .requestSchema(EchoPayload.SCHEMA)
 *     .responseSchema(EchoPayload.SCHEMA)
 *     .description("Echoes a message back to the client")
 *     .register((server, client, params) -> params);
 *
 * // Without request payload:
 * NS.method("get_time")
 *     .responseSchema(TimePayload.SCHEMA)
 *     .description("Returns the current game time")
 *     .register((server, client) -> new TimePayload(server.getGameTime()));
 * }</pre>
 *
 * @param <Param> The type of the payload received from the client, or {@link Void} for methods without parameters
 * @param <Result> The type of the payload returned to the client
 */
public final class MSMPMethod<Param, Result> {

    /**
     * Creates and registers a new incoming method.
     *
     * @param namespace The namespace to register this method under
     * @param name The name of this method
     * @param paramSchema The schema describing the payload received from the client
     * @param resultSchema The schema describing the payload returned to the client
     * @param description A human-readable description of this method
     * @param rpcFunction The raw RPC function to invoke when this method is called
     */
    @SuppressWarnings("unchecked")
    public MSMPMethod(
        String namespace,
        String name,
        Schema<Param> paramSchema,
        Schema<Result> resultSchema,
        String description,
        IncomingRpcMethod.RpcMethodFunction<Param, Result> rpcFunction
    ) {
        Identifier id = Identifier.fromNamespaceAndPath(namespace, name);
        ((IncomingRpcMethodBuilderAccessor<Param, Result>)
            IncomingRpcMethod.<Param, Result>method(rpcFunction)
                .description(description)
                .param(name, paramSchema)
                .response(name, resultSchema)
        ).invokeRegister(BuiltInRegistries.INCOMING_RPC_METHOD, id);
    }

    /**
     * Creates and registers a new incoming method without parameters.
     *
     * @param namespace The namespace to register this method under
     * @param name The name of this method
     * @param resultSchema The schema describing the payload returned to the client
     * @param description A human-readable description of this method
     * @param rpcFunction The raw RPC function without parameters to invoke when this method is called
     */
    @SuppressWarnings("unchecked")
    public MSMPMethod(
        String namespace,
        String name,
        Schema<Result> resultSchema,
        String description,
        IncomingRpcMethod.ParameterlessRpcMethodFunction<Result> rpcFunction
    ) {
        Identifier id = Identifier.fromNamespaceAndPath(namespace, name);
        ((IncomingRpcMethodBuilderAccessor<Void, Result>)
            IncomingRpcMethod.<Result>method(rpcFunction)
                .description(description)
                .response(name, resultSchema)
        ).invokeRegister(BuiltInRegistries.INCOMING_RPC_METHOD, id);
    }
}
