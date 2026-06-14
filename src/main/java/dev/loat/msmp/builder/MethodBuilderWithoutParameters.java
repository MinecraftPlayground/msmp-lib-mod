package dev.loat.msmp.builder;

import dev.loat.msmp.MSMPMethodHandler;
import dev.loat.msmp.MSMPMethodHandlerWithoutParameters;
import dev.loat.msmp.MSMPMethod;
import dev.loat.msmp.MSMPNamespace;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.jsonrpc.api.Schema;

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

    public MethodBuilderWithoutParameters<Result> description(String description) {
        this.description = description;
        return this;
    }

    public MSMPMethod<Void, Result> register(MSMPMethodHandlerWithoutParameters<Result> handler) {
        return new MSMPMethod<>(namespace, name, resultSchema, description,
            (api, client) -> {
                MinecraftServer server = msmpNamespace.getServer();
                if (server == null) throw new IllegalStateException(
                    "MsmpNamespace '%s' has no server attached. Call attach(server) in SERVER_STARTED.".formatted(namespace)
                );
                return handler.apply(server, client);
            });
    }
}
