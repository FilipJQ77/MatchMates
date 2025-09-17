package com.matchmates.matchmates

import android.os.Bundle
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.MethodChannel
import com.google.android.gms.wearable.*
import com.google.android.gms.tasks.OnSuccessListener

class MainActivity : FlutterActivity(), MessageClient.OnMessageReceivedListener {
    private val CHANNEL = "wear_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Wearable.getMessageClient(this).addListener(this)

        MethodChannel(
            flutterEngine!!.dartExecutor.binaryMessenger,
            CHANNEL
        ).setMethodCallHandler { call, result ->
            if (call.method == "sendMessage") {
                val msg = call.arguments as String
                sendToWatch(msg)
                result.success(null)
            } else {
                result.notImplemented()
            }
        }
    }

    private fun sendToWatch(message: String) {
        Wearable.getNodeClient(this).connectedNodes
            .addOnSuccessListener { nodes ->
                for (node in nodes) {
                    Wearable.getMessageClient(this)
                        .sendMessage(node.id, "/message_path", message.toByteArray())
                }
            }
    }

    override fun onMessageReceived(event: MessageEvent) {
        val msg = String(event.data)
        runOnUiThread {
            MethodChannel(flutterEngine!!.dartExecutor.binaryMessenger, CHANNEL)
                .invokeMethod("onMessage", msg)
        }
    }
}
