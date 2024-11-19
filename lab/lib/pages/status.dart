import 'package:flutter/material.dart';
import 'package:lab/widets/basic_card.dart';

import '../styles.dart';

class Status extends StatefulWidget {
  const Status({super.key});

  @override
  State<Status> createState() => _StatusState();
}

class _StatusState extends State<Status> {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: ThemeData(colorScheme: colorScheme, useMaterial3: true),
      home: Scaffold(
        body: SingleChildScrollView(
          child: Container(
            color: colorScheme.surface,
            child: Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  BasicCard(
                    child: Column(
                      children: [
                        Row(
                          children: [
                            Text("학습 현황", style: nanum25pEB)
                          ],
                        )
                      ],
                    )
                  )
                ],
              ),
            )
          ),
        )
      ),
    );
  }
}
