import 'package:flutter/material.dart';
import 'package:lab/styles.dart';
import 'package:lab/widets/basic_app_bar.dart';
import 'package:lab/widets/basic_card.dart';

import '../types/problem.dart';

class ProblemDetail extends StatelessWidget {
  final Problem problem;

  ProblemDetail({super.key, Problem? problem}) : problem = problem ?? Problem.randomDummy();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const BasicAppBar(),
      body: Center(
        child: BasicCard(
          child: Column(
            children: [
              Row(
                children: [
                  Column(
                    children: [
                      Text('${problem.id}: ${problem.title}', style: nanum25pEB),
                    ],
                  ),
                  Column(
                    children: [
                      Text('푼 사람: ${problem.solved}', style: nanum15sR),
                    ],
                  )
                ],
              )
            ],
          ),
        )
      ),
    );
  }
}
