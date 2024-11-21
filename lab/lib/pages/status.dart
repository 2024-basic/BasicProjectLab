import 'package:flutter/material.dart';
import 'package:lab/widets/PidChart.dart';
import 'package:lab/widets/basic_card.dart';
import 'package:lab/widets/basic_card_half.dart';

import '../styles.dart';
import '../types/problem.dart';
import '../widets/problem_tile.dart';

class Status extends StatefulWidget {
  const Status({super.key});

  @override
  State createState() => _StatusState();
}

class _StatusState extends State<Status> with TickerProviderStateMixin {
  List<double> listOfStudyValue1 = [5, 7.6, 6.7, 8];
  List<double> listOfStudyValue2 = [4.3, 6.6, 5.8, 6.8];
  List<String> listOfStudyValueName1 = [
    'dp',
    'implementation',
    'graphs',
    'data_structures'
  ];
  List<String> listOfStudyValueName2 = [
    'string',
    'sorting',
    'greedy',
    'shortest_path'
  ];

  int goal = 10;
  List<Problem> halfproblems = [];
  List<Problem> halfproblems2 = [];
  Problem? unratedProblem;
  late List<AnimationController> _controllers;
  late List<Animation<double>> _animations;

  @override
  void initState() {
    super.initState();
    // dummy problems
    _controllers = List.generate(
      listOfStudyValue1.length + listOfStudyValue2.length,
      (index) => AnimationController(
        vsync: this,
        duration: const Duration(seconds: 2),
      ),
    );

    _animations = List.generate(
      listOfStudyValue1.length + listOfStudyValue2.length,
      (index) => Tween<double>(
        begin: 0.0,
        end: index < listOfStudyValue1.length
            ? listOfStudyValue1[index]
            : listOfStudyValue2[index - listOfStudyValue1.length],
      ).animate(
        CurvedAnimation(
          parent: _controllers[index],
          curve: Curves.easeOut,
        ),
      ),
    );

    // Trigger animations to start when screen is loaded
    for (var controller in _controllers) {
      controller.forward();
    }

    halfproblems = [
      Problem.randomDummy(),
      Problem.randomDummy(),
      Problem.randomDummy(),
      Problem.randomDummy(),
      Problem.randomDummy(),
    ];
    halfproblems2 = [
      Problem.randomDummy(),
      Problem.randomDummy(),
      Problem.randomDummy(),
    ];
    unratedProblem = Problem.randomDummy();
  }

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
                            Text("학습 현황", style: nanum25pEB),
                          ],
                        ),
                        Padding(
                          padding: EdgeInsets.only(top: 8, bottom: 8),
                          child: Divider(
                            color: colorScheme.secondary,
                            thickness: 1,
                          ),
                        ),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children:
                              List.generate(listOfStudyValue1.length, (index) {
                            return Column(
                              children: [
                                AnimatedBuilder(
                                  animation: _animations[index],
                                  builder: (context, child) {
                                    return CustomPaint(
                                      size: Size(
                                          MediaQuery.of(context).size.width *
                                              0.15,
                                          MediaQuery.of(context).size.width *
                                              0.15),
                                      painter: PieChart(
                                        goal: goal,
                                        percentage: _animations[index].value,
                                        textScaleFactor: 1.0,
                                        chart: listOfStudyValueName1[index],
                                      ),
                                    );
                                  },
                                ),
                                Padding(
                                  padding: const EdgeInsets.only(top: 8.0),
                                  child: Text(listOfStudyValueName1[index],
                                      style: nanum10sB),
                                ),
                              ],
                            );
                          }),
                        ),
                        Padding(
                          padding: EdgeInsets.only(top: 8, bottom: 8),
                          child: Divider(
                            color: colorScheme.secondary,
                            thickness: 1,
                          ),
                        ),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children:
                              List.generate(listOfStudyValue2.length, (index) {
                            int idx = index + listOfStudyValue1.length;
                            return Column(
                              children: [
                                AnimatedBuilder(
                                  animation: _animations[idx],
                                  builder: (context, child) {
                                    return CustomPaint(
                                      size: Size(
                                          MediaQuery.of(context).size.width *
                                              0.15,
                                          MediaQuery.of(context).size.width *
                                              0.15),
                                      painter: PieChart(
                                        goal: goal,
                                        percentage: _animations[idx].value,
                                        textScaleFactor: 1.0,
                                        chart: listOfStudyValueName2[index],
                                      ),
                                    );
                                  },
                                ),
                                Padding(
                                  padding: const EdgeInsets.only(top: 8.0),
                                  child: Text(listOfStudyValueName2[index],
                                      style: nanum10sB),
                                ),
                              ],
                            );
                          }),
                        )
                      ],
                    ),
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      BasicCardHalf(
                          child: Column(
                        children: [
                          Row(
                            children: [
                              Text("맞은 문제", style: nanum15pEB),
                              Expanded(
                                child: Align(
                                  alignment: Alignment.centerRight,
                                  child: Column(
                                    children: [
                                      IconButton(
                                        onPressed: () {},
                                        icon: const Icon(
                                          Icons.arrow_forward_ios,
                                          color: secondaryColor,
                                        ),
                                      )
                                    ],
                                  ),
                                ),
                              ),
                            ],
                          ),
                          Padding(
                            padding: EdgeInsets.only(top: 8, bottom: 8),
                            child: Divider(
                              color: colorScheme.secondary,
                              thickness: 1,
                            ),
                          ),
                          ListView.separated(
                            shrinkWrap: true,
                            physics: const NeverScrollableScrollPhysics(),
                            itemCount: halfproblems.length,
                            itemBuilder: (context, index) {
                              final halfproblem = halfproblems[index];
                              return toListTileHalf(halfproblem, context);
                            },
                            separatorBuilder: (context, index) => Divider(
                                color: colorScheme.secondary, thickness: 1),
                          ),
                        ],
                      )),
                      BasicCardHalf(
                          child: Column(
                        children: [
                          Row(
                            children: [
                              Text("시도했지만 맞지 못한 문제",
                                  style:
                                      nanum(size: 12, weight: FontWeight.w900)),
                              Expanded(
                                child: Align(
                                  alignment: Alignment.centerRight,
                                  child: Column(
                                    children: [
                                      IconButton(
                                        onPressed: () {},
                                        icon: const Icon(
                                          Icons.arrow_forward_ios,
                                          color: secondaryColor,
                                        ),
                                      )
                                    ],
                                  ),
                                ),
                              ),
                            ],
                          ),
                          Padding(
                            padding: EdgeInsets.only(top: 8, bottom: 8),
                            child: Divider(
                              color: colorScheme.secondary,
                              thickness: 1,
                            ),
                          ),
                          ListView.separated(
                            shrinkWrap: true,
                            physics: const NeverScrollableScrollPhysics(),
                            itemCount: halfproblems2.length,
                            itemBuilder: (context, index) {
                              final halfproblem2 = halfproblems2[index];
                              return toListTileHalf(halfproblem2, context);
                            },
                            separatorBuilder: (context, index) => Divider(
                                color: colorScheme.secondary, thickness: 1),
                          ),
                        ],
                      )),
                    ],
                  )
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
