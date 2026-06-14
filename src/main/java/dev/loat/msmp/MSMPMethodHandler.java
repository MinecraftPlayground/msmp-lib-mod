package dev.loat.msmp;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.jsonrpc.methods.ClientInfo;


/**
 * Functional interface for handling incoming MSMP method calls.
 *
 * <p>Provides access to the running {@link MinecraftServer}, the request payload
 * from the client, and the {@link ClientInfo} containing the client's connection ID.</p>
 *
 * @param <Param> The type of the payload received from the client
 * @param <Result> The type of the payload returned to the client
 */
@FunctionalInterface
public interface MSMPMethodHandler<Param, Result> {

    /**
     * Handles an incoming method call from a connected client.
     *
     * @param server The running {@link MinecraftServer} instance
     * @param client The {@link ClientInfo} of the calling client,
     * providing access to the client's {@link ClientInfo#connectionId()}
     * @param params The payload received from the client
     * @return The payload to return to the client
     */
    Result apply(MinecraftServer server, ClientInfo client, Param params);
}
