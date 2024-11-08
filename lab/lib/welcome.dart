import 'package:flutter/material.dart';
import 'package:lab/styles.dart';

void main() {
  runApp(const WelcomeHome());
}

class WelcomeHome extends StatefulWidget {
  const WelcomeHome({super.key});

  @override
  State<WelcomeHome> createState() => _WelcomeHomeState();
}

class _WelcomeHomeState extends State<WelcomeHome> {
  static const strBlank = '----------';
  String? strWelcome = strBlank;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      theme: ThemeData(colorScheme: colorScheme, useMaterial3: true),
      home: Scaffold(
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                strWelcome!,
                style: const TextStyle(fontSize: 30),
              ),
              const SizedBox(
                height: 50,
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  ElevatedButton(
                      onPressed: () {
                        setState(() {
                          strWelcome = 'Welcome';
                        });
                      },
                      child: const Text('Welcome')),
                  const SizedBox(
                    width: 50,
                  ),
                  ElevatedButton(
                      onPressed: () {
                        setState(() {
                          strWelcome = strBlank;
                        });
                      },
                      child: const Text('     clear     '))
                ],
              )
            ],
          ),
        ),
      ),
    );
  }
}
