package nl.owenray.udp

import android.net.wifi.WifiManager
import android.util.Log
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import android.content.Context


class UdpTurboModule(reactContext: ReactApplicationContext?) : NativeUdpTurboSpec(reactContext) {
    private val mClients = ArrayList<UdpSocketClient>()
    private val wifi = reactApplicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val mMulticastLock = wifi.createMulticastLock("udp-lock").apply { setReferenceCounted(true) }


    override fun createSocket(options: ReadableMap): Double {
        val client = UdpSocketClient();
        mClients.add(client)
        Log.v("UdpTurboModule", "create socket, client size: " + mClients.size);
        return mClients.lastIndexOf(client).toDouble()
    }

    override fun bind(id: Double, port: Double, address: String, options: ReadableMap, promise: Promise) {
        try {
            val client: UdpSocketClient = mClients.get(id.toInt())
            client.bind(port.toInt(), address)
            promise.resolve(null)
        } catch (e: Exception) {
            promise.reject(e)
        }
    }

    override fun close(id: Double, promise: Promise) {
        try {
            val client: UdpSocketClient = mClients.get(id.toInt())
            if (mMulticastLock != null && mMulticastLock.isHeld() && client.isMulticast()) {
                // drop the multi-cast lock if this is a multi-cast client
                mMulticastLock.release()
            }
            client.close()
            mClients.removeAt(id.toInt())
            promise.resolve(null)
        } catch (e: Exception) {
            promise.reject(e)
        }
    }

    override fun sendBase64(
        id: Double,
        base64String: String?,
        port: Double,
        address: String?,
        promise: Promise?
    ) {
        try {
            val client: UdpSocketClient = mClients.get(id.toInt())
            client.send(base64String, port.toInt(), address)
            Log.v("UdpTurboModule", "send done")
            promise?.resolve(null)
        } catch (e: Exception) {
            Log.v("UdpTurboModule", "send error")
            promise?.reject(e)
        }
    }

    override fun receive(id: Double, promise: Promise?) {
        Thread {
            try {
                val client: UdpSocketClient = mClients.get(id.toInt())
                val base64String = client.receive()
                promise?.resolve(base64String)
            } catch (e: Exception) {
                promise?.reject(e)
            }
        }.start()
    }

    override fun setBroadcast(id: Double, flag: Boolean, Promise: Promise?) {
        try {
            val client: UdpSocketClient = mClients.get(id.toInt())
            mMulticastLock.acquire()
            client.setBroadcast(flag)
            Promise?.resolve(null)
        } catch (e: Exception) {
            Promise?.reject(e)
        }
    }

    override fun addMembership(id: Double, multicastAddress: String?, Promise: Promise?) {
        try {
            val client: UdpSocketClient = mClients.get(id.toInt())
            client.addMembership(multicastAddress);
            Promise?.resolve(null)
        } catch (e: Exception) {
            Promise?.reject(e)
        }
    }

    override fun dropMembership(id: Double, multicastAddress: String?, Promise: Promise?) {
        try {
            val client: UdpSocketClient = mClients.get(id.toInt())
            client.dropMembership(multicastAddress);
            Promise?.resolve(null)
        } catch (e: Exception) {
            Promise?.reject(e)
        }
    }

    override fun reset(promise: Promise?) {
        try {
            for (client in mClients) {
                if (mMulticastLock != null && mMulticastLock.isHeld() && client.isMulticast()) {
                    mMulticastLock.release()
                }
                client.close()
            }
            mClients.clear()
            Log.v("UdpTurboModule", "Reset: All clients removed and sockets closed")
            promise?.resolve(null)
        } catch (e: Exception) {
            Log.e("UdpTurboModule", "Reset error: ${e.message}")
            promise?.reject(e)
        }
    }

    companion object {
        const val NAME = "UdpTurbo"
    }
}
