import 'package:flutter/material.dart';
import 'styles.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
          colorScheme: colorScheme,
          useMaterial3: true
      ),
      home: Scaffold(
        body: Container(
          color: colorScheme.background,

          child: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text(
                  'Welcome to evenUP',
                  style: nanum30pEB,
                ),
                const SizedBox(
                  height: 50,
                ),
                ElevatedButton(
                    onPressed: () {
                      Navigator.pushNamed(context, '/welcome');
                    },
                    child: const Text('Welcome')),
                const SizedBox(
                  height: 50,
                ),
                ElevatedButton(
                    onPressed: () {
                      Navigator.pushNamed(context, '/counter');
                    },
                    child: const Text('Counter')),
                const SizedBox(
                  height: 50,
                ),
                ElevatedButton(
                    onPressed: () {
                      Navigator.pushNamed(context, '/image');
                    },
                    child: const Text('Image')),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
