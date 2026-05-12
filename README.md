<img src="assets/icon.png" width="64" align="right">

# MSMP Lib

A library that extends the [Minecraft Server Management Protocol](https://minecraft.wiki/w/Minecraft_Server_Management_Protocol) (MSMP) by allowing for custom methods and notifications to be registered with the MSMP registry. Easy to use, customizable, and extensible.

## Installation

Add the following to your `build.gradle`:

```groovy
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.YOUR_USERNAME:msmp-lib:VERSION")
}
```

And declare the dependency in your `fabric.mod.json`:

```json
"depends": {
    "msmp-lib": "*"
}
```

## Usage

### 1. Define your payloads

Payloads are simple records that describe the data sent and received over MSMP.
Each payload needs a `Codec` for serialization and a `Schema` for MSMP discovery.

```java
public record PingPayload(String message) {

    public static final Codec<PingPayload> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.STRING.fieldOf("message").forGetter(PingPayload::message)
    ).apply(i, PingPayload::new));

    public static final Schema<PingPayload> SCHEMA = Schema.record(CODEC)
        .withField("message", Schema.STRING_SCHEMA);
}
```

### 2. Initialize in your mod

Create a `MsmpServer` instance in the `SERVER_STARTED` lifecycle event.
Use `namespace()` to register methods and notifications, and `send()` to broadcast notifications.

```java
public class MyMod implements ModInitializer {

    private static MsmpServer msmp;
    private static MsmpNotification<PingPayload> ping;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            msmp = new MsmpServer(server);

            ping = msmp.namespace("my_mod")
                .notification("ping", PingPayload.SCHEMA, "A ping notification");

            msmp.namespace("my_mod")
                .method("echo",
                    EchoPayload.SCHEMA,
                    EchoPayload.SCHEMA,
                    "Echoes a message back to the client",
                    (server, params, client) -> {
                        System.out.println("Called by connection: " + client.connectionId());
                        return params;
                    }
                );
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            msmp = null;
        });
    }

    // Broadcast a notification to all connected clients
    public static void sendPing() {
        if (msmp != null) msmp.send(ping, new PingPayload("hello"));
    }
}
```

### Handler parameters

The method handler receives three parameters:

| Parameter | Type | Description |
|---|---|---|
| `server` | `MinecraftServer` | The running Minecraft server instance |
| `params` | `Param` | The payload received from the client |
| `client` | `ClientInfo` | Info about the calling client, including `connectionId()` |

### JSON-RPC examples

**Calling a method** (`my_mod:echo`):

```json
{
    "jsonrpc": "2.0",
    "id": 1,
    "method": "my_mod:echo",
    "params": [{
        "message": "hello"
    }]
}
```

**Response:**

```json
{
    "jsonrpc": "2.0",
    "id": 1,
    "result": {
        "message": "hello"
    }
}
```

**Receiving a notification** (`my_mod:notification/ping`):

```json
{
    "jsonrpc": "2.0",
    "method": "my_mod:notification/ping",
    "params": [{
        "message": "hello"
    }]
}
```

## Requirements

- Minecraft with MSMP enabled (`management-server-enabled=true` in `server.properties`)
- The Management Server listens on `localhost:25576` by default

## License

[LGPL-3.0](LICENSE)
