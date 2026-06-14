package dev.loat.msmp;

import dev.loat.msmp.mixin.IncomingRpcMethodBuilderAccessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.jsonrpc.IncomingRpcMethod;
import net.minecraft.server.jsonrpc.api.Schema;

/**
 * Represents an incoming MSMP method that can be called by connected clients.
 *
 * <p>Instances are created via {@link dev.loat.msmp.builder.MethodBuilderWithSchemas#register(MsmpMethodHandler)}
 * or {@link dev.loat.msmp.builder.MethodBuilderWithSchemas#register(MsmpParameterlessMethodHandler)}
 * and are registered in the MSMP registry immediately upon creation.</p>
 *
 * <pre>{@code
 * // With request payload:
 * NS.method("echo")
 *     .requestSchema(EchoPayload.SCHEMA)
 *     .responseSchema(EchoPayload.SCHEMA)
 *     .description("Echoes a message back to the client")
 *     .register((server, params, client) -> params);
 *
 * // Without request payload:
 * NS.method("get_time")
 *     .responseSchema(TimePayload.SCHEMA)
 *     .description("Returns the current game time")
 *     .register((server, client) -> new TimePayload(server.getGameTime()));
 * }</pre>
 *
 * @param <Param>  the type of the payload received from the client, or {@link Void} for parameterless methods
 * @param <Result> the type of the payload returned to the client
 */
public final class MSMPMethod<Param, Result> {

    /**
     * Creates and registers a new incoming method with a request payload.
     * Package-private — use {@link dev.loat.msmp.builder.MethodBuilderWithSchemas#register(MsmpMethodHandler)}.
     *
     * @param namespace    the namespace to register this method under
     * @param name         the name of this method
     * @param paramSchema  the schema describing the payload received from the client
     * @param resultSchema the schema describing the payload returned to the client
     * @param description  a human-readable description of this method
     * @param handler      the handler to invoke when this method is called by a client
     */
    @SuppressWarnings("unchecked")
    MSMPMethod(
        String namespace,
        String name,
        Schema<Param> paramSchema,
        Schema<Result> resultSchema,
        String description,
        MSMPMethodHandler<Param, Result> handler
    ) {
        Identifier id = Identifier.fromNamespaceAndPath(namespace, name);
        ((IncomingRpcMethodBuilderAccessor<Param, Result>)
            IncomingRpcMethod.<Param, Result>method(
                (api, params, client) -> handler.apply(null, params, client)
            )
            .description(description)
            .param(name, paramSchema)
            .response(name, resultSchema)
        ).invokeRegister(BuiltInRegistries.INCOMING_RPC_METHOD, id);
    }

    /**
     * Creates and registers a new parameterless incoming method.
     * Package-private — use {@link dev.loat.msmp.builder.MethodBuilderWithSchemas#register(MsmpParameterlessMethodHandler)}.
     *
     * @param namespace    the namespace to register this method under
     * @param name         the name of this method
     * @param resultSchema the schema describing the payload returned to the client
     * @param description  a human-readable description of this method
     * @param handler      the parameterless handler to invoke when this method is called by a client
     */
    @SuppressWarnings("unchecked")
    MSMPMethod(
        String namespace,
        String name,
        Schema<Result> resultSchema,
        String description,
        MSMPMethodHandlerWithoutParameters<Result> handler
    ) {
        Identifier id = Identifier.fromNamespaceAndPath(namespace, name);
        ((IncomingRpcMethodBuilderAccessor<Void, Result>)
            IncomingRpcMethod.<Result>method(
                (api, client) -> handler.apply(null, client)
            )
            .description(description)
            .response(name, resultSchema)
        ).invokeRegister(BuiltInRegistries.INCOMING_RPC_METHOD, id);
    }
}
