package dev.loat.msmp.builder;

import net.minecraft.server.jsonrpc.api.Schema;

/**
 * First stage of the notification builder — only the notification name is known.
 *
 * <p>Call {@link #responseSchema(Schema)} to define the payload type.</p>
 */
public final class NotificationBuilder {

    private final String namespace;
    private final String name;

    NotificationBuilder(String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    /**
     * Sets the response schema for this notification.
     *
     * @param <Payload> the type of the payload sent with this notification
     * @param schema    the schema describing the payload structure
     * @return a {@link NotificationBuilderWithSchema} with the schema set
     */
    public <Payload> NotificationBuilderWithSchema<Payload> responseSchema(Schema<Payload> schema) {
        return new NotificationBuilderWithSchema<>(namespace, name, schema);
    }
}
