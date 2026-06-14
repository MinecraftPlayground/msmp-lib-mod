package dev.loat.msmp.builder;

import net.minecraft.server.jsonrpc.api.Schema;

public final class NotificationBuilder {

    private final String namespace;
    private final String name;

    public NotificationBuilder(String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    public <Payload> NotificationBuilderWithSchema<Payload> responseSchema(Schema<Payload> schema) {
        return new NotificationBuilderWithSchema<>(namespace, name, schema);
    }
}
