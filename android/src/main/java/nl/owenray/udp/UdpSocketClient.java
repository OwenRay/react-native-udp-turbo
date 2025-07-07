package nl.owenray.udp;

import android.util.Base64;


import java.io.IOException;
import java.net.*;

import javax.annotation.Nullable;

/**
 * Client class that wraps a sender and a receiver for UDP data.
 */
public final class UdpSocketClient {

    private DatagramSocket mSocket;
    private boolean mIsMulticastSocket = false;

    public UdpSocketClient() {
    }

    /**
     * Checks to see if client part of a multi-cast group.
     * @return boolean true IF the socket is part of a multi-cast group.
     */
    public boolean isMulticast() {
        return mIsMulticastSocket;
    }

    /**
     * Binds to a specific port or address.  A random port is used if the address is {@code null}.
     *
     * @param port local port to bind to
     * @param address local address to bind to
     * @throws IOException
     * @throws IllegalArgumentException
     *             if the SocketAddress is not supported
     * @throws SocketException
     *             if the socket is already bound or a problem occurs during
     *             binding.
     */
    public void bind(Integer port, @Nullable String address) throws IOException {
        if (mSocket != null) {
            throw new IllegalStateException("Socket is already bound");
        }
        SocketAddress socketAddress;
        if (address != null) {
            socketAddress = new InetSocketAddress(InetAddress.getByName(address), port);
        } else {
            socketAddress = new InetSocketAddress(port);
        }

        mSocket = new MulticastSocket(socketAddress);
        mSocket.setReuseAddress(true);
    }

    /**
     * Adds this socket to the specified multicast group. Rebuilds the receiver task with a
     * MulticastSocket.
     *
     * @param address the multicast group to join
     * @throws UnknownHostException
     * @throws IOException
     * @throws IllegalStateException if socket is not bound.
     */
    public void addMembership(String address) throws UnknownHostException, IOException, IllegalStateException {
        if (null == mSocket || !mSocket.isBound()) {
            throw new IllegalStateException("Socket is not bound.");
        }

        ((MulticastSocket) mSocket).joinGroup(InetAddress.getByName(address));
        mIsMulticastSocket = true;
    }

    /**
     * Removes this socket from the specified multicast group.
     *
     * @param address the multicast group to leave
     * @throws UnknownHostException
     * @throws IOException
     */
    public void dropMembership(String address) throws UnknownHostException, IOException {
        ((MulticastSocket) mSocket).leaveGroup(InetAddress.getByName(address));
        mIsMulticastSocket = false;
    }

    /**
     * Creates a UdpSenderTask, and transmits udp data in the background.
     *
     * @param base64String byte array housed in a String.
     * @param port destination port
     * @param address destination address
     * @throws UnknownHostException
     * @throws IOException
     * @throws IllegalStateException if socket is not bound.
     */
    public void send(String base64String, Integer port, String address) throws UnknownHostException, IllegalStateException, IOException {
        if (null == mSocket || !mSocket.isBound()) {
            throw new IllegalStateException("Socket is not bound.");
        }

        InetSocketAddress inetAddress = new InetSocketAddress(InetAddress.getByName(address), port);
        byte[] data = Base64.decode(base64String, Base64.NO_WRAP);
        mSocket.send(new DatagramPacket(data, data.length, inetAddress));
    }

    /**
     * Sets the socket to enable broadcasts.
     */
    public void setBroadcast(boolean flag) throws SocketException {
        if (mSocket != null) {
            mSocket.setBroadcast(flag);
        }
    }

    /**
     * Shuts down the receiver task, closing the socket.
     */
    public void close() {
        // close the socket
        if (mSocket != null && !mSocket.isClosed()) {
            mSocket.close();
        }

        mSocket = null;
    }

    public String receive() throws IOException {
        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        mSocket.receive(packet);
        return Base64.encodeToString(buf, Base64.NO_WRAP);
    }
}
