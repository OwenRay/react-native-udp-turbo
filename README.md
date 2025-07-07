# React Native UDP Turbo

A React Native library for UDP socket communication using the new Turbo Module system. This library provides a simple and efficient way to work with UDP sockets in your React Native applications.

## Features

- Create and manage UDP sockets
- Send and receive data over UDP
- Support for broadcast and multicast operations
- Implemented using React Native's Turbo Module system for better performance
- Written in TypeScript for type safety

## Platform Support

- ✅ Android
- ❌ iOS (Not yet implemented)

## Installation

```sh
# Using npm
npm install react-native-udp-turbo

# Using yarn
yarn add react-native-udp-turbo
```

### Android Setup

The library should work out of the box with React Native's auto-linking.

## Usage

Here's a simple example of how to use the library:

```typescript
import ReactNativeUdpTurbo from "react-native-udp-turbo";
import { Buffer } from "buffer";

// Create a UDP socket
const socketId = ReactNativeUdpTurbo.createSocket({ type: "udp4" });

// Bind the socket to a port and address
await ReactNativeUdpTurbo.bind(socketId, 1234, "0.0.0.0", {});

// Send a message
const message = Buffer.from("Hello, UDP!", "ascii").toString("base64");
await ReactNativeUdpTurbo.sendBase64(socketId, message, 1235, "192.168.1.255");

// Receive a message
const receivedBase64 = await ReactNativeUdpTurbo.receive(socketId);
const receivedMessage = Buffer.from(receivedBase64, "base64").toString("ascii");
console.log("Received:", receivedMessage);

// Close the socket when done
await ReactNativeUdpTurbo.close(socketId);
```

### Ping-Pong Example

Here's a more complete example showing a ping-pong communication between two sockets:

```typescript
import { useCallback, useEffect, useMemo, useState } from "react";
import ReactNativeUdpTurbo from "react-native-udp-turbo";
import { Buffer } from "buffer";

const PING_MESSAGE = Buffer.from('ping', 'ascii').toString('base64');
const PONG_MESSAGE = Buffer.from('pong', 'ascii').toString('base64');
const PING_PORT = 1235;
const PONG_PORT = 1234;
const BIND_ADDRESS = '0.0.0.0';

function UdpExample() {
    const idPing = useMemo(() => ReactNativeUdpTurbo.createSocket({type: "udp4"}), []);
    const idPong = useMemo(() => ReactNativeUdpTurbo.createSocket({type: "udp4"}), []);
    const [messages, setMessages] = useState<string[]>([]);

    const receivePing = useCallback(() => {
        ReactNativeUdpTurbo.receive(idPing).then(message => {
            console.log('received message', message);
            setMessages(messages => [...messages, Buffer.from(message, 'base64').toString('ascii')]);
        }).finally(receivePing)
    }, [setMessages]);

    const receivePong = useCallback(() => {
        ReactNativeUdpTurbo.receive(idPong).then(message => {
            console.log('received message', message);
            setMessages(messages => [...messages, Buffer.from(message, 'base64').toString('ascii')]);
        }).finally(receivePong)
    }, [setMessages])

    useEffect(() => {
        console.log('bind addresses');
        ReactNativeUdpTurbo.bind(idPing, PING_PORT, BIND_ADDRESS, {})
            .then(() => console.log('bound ping'))
            .catch(console.error);
        ReactNativeUdpTurbo.bind(idPong, PONG_PORT, BIND_ADDRESS, {})
            .then(() => console.log('bound pong'))
            .catch(console.error);
        receivePing();
        receivePong();
    }, [idPing, idPong])

    const sendPing = () => {
        ReactNativeUdpTurbo.sendBase64(idPing, PING_MESSAGE, PONG_PORT, '127.0.0.1')
            .then(() => console.log('sent ping'))
            .catch(console.error);
    };

    const sendPong = () => {
        ReactNativeUdpTurbo.sendBase64(idPong, PONG_MESSAGE, PING_PORT, '127.0.0.1')
            .then(() => console.log('sent pong'))
            .catch(console.error);
    };

    return { messages, sendPing, sendPong };
}
```

## API Reference

### Socket Creation and Management

#### `createSocket(options: { type: 'udp4' | 'udp6' }): number`

Creates a new UDP socket.

- **options.type**: Specifies the address family. Use 'udp4' for IPv4 or 'udp6' for IPv6.
- **Returns**: A socket ID that is used to reference this socket in other methods.

#### `bind(id: number, port: number, address: string, options: Object): Promise<void>`

Binds the socket to a port and address.

- **id**: The socket ID returned from `createSocket`.
- **port**: The port to bind to.
- **address**: The address to bind to (e.g., '0.0.0.0' for all interfaces).
- **options**: Additional options (currently not used).

#### `close(id: number): Promise<void>`

Closes the socket.

- **id**: The socket ID returned from `createSocket`.

### Data Transmission

#### `sendBase64(id: number, base64String: string, port: number, address: string): Promise<void>`

Sends a base64-encoded message to the specified address and port.

- **id**: The socket ID returned from `createSocket`.
- **base64String**: The message to send, encoded as a base64 string.
- **port**: The destination port.
- **address**: The destination address.

### Data Reception

#### `receive(id: number): Promise<string>`

Receives a message from the socket.

- **id**: The socket ID returned from `createSocket`.
- **Returns**: A Promise that resolves to the received message as a base64-encoded string.

### Socket Configuration

#### `setBroadcast(id: number, flag: boolean): Promise<void>`

Enables or disables broadcast mode for the socket.

- **id**: The socket ID returned from `createSocket`.
- **flag**: `true` to enable broadcast mode, `false` to disable it.

### Multicast Operations

#### `addMembership(id: number, multicastAddress: string): Promise<void>`

Adds the socket to a multicast group.

- **id**: The socket ID returned from `createSocket`.
- **multicastAddress**: The multicast group address to join.

#### `dropMembership(id: number, multicastAddress: string): Promise<void>`

Removes the socket from a multicast group.

- **id**: The socket ID returned from `createSocket`.
- **multicastAddress**: The multicast group address to leave.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
