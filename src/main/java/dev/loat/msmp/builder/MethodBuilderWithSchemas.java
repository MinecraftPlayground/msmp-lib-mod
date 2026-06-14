package dev.loat.msmp.builder;

import dev.loat.msmp.MSMPMethod;
import dev.loat.msmp.MSMPMethodHandler;
import dev.loat.msmp.MSMPMethodHandlerWithoutParameters;
import net.minecraft.server.jsonrpc.api.Schema;

/**
 * Final stage of the method builder — both schemas are known.
 *
 * <p>Optionally call {@link #description(String)} before calling {@link #register(MsmpMethodHandler)}
 * or {@link #register(MsmpParameterlessMethodHandler)} to complete the registration.</p>
 *
 * @param <Param>  the type of the request payload, or {@link Void} for parameterless methods
 * @param <Result> the type of the response payload
 */
public final class MethodBuilderWithSchemas<Param, Result> {

    private final String namespace;
    private final String name;
    private final Schema<Param> paramSchema;
    private final Schema<Result> resultSchema;
    private String description = "";

    MethodBuilderWithSchemas(String namespace, String name, Schema<Param> paramSchema, Schema<Result> resultSchema) {
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
     * <p>Use this when {@link MethodBuilder#requestSchema(Schema)} was called.</p>
     *
     * @param handler the handler to invoke when this method is called by a client
     * @return the registered {@link MsmpMethod}
     */
    public MSMPMethod<Param, Result> register(MSMPMethodHandler<Param, Result> handler) {
        return new MSMPMethod<>(namespace, name, paramSchema, resultSchema, description, handler);
    }

    /**
     * Registers the method with a parameterless handler.
     *
     * <p>Use this when {@link MethodBuilder#responseSchema(Schema)} was called directly,
     * skipping {@link MethodBuilder#requestSchema(Schema)}.</p>
     *
     * @param handler the handler to invoke when this method is called by a client
     * @return the registered {@link MsmpMethod}
     */
    public MSMPMethod<Param, Result> register(MSMPMethodHandlerWithoutParameters<Result> handler) {
        return new MSMPMethod<>(namespace, name, resultSchema, description, handler);
    }
}
