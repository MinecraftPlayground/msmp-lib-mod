package dev.loat.msmp.builder;

import dev.loat.msmp.MSMPNamespace;
import net.minecraft.server.jsonrpc.api.Schema;

public final class MethodBuilderWithParameters<Param> {

    private final MSMPNamespace msmpNamespace;
    private final String namespace;
    private final String name;
    private final Schema<Param> paramSchema;
    private String description;

    MethodBuilderWithParameters(MSMPNamespace msmpNamespace, String namespace, String name, Schema<Param> paramSchema, String description) {
        this.msmpNamespace = msmpNamespace;
        this.namespace = namespace;
        this.name = name;
        this.paramSchema = paramSchema;
        this.description = description;
    }

    public MethodBuilderWithParameters<Param> description(String description) {
        this.description = description;
        return this;
    }

    public <Result> MethodBuilderWithSchemas<Param, Result> responseSchema(Schema<Result> schema) {
        return new MethodBuilderWithSchemas<>(msmpNamespace, namespace, name, paramSchema, schema, description);
    }
}

