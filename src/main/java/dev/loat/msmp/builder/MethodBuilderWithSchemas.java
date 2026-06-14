package dev.loat.msmp.builder;

import dev.loat.msmp.MSMPMethod;
import dev.loat.msmp.MSMPMethodHandler;
import dev.loat.msmp.MSMPNamespace;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.jsonrpc.IncomingRpcMethod;
import net.minecraft.server.jsonrpc.api.Schema;

/**
 * Final stage of the method builder — both schemas are known.
 *
 * <p>Optionally call {@link #description(String)} before calling
 * {@link #register(MSMPMethodHandler)} to complete the registration.</p>
 *
 * @param <Param> The type of the request payload
 * @param <Result> The type of the response payload
 */
public final class MethodBuilderWithSchemas<Param, Result> {

    private final MSMPNamespace msmpNamespace;
    private final String namespace;
    private final String name;
    private final Schema<Param> paramSchema;
    private final Schema<Result> resultSchema;
    private String description = "";

    MethodBuilderWithSchemas(MSMPNamespace msmpNamespace, String namespace, String name, Schema<Param> paramSchema, Schema<Result> resultSchema) {
        this.msmpNamespace = msmpNamespace;
        this.namespace = namespace;
        this.name = name;
        this.paramSchema = paramSchema;
        this.resultSchema = resultSchema;
    }

    /**
     * Sets the description for this method.
     *
     * @param description a human-readable description of this method
     * @return this builder
     */
    public MethodBuilderWithSchemas<Param, Result> description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Registers the method with a handler that receives the request payload.
     *
     * @param handler The handler to invoke when this method is called by a client
     * @return the registered {@link MSMPMethod}
     */
    public MSMPMethod<Param, Result> register(MSMPMethodHandler<Param, Result> handler) {
        IncomingRpcMethod.RpcMethodFunction<Param, Result> rpcFunction = (api, params, client) -> {
            MinecraftServer server = msmpNamespace.getServer();
            if (server == null) throw new IllegalStateException(
                "MSMPNamespace '%s' has no server attached. Call attach(server) in SERVER_STARTED.".formatted(namespace)
            );
            return handler.apply(server, client, params);
        };
        return new MSMPMethod<>(namespace, name, paramSchema, resultSchema, description, rpcFunction);
    }
}
