package dev.loat.msmp;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.jsonrpc.methods.ClientInfo;

/**
 * Functional interface for handling incoming MSMP method calls without parameters.
 *
 * <p>Use this when no request payload is expected from the client.</p>
 *
 * @param <Result> The type of the payload returned to the client
 */
@FunctionalInterface
public interface MSMPMethodHandlerWithoutParameters<Result> {

    /**
     * Handles an incoming method call without parameters from a connected client.
     *
     * @param server The running {@link MinecraftServer} instance
     * @param client The {@link ClientInfo} of the calling client
     * @return the payload to return to the client
     */
    Result apply(MinecraftServer server, ClientInfo client);
}
