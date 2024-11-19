import 'package:flutter/material.dart';
import 'package:lab/styles.dart';
import 'package:lab/types/problem.dart';
import 'package:lab/widets/basic_card.dart';

class ProblemList extends StatefulWidget {
  const ProblemList({super.key});

  @override
  State<ProblemList> createState() => _ProblemListState();
}

class _ProblemListState extends State<ProblemList> {
  List<Problem> dailyProblems = [];
  List<Problem> challengeProblems = [];

  @override
  void initState() {
    super.initState();
    // dummy problems
    dailyProblems = [
      Problem.randomDummy(),
      Problem.randomDummy(),
      Problem.randomDummy(),
      Problem.randomDummy(),
      Problem.randomDummy(),
      Problem.randomDummy(),
    ];
    challengeProblems = [
      Problem.randomDummy(),
      Problem.randomDummy(),
      Problem.randomDummy(),
      Problem.randomDummy(),
      Problem.randomDummy(),
      Problem.randomDummy(),
    ];
  }

  ListView makeProblemList(List<Problem> problems) {
    return ListView.separated(
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      itemCount: problems.length,
      itemBuilder: (context, index) {
        final problem = problems[index];
        return Dismissible(
          key: Key(problem.id.toString()),
          direction: DismissDirection.endToStart,
          onDismissed: (direction) {
            setState(() {
              problems.removeAt(index);
              problems.add(Problem.randomDummy());
            });
          },
          background: Container(
            color: Colors.lightGreen,
            alignment: Alignment.centerRight,
            padding: const EdgeInsets.symmetric(
                horizontal: 20.0),
            child: const Icon(Icons.recycling,
                color: Colors.white),
          ),
          child: problem.toListTile(context, () {}),
        );
      },
      separatorBuilder: (context, index) => Divider(
        color: colorScheme.secondary,
        thickness: 1,
      ),
    );
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
                            Column(
                              children: [
                                Text(
                                  "오늘의 문제",
                                  style: nanum25pEB,
                                )
                              ],
                            ),
                          ],
                        ),
                        Divider(
                          color: colorScheme.secondary,
                          thickness: 1,
                        ),
                        Column(
                          children: [
                            makeProblemList(dailyProblems),
                          ],
                        ),
                      ],
                    )),
                    BasicCard(
                        child: Column(
                      children: [
                        Row(
                          children: [
                            Column(
                              children: [
                                Text(
                                  "고난도 문제",
                                  style: nanum25pEB,
                                )
                              ],
                            ),
                          ],
                        ),
                        Divider(
                          color: colorScheme.secondary,
                          thickness: 1,
                        ),
                        Column(
                          children: [
                            makeProblemList(challengeProblems),
                          ],
                        ),
                      ],
                    )),
                  ],
                ),
              ),
            ),
          ),
        ));
  }
}

