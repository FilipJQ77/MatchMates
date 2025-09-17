import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() => runApp(const MyApp());

class MyApp extends StatefulWidget {
  const MyApp({super.key});
  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  static const platform = MethodChannel('wear_channel');
  String message = "No messages yet";

  @override
  void initState() {
    super.initState();
    platform.setMethodCallHandler((call) async {
      if (call.method == "onMessage") {
        setState(() {
          message = "From Phone: ${call.arguments}";
        });
      }
    });
  }

  Future<void> sendMessage() async {
    await platform.invokeMethod('sendMessage', "hello from watch");
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(message),
              const SizedBox(height: 20),
              ElevatedButton(
                onPressed: sendMessage,
                child: const Text("Send to Phone"),
              )
            ],
          ),
        ),
      ),
    );
  }
}
