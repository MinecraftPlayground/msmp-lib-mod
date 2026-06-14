package dev.loat.msmp.builder;

import dev.loat.msmp.MSMPNamespace;
import net.minecraft.server.jsonrpc.api.Schema;

public final class MethodBuilder {

    private final MSMPNamespace msmpNamespace;
    private final String namespace;
    private final String name;

    public MethodBuilder(MSMPNamespace msmpNamespace, String namespace, String name) {
        this.msmpNamespace = msmpNamespace;
        this.namespace = namespace;
        this.name = name;
    }

    public <Param> MethodBuilderWithParameters<Param> requestSchema(Schema<Param> schema) {
        return new MethodBuilderWithParameters<>(msmpNamespace, namespace, name, schema);
    }

    public <Result> MethodBuilderWithoutParameters<Result> responseSchema(Schema<Result> schema) {
        return new MethodBuilderWithoutParameters<>(msmpNamespace, namespace, name, schema);
    }
}
