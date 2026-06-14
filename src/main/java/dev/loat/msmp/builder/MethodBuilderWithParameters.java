package dev.loat.msmp.builder;

import net.minecraft.server.jsonrpc.api.Schema;

/**
 * Second stage of the method builder — the request schema is known.
 *
 * <p>Call {@link #responseSchema(Schema)} to define the response payload type.</p>
 *
 * @param <Param> the type of the request payload
 */
public final class MethodBuilderWithParameters<Param> {

    private final String namespace;
    private final String name;
    private final Schema<Param> paramSchema;

    MethodBuilderWithParameters(String namespace, String name, Schema<Param> paramSchema) {
        this.namespace = namespace;
        this.name = name;
        this.paramSchema = paramSchema;
    }

    /**
     * Sets the response schema for this method.
     *
     * @param <Result> the type of the response payload
     * @param schema   the schema describing the response payload
     * @return a {@link MethodBuilderWithSchemas} with both schemas set
     */
    public <Result> MethodBuilderWithSchemas<Param, Result> responseSchema(Schema<Result> schema) {
        return new MethodBuilderWithSchemas<>(namespace, name, paramSchema, schema);
    }
}
