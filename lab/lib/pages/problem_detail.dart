import 'package:flutter/material.dart';
import 'package:lab/styles.dart';
import 'package:lab/widets/basic_app_bar.dart';

import '../types/problem.dart';

class ProblemDetail extends StatelessWidget {
  final Problem problem;

  const ProblemDetail({super.key, required this.problem});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const BasicAppBar(),
      body: Center(
        child: Text('문제 상세 화면', style: nanum30pEB,),
      ),
    );
  }
}
