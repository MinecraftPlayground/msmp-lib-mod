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
    implementation("com.github.MinecraftPlayground/msmp-lib-mod:VERSION")
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

    public static final Codec CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.STRING.fieldOf("message").forGetter(EchoPayload::message)
    ).apply(i, EchoPayload::new));

    public static final Schema SCHEMA = Schema.record(CODEC)
        .withField("message", Schema.STRING_SCHEMA);
}
```

### 2. Define your methods and notifications

Group all your methods and notifications in a dedicated class.
Use `MSMPNamespace` to register them under a custom namespace.
Call `register()` to trigger static initialization.

```java
public class MyModMSMP {
    private static final MSMPNamespace NS = new MSMPNamespace("my_mod");

    // Incoming method — client sends a request, server responds
    public static final MSMPMethod ECHO =
        NS.method("echo",
            EchoPayload.SCHEMA,
            EchoPayload.SCHEMA,
            "Echoes a message back to the client",
            (api, params, client) -> params
        );

    // Outgoing notification — server broadcasts to all connected clients
    public static final MSMPNotification PING =
        NS.notification("ping", PingPayload.SCHEMA, "A ping notification");

    public static void register() {}
}
```

### 3. Initialize in your mod

Call `register()` in `onInitialize()` to load the class and register all methods
and notifications. Use `MSMPServer` to broadcast notifications to connected clients.

```java
public class MyMod implements ModInitializer {

    private static MSMPServer msmp;

    @Override
    public void onInitialize() {
        MyModMSMP.register();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            msmp = new MSMPServer(server);
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            msmp = null;
        });
    }

    public static void sendPing() {
        if (msmp != null) msmp.send(MyModMSMP.PING, new PingPayload("hello"));
    }
}
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
