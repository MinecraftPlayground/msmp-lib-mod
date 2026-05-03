package dev.loat.msmp.mixin;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.jsonrpc.IncomingRpcMethod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;


/**
 * Mixin accessor for the private
 * {@link IncomingRpcMethod.IncomingRpcMethodBuilder#register(Registry, Identifier)} method.
 *
 * <p>The public {@code register(Registry, String)} overload always prepends
 * {@code minecraft:} as the namespace via {@link Identifier#withDefaultNamespace(String)},
 * making it impossible to register methods under a custom namespace.
 * This accessor exposes the private {@code register(Registry, Identifier)} overload directly,
 * allowing a fully custom {@link Identifier} (e.g. {@code entity_data:get_position})
 * to be used.</p>
 *
 * <p>Used internally by {@link dev.loat.msmp.MSMPMethod} to register methods
 * under a custom namespace.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * ((IncomingRpcMethodBuilderAccessor<MyParam, MyResult>) IncomingRpcMethod
 *     .<MyParam, MyResult>method(handler)
 *     .description("...")
 *     .param("param", PARAM_SCHEMA)
 *     .response("result", RESULT_SCHEMA)
 * ).invokeRegister(
 *     BuiltInRegistries.INCOMING_RPC_METHOD,
 *     Identifier.fromNamespaceAndPath("my_mod", "my_method")
 * );
 * }</pre>
 *
 * @param <Param>  the type of the method parameter payload
 * @param <Result> the type of the method result payload
 */
@Mixin(IncomingRpcMethod.IncomingRpcMethodBuilder.class)
public interface IncomingRpcMethodBuilderAccessor<Params, Result> {


    /**
     * Invokes the private {@code register(Registry, Identifier)} method on
     * {@link IncomingRpcMethod.IncomingRpcMethodBuilder}, registering the built
     * {@link IncomingRpcMethod} under the given fully qualified {@link Identifier}
     * without any namespace defaulting.
     *
     * @param registry The registry to register the method in
     * @param id The fully qualified identifier for the method
     * 
     * @return The registered {@link IncomingRpcMethod}
     */
    @Invoker("register")
    IncomingRpcMethod<?, ?> invokeRegister(Registry<IncomingRpcMethod<?, ?>> registry, Identifier id);
}
