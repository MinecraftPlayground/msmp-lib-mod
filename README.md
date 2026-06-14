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
    implementation("com.github.MinecraftPlayground:msmp-lib-mod:VERSION")
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
public record EchoPayload(String message) {

    public static final Codec<EchoPayload> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.STRING.fieldOf("message").forGetter(EchoPayload::message)
    ).apply(i, EchoPayload::new));

    public static final Schema<EchoPayload> SCHEMA = Schema.record(CODEC)
        .withField("message", Schema.STRING_SCHEMA);
}
```

### 2. Register methods and notifications

Create a `MSMPNamespace` and register methods and notifications in `onInitialize()` -
before the server starts and the registry is frozen.

Use `attach()` and `detach()` in the server lifecycle events to bind and release
the server instance for use in method handlers.

```java
public class MyMod implements ModInitializer {

    private static final MSMPNamespace NS = new MSMPNamespace("my_mod");

    // Registered as a field - before the server starts
    private static final MSMPNotification<EchoPayload> PING =
        NS.notification("ping")
            .responseSchema(EchoPayload.SCHEMA)
            .description("A ping notification")
            .register();

    private static MSMPServer msmp;

    @Override
    public void onInitialize() {
        // Register a method that receives a parameter
        NS.method("echo")
            .requestSchema(EchoPayload.SCHEMA)
            .responseSchema(EchoPayload.SCHEMA)
            .description("Echoes a message back to the client")
            .register((server, client, params) -> {
                System.out.println("Called by connection: " + client.connectionId());
                return params;
            });

        // Register a method without parameters
        NS.method("get_time")
            .responseSchema(TimePayload.SCHEMA)
            .description("Returns the current game time")
            .register((server, client) -> new TimePayload(server.getGameTime()));

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
        if (msmp != null) msmp.send(PING, new EchoPayload("hello"));
    }
}
```

### Method types

**Methods with request and response payload:**

```java
NS.method("echo")
    .requestSchema(EchoPayload.SCHEMA)
    .responseSchema(EchoPayload.SCHEMA)
    .description("Echoes a message back to the client")
    .register((server, client, params) -> {
        System.out.println("Received: " + params.message());
        return params;
    });
```

**Parameterless methods (no request payload):**

```java
NS.method("get_time")
    .responseSchema(TimePayload.SCHEMA)
    .description("Returns the current game time")
    .register((server, client) -> new TimePayload(server.getGameTime()));
```

### Handler parameters

The method handler signature depends on whether the method expects parameters:

**With parameters:** `(server, client, params) -> result`

| Parameter | Type              | Description                           |
|-----------|-------------------|:--------------------------------------|
| `server`  | `MinecraftServer` | The running Minecraft server instance |
| `client`  | `ClientInfo`      | Info about the calling client         |
| `params`  | `Param`           | The payload received from the client  |

**Without parameters:** `(server, client) -> result`

| Parameter | Type              | Description                           |
|-----------|-------------------|:--------------------------------------|
| `server`  | `MinecraftServer` | The running Minecraft server instance |
| `client`  | `ClientInfo`      | Info about the calling client         |

### Sending notifications

**Broadcast to all connected clients:**

```java
msmp.send(PING, new EchoPayload("hello"));
```

**Send to a specific client** using the `connectionId` from a method handler:

```java
NS.method("notify_client")
    .requestSchema(RequestPayload.SCHEMA)
    .responseSchema(ResponsePayload.SCHEMA)
    .description("Sends a notification only to the calling client")
    .register((server, client, params) -> {
        msmp.sendTo(client.connectionId(), PING, new EchoPayload("only for you"));
        return new ResponsePayload("sent");
    });
```

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
