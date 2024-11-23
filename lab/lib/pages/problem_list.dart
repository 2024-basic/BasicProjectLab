import 'dart:collection';

import 'package:flutter/material.dart';
import 'package:lab/api_handler.dart';
import 'package:lab/styles.dart';
import 'package:lab/types/problem.dart';
import 'package:lab/widets/basic_card.dart';

import '../widets/problem_tile.dart';

class ProblemList extends StatefulWidget {
  const ProblemList({super.key});

  @override
  State<ProblemList> createState() => _ProblemListState();
}

class _ProblemListState extends State<ProblemList> {
  int _currentPage = 0;

  List<Problem> dailyProblems = [];
  List<Problem> challengeProblems = [];

  Queue<Problem> waitingProblems = Queue();

  @override
  void initState() {
    super.initState();
    // dummy problems
    ApiHandler().requestRecommendedProblems(_currentPage, 5, 30).then((lst) {
      dailyProblems = lst;
      setState(() {

      });
    });
    ApiHandler().requestRecommendedProblems(_currentPage, 20, 30).then((lst) {
      challengeProblems = lst;
      setState(() {

      });
    });

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
          onDismissed: (direction) async {
            problems.removeAt(index);

            if (waitingProblems.isNotEmpty) {
              problems.insert(index, waitingProblems.removeFirst());
            } else {
              _currentPage += 5;
              waitingProblems.addAll(await ApiHandler().requestRecommendedProblems(_currentPage, 5, 30));
              problems.insert(index, waitingProblems.removeFirst());
            }
            setState(() {});
          },
          background: Container(
            color: Colors.lightGreen,
            alignment: Alignment.centerRight,
            padding: const EdgeInsets.symmetric(horizontal: 20.0),
            child: const Icon(Icons.recycling, color: Colors.white),
          ),
          child: toListTile(problem, context),
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
                            dailyProblems.isEmpty
                                ? const Center(
                                    child: CircularProgressIndicator(),
                                  )
                                : makeProblemList(dailyProblems),
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
                            challengeProblems.isEmpty
                                ? const Center(
                                    child: CircularProgressIndicator(),
                                  )
                                : makeProblemList(challengeProblems),
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
