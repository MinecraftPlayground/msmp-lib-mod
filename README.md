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

### 2. Register methods and notifications

Create a `MsmpNamespace` and register methods and notifications in `onInitialize()` —
before the server starts and the registry is frozen.

Use `attach()` and `detach()` in the server lifecycle events to bind and release
the server instance for use in method handlers.

```java
public class MyMod implements ModInitializer {

    private static final MsmpNamespace NS = new MsmpNamespace("my_mod");

    // Registered immediately as static fields — before the server starts
    private static final MsmpNotification<PingPayload> PING =
        NS.notification("ping", PingPayload.SCHEMA, "A ping notification");

    private static MsmpServer msmp;

    @Override
    public void onInitialize() {
        // Methods are registered here — registry is still open
        NS.method("echo",
            EchoPayload.SCHEMA,
            EchoPayload.SCHEMA,
            "Echoes a message back to the client",
            (server, params, client) -> {
                System.out.println("Called by connection: " + client.connectionId());
                return params;
            }
        );

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            NS.attach(server); // bind server for use in method handlers
            msmp = new MsmpServer(server);
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            NS.detach();
            msmp = null;
        });
    }

    // Broadcast a notification to all connected clients
    public static void sendPing() {
        if (msmp != null) msmp.send(PING, new PingPayload("hello"));
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

### Sending notifications

**Broadcast to all connected clients:**

```java
msmp.send(PING, new PingPayload("hello"));
```

**Send to a specific client** using the `connectionId` from a method handler:

```java
NS.method("echo",
    EchoPayload.SCHEMA,
    EchoPayload.SCHEMA,
    "Echoes a message back to the calling client only",
    (server, params, client) -> {
        msmp.sendTo(client.connectionId(), PING, new PingPayload("only for you"));
        return params;
    }
);
```

### JSON-RPC examples

**Calling a method** (`my_mod:method/echo`):

```json
{
    "jsonrpc": "2.0",
    "id": 1,
    "method": "my_mod:method/echo",
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
