import 'package:flutter/material.dart';
import 'package:lab/types/problem.dart';
import 'package:lab/widets/basic_card.dart';
import 'styles.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  List<Problem> problems = [];
  Problem? unratedProblem;
  int hoverIndex = -1;

  @override
  void initState() {
    super.initState();
    // dummy problems
    problems = [
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
                  child: Center(
                    child: Text(
                      "OOO점 상위 OO%!",
                      style: nanum30pEB,
                    ),
                  ),
                ),
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
                    Divider(
                      color: colorScheme.secondary,
                      thickness: 1,
                    ),
                    Column(
                      children: [
                        ListView.separated(
                          shrinkWrap: true,
                          physics: const NeverScrollableScrollPhysics(),
                          itemCount: problems.length,
                          itemBuilder: (context, index) {
                            final problem = problems[index];
                            return problem.toListTile(context, () {});
                          },
                          separatorBuilder: (context, index) => Divider(
                              color: colorScheme.secondary, thickness: 1),
                        ),
                      ],
                    ),
                  ],
                )),
                if (unratedProblem != null)
                  BasicCard(
                      child: Column(
                    children: [
                      Row(
                        children: [
                          Column(
                            children: [
                              Text(
                                "전에 푼 문제를 평가해주세요!",
                                style: nanum25pEB,
                              )
                            ],
                          ),
                        ],
                      ),
                      Column(
                        children: [
                          Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: List.generate(5, (index) {
                              return MouseRegion(
                                onEnter: (_) {
                                  setState(() {
                                    hoverIndex = index;
                                  });
                                },
                                onExit: (_) {
                                  setState(() {
                                    hoverIndex = -1;
                                  });
                                },
                                child: IconButton(
                                  icon: Icon(
                                    Icons.circle,
                                    color: index < (unratedProblem?.level ?? 0)
                                        ? primaryColor
                                        : (index <= hoverIndex
                                            ? primaryColor.withOpacity(0.5)
                                            : Colors.grey),
                                  ),
                                  onPressed: () {
                                    setState(() {
                                      unratedProblem = Problem(
                                          unratedProblem!.id,
                                          unratedProblem!.title,
                                          unratedProblem!.description,
                                          index + 1,
                                          unratedProblem!.solved);
                                    });
                                  },
                                ),
                              );
                            }),
                          ),
                          ListTile(
                            title:
                                Text(unratedProblem!.title, style: nanum20sEB),
                            subtitle: Text(unratedProblem!.description),
                            onTap: () {},
                          )
                        ],
                      ),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.end,
                        children: [
                          Icon(Icons.circle, color: primaryColor),
                          Text("3.2", style: nanum15sR),
                        ],
                      )
                    ],
                  ))
              ],
            ),
          ),
        ),
      )),
    );
  }
}
