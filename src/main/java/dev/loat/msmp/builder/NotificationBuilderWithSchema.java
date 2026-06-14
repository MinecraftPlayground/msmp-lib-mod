package dev.loat.msmp.builder;

import dev.loat.msmp.MSMPNotification;
import net.minecraft.server.jsonrpc.api.Schema;

/**
 * Final stage of the notification builder — the schema is known.
 *
 * <p>Optionally call {@link #description(String)} before calling {@link #register()}
 * to complete the registration.</p>
 *
 * @param <Payload> the type of the payload sent with this notification
 */
public final class NotificationBuilderWithSchema<Payload> {

    private final String namespace;
    private final String name;
    private final Schema<Payload> schema;
    private String description = "";

    NotificationBuilderWithSchema(String namespace, String name, Schema<Payload> schema) {
        this.namespace = namespace;
        this.name = name;
        this.schema = schema;
    }

    /**
     * Sets the description for this notification.
     *
     * @param description a human-readable description of this notification
     * @return this builder
     */
    public NotificationBuilderWithSchema<Payload> description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Registers the notification.
     *
     * @return the registered {@link MsmpNotification}
     */
    public MSMPNotification<Payload> register() {
        return new MSMPNotification<>(namespace, name, schema, description);
    }
}
