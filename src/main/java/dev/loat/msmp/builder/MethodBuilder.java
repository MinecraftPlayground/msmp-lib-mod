package dev.loat.msmp.builder;

import net.minecraft.server.jsonrpc.api.Schema;

/**
 * First stage of the method builder — only the method name is known.
 *
 * <p>Call {@link #requestSchema(Schema)} to define the request payload type,
 * or {@link #responseSchema(Schema)} directly for a parameterless method.</p>
 */
public final class MethodBuilder {

    private final String namespace;
    private final String name;

    MethodBuilder(String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    /**
     * Sets the request schema for this method.
     *
     * <p>Call this if the method expects a payload from the client.
     * Skip this and call {@link #responseSchema(Schema)} directly for a parameterless method.</p>
     *
     * @param <Param> the type of the request payload
     * @param schema  the schema describing the request payload
     * @return a {@link MethodBuilderWithParam} with the request schema set
     */
    public <Param> MethodBuilderWithParameters<Param> requestSchema(Schema<Param> schema) {
        return new MethodBuilderWithParameters<>(namespace, name, schema);
    }

    /**
     * Sets the response schema for this method, skipping the request schema.
     *
     * <p>Use this for parameterless methods that don't expect a payload from the client.</p>
     *
     * @param <Result> the type of the response payload
     * @param schema   the schema describing the response payload
     * @return a {@link MethodBuilderWithSchemas} with no request schema and the response schema set
     */
    public <Result> MethodBuilderWithSchemas<Void, Result> responseSchema(Schema<Result> schema) {
        return new MethodBuilderWithSchemas<>(namespace, name, null, schema);
    }
}
