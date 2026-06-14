package dev.loat.msmp.builder;

import dev.loat.msmp.MSMPNotification;
import net.minecraft.server.jsonrpc.api.Schema;

public final class NotificationBuilderWithSchema<Payload> {

    private final String namespace;
    private final String name;
    private final Schema<Payload> schema;
    private String description = "";

    public NotificationBuilderWithSchema(String namespace, String name, Schema<Payload> schema, String description) {
        this.namespace = namespace;
        this.name = name;
        this.schema = schema;
        this.description = description;
    }

    public NotificationBuilderWithSchema(String namespace, String name, Schema<Payload> schema) {
        this.namespace = namespace;
        this.name = name;
        this.schema = schema;
        this.description = "";
    }

    public NotificationBuilderWithSchema<Payload> description(String description) {
        this.description = description;
        return this;
    }

    public MSMPNotification<Payload> register() {
        return new MSMPNotification<>(namespace, name, schema, description);
    }
}
