package dev.loat.msmp;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.jsonrpc.methods.ClientInfo;

/**
 * Functional interface for handling parameterless incoming MSMP method calls.
 *
 * <p>Use this when no request payload is expected from the client.</p>
 *
 * @param <Result> the type of the payload returned to the client
 */
@FunctionalInterface
public interface MSMPMethodHandlerWithoutParameters<Result> {

    /**
     * Handles an incoming parameterless method call from a connected client.
     *
     * @param server the running {@link MinecraftServer} instance
     * @param client the {@link ClientInfo} of the calling client
     * @return the payload to return to the client
     */
    Result apply(MinecraftServer server, ClientInfo client);
}
