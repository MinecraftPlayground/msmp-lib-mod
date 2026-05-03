package dev.loat.msmp.mixin;

import net.minecraft.server.jsonrpc.Connection;
import net.minecraft.server.jsonrpc.ManagementServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import java.util.function.Consumer;


/**
 * Mixin accessor for the package-private
 * {@link ManagementServer#forEachConnection(Consumer)} method.
 *
 * <p>{@link ManagementServer#forEachConnection(Consumer)} is package-private and therefore
 * not directly callable from outside {@code net.minecraft.server.jsonrpc}. This accessor
 * exposes it so that all active WebSocket connections can be iterated and notifications
 * can be dispatched to each one.</p>
 *
 * <p>Used internally by {@link dev.loat.msmp.MSMPServer#send(dev.loat.msmp.MSMPNotification, Object)}
 * to broadcast notifications to all connected clients.</p>
 */
@Mixin(ManagementServer.class)
public interface ManagementServerAccessor {

    /**
     * Invokes the package-private {@code forEachConnection(Consumer)} method on
     * {@link ManagementServer}, calling the given {@code consumer} for each currently
     * active {@link Connection}.
     *
     * @param consumer the action to perform for each active connection,
     * e.g. sending a notification via {@link Connection#sendNotification}
     */
    @Invoker("forEachConnection")
    void invokeForEachConnection(Consumer<Connection> consumer);
}
