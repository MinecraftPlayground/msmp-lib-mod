package dev.loat.msmp.builder;

import dev.loat.msmp.MSMPMethod;
import dev.loat.msmp.MSMPMethodHandlerWithoutParameters;
import dev.loat.msmp.MSMPNamespace;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.jsonrpc.IncomingRpcMethod;
import net.minecraft.server.jsonrpc.api.Schema;

/**
 * Final stage of the method builder for methods without parameters — the response schema is known.
 *
 * <p>Optionally call {@link #description(String)} before calling
 * {@link #register(MSMPMethodHandlerWithoutParameters)} to complete the registration.</p>
 *
 * @param <Result> The type of the response payload
 */
public final class MethodBuilderWithoutParameters<Result> {

    private final MSMPNamespace msmpNamespace;
    private final String namespace;
    private final String name;
    private final Schema<Result> resultSchema;
    private String description = "";

    MethodBuilderWithoutParameters(MSMPNamespace msmpNamespace, String namespace, String name, Schema<Result> resultSchema) {
        this.msmpNamespace = msmpNamespace;
        this.namespace = namespace;
        this.name = name;
        this.resultSchema = resultSchema;
    }

    /**
     * Sets the description for this method.
     *
     * @param description a human-readable description of this method
     * @return this builder
     */
    public MethodBuilderWithoutParameters<Result> description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Registers the method without parameters.
     *
     * @param handler The handler to invoke when this method is called by a client
     * @return the registered {@link MSMPMethod}
     */
    public MSMPMethod<Void, Result> register(MSMPMethodHandlerWithoutParameters<Result> handler) {
        IncomingRpcMethod.ParameterlessRpcMethodFunction<Result> rpcFunction = (api, client) -> {
            MinecraftServer server = msmpNamespace.getServer();
            if (server == null) throw new IllegalStateException(
                "MSMPNamespace '%s' has no server attached. Call attach(server) in SERVER_STARTED.".formatted(namespace)
            );
            return handler.apply(server, client);
        };
        return new MSMPMethod<>(namespace, name, resultSchema, description, rpcFunction);
    }
}
