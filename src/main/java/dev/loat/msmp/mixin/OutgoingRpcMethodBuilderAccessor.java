package dev.loat.msmp.mixin;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.jsonrpc.OutgoingRpcMethod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;


/**
 * Mixin accessor for the private
 * {@link OutgoingRpcMethod.OutgoingRpcMethodBuilder#register(Identifier)} method.
 *
 * <p>The public {@code register(String)} overload always prepends {@code minecraft:notification/}
 * as the namespace, making it impossible to register notifications under a custom namespace.
 * This accessor exposes the private {@code register(Identifier)} overload directly, allowing
 * a fully custom {@link Identifier} (e.g. {@code entity_data:notification/ping}) to be used.</p>
 *
 * <p>Used internally by {@link dev.loat.msmp.MSMPNotification} to register notifications
 * under a custom namespace.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * ((OutgoingRpcMethodBuilderAccessor<MyPayload, Void>) OutgoingRpcMethod
 *     .<MyPayload>notificationWithParams()
 *     .description("...")
 *     .param("param", MY_SCHEMA)
 * ).invokeRegister(Identifier.fromNamespaceAndPath("my_mod", "notification/my_event"));
 * }</pre>
 *
 * @param <Payload> The type of the notification payload
 * @param <Result> The type of the result (typically {@link Void})
 */
@Mixin(OutgoingRpcMethod.OutgoingRpcMethodBuilder.class)
public interface OutgoingRpcMethodBuilderAccessor<Params, Result> {

    /**
     * Invokes the private {@code register(Identifier)} method on
     * {@link OutgoingRpcMethod.OutgoingRpcMethodBuilder}, registering the built
     * {@link OutgoingRpcMethod} under the given fully qualified {@link Identifier}
     * without any namespace defaulting.
     *
     * @param id The fully qualified identifier for the notification
     * 
     * @return A {@link Holder.Reference} to the registered {@link OutgoingRpcMethod}
     */
    @Invoker("register")
    Holder.Reference<OutgoingRpcMethod<Params, Result>> invokeRegister(Identifier id);
}
