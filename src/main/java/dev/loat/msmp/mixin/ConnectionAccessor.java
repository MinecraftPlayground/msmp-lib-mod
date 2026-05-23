package dev.loat.msmp.mixin;

import net.minecraft.server.jsonrpc.Connection;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


/**
 * Mixin accessor for the private {@code clientInfo} field of {@link Connection}.
 *
 * <p>Used internally by {@link dev.loat.msmp.MsmpServer#sendTo(Integer, dev.loat.msmp.MsmpNotification, Object)}
 * to identify connections by their {@link ClientInfo#connectionId()} and send
 * notifications to a specific client.</p>
 */
@Mixin(Connection.class)
public interface ConnectionAccessor {

    /**
     * Returns the {@link ClientInfo} associated with this connection,
     * containing the client's {@code connectionId}.
     *
     * @return the {@link ClientInfo} of this connection
     */
    @Accessor("clientInfo")
    ClientInfo getClientInfo();
}
