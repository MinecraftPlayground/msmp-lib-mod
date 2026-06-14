package dev.loat.msmp.builder;

import net.minecraft.server.jsonrpc.api.Schema;

public final class NotificationBuilder {

    private final String namespace;
    private final String name;
    private String description = "";

    public NotificationBuilder(String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    public NotificationBuilder description(String description) {
        this.description = description;
        return this;
    }

    public <Payload> NotificationBuilderWithSchema<Payload> responseSchema(Schema<Payload> schema) {
        return new NotificationBuilderWithSchema<>(namespace, name, schema, description);
    }
}
