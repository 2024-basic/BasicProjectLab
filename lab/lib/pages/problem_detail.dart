import 'package:flutter/material.dart';
import 'package:lab/api_handler.dart';
import 'package:lab/styles.dart';
import 'package:lab/widets/basic_app_bar.dart';
import 'package:lab/widets/basic_card.dart';
import 'package:lab/widets/spoiler.dart';
import 'package:url_launcher/url_launcher.dart';

import '../types/problem.dart';

class ProblemDetail extends StatefulWidget {
  final Problem problem;
  final bool userSolved;

  const ProblemDetail(
      {super.key, required this.problem, this.userSolved = false});

  @override
  State<ProblemDetail> createState() => _ProblemDetailState();
}

class _ProblemDetailState extends State<ProblemDetail> {
  static int count = 0;
  final TextEditingController _ratingController = TextEditingController();
  List<dynamic> _ratings = [];
  int hoverIndex = -1;

  _ProblemDetailState() {
    count++;
    switch (count) {
      case 1: _ratings = [
        {
          'user': 'brian951862',
          'rating': 5,
          'comment': '상당히 재밌는 문제였습니다',
          'spoiler': false,
          'date': '2024-11-20',
        },
        {
          'user': 'jangfish0925',
          'rating': 1,
          'comment': '문제 설명이 너무 불친절해요;; 출력 형식과 전혀 다른 방식으로 출력을 해야 합니다...',
          'spoiler': true,
          'date': '2024-11-21',
        },
        {
          'user': 'wkdeogks17',
          'rating': 3,
          'comment': '와 이게 되네 ㅋㅋ',
          'spoiler': false,
          'date': '2024-11-23',
        }
      ]; break;
      case 2: _ratings = [
        {
          'user': 'brian951862',
          'rating': 3,
          'comment': '음? 이게 뭐지',
          'spoiler': false,
          'date': '2024-11-20',
        },
      ]; break;
      case 3: _ratings = [
        {
          'user': 'brian951862',
          'rating': 4,
          'comment': '정말 교육적인 문제네요 추천합니다.',
          'spoiler': false,
          'date': '2024-11-15',
        }
      ];
      default: _ratings = [
        {
          'user': 'jangfish0925',
          'rating': 5,
          'comment': '풀이를 떠올리기까지의 과정이 정말로 만족스러웠어요',
          'spoiler': false,
          'date': '2024-11-19',
        },
      ];
    }
  }

  void _submitRating() {
    var date = DateTime.now();
    setState(() {
      _ratings.add({
        'user': ApiHandler().userId,
        'rating': widget.problem.rate,
        'comment': _ratingController.text,
        'spoiler': false,
        'date': '${date.year}-${date.month}-${date.day}',
      });
      _ratingController.clear();
    });
  }

  @override
  Widget build(BuildContext context) {
    final problem = widget.problem;

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
                      ClipRect(
                        child: Text(
                          '${problem.id}: ${problem.title}',
                          style: nanum25pEB,
                          softWrap: true,
                          overflow: TextOverflow.visible,
                        ),
                      ),
                    ],
                  ),
                  Expanded(
                    child: Align(
                      alignment: Alignment.centerRight,
                      child: Column(
                        children: [
                          Row(
                            mainAxisAlignment: MainAxisAlignment.end,
                            children: [
                              IconButton(
                                icon: const Icon(Icons.public,
                                    size: 30, color: Colors.blue),
                                onPressed: () async {
                                  final url = Uri.parse(
                                      'https://www.acmicpc.net/problem/${problem.id}');
                                  if (await canLaunchUrl(url)) {
                                    await launchUrl(url);
                                  } else {
                                    throw 'Could not launch $url';
                                  }
                                },
                              ),
                              SizedBox(width: 30),
                              Icon(Icons.circle, color: primaryColor),
                              Text(
                                  (_ratings.map((e) {
                                            return e['rating'];
                                          }).reduce((a, b) => a + b) /
                                          _ratings.length)
                                      .toString(),
                                  style: nanum15sR),
                              SizedBox(width: 10),
                              Text('푼 사람: ${problem.solved}', style: nanum15sR),
                            ],
                          ),
                        ],
                      ),
                    ),
                  ),
                ],
              ),
              Divider(thickness: 1, color: colorScheme.secondary),
              SizedBox(height: 20),
              Spoiler(child: problem.description, style: nanum20sB, initialShow: widget.userSolved,),
              SizedBox(height: 20),
              Divider(thickness: 1, color: colorScheme.secondary),
              SizedBox(height: 20),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  widget.userSolved
                      ? Column(
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
                                      color: index < (problem.rate ?? 0)
                                          ? primaryColor
                                          : (index <= hoverIndex
                                              ? primaryColor.withOpacity(0.5)
                                              : Colors.grey),
                                    ),
                                    onPressed: () {
                                      setState(() {
                                        problem.rate = index + 1;
                                      });
                                    },
                                  ),
                                );
                              }),
                            ),
                          ],
                        )
                      : const SizedBox(),
                ],
              ),
              widget.userSolved
                  ?               Row(
                children: [
                  Expanded(
                    child: TextFormField(
                      controller: _ratingController,
                      decoration: const InputDecoration(
                        labelText: '의견을 입력해주세요',
                        labelStyle: TextStyle(color: Colors.grey),
                        floatingLabelStyle: TextStyle(color: primaryColor),
                      ),
                    ),
                  ),
                  ElevatedButton(
                    onPressed: _submitRating,
                    style: ElevatedButton.styleFrom(
                      padding: const EdgeInsets.symmetric(
                          vertical: 20, horizontal: 40),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(5),
                      ),
                      foregroundColor: Colors.black,
                      backgroundColor: Colors.white,
                    ),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      children: [
                        Text('제출하기', style: nanum20sB),
                      ],
                    ),
                  ),
                ],
              )
              : Text('이 문제를 풀고 평가를 남겨주세요!', style: nanum20sB),

              SizedBox(height: 20),
              Divider(thickness: 1, color: colorScheme.secondary),
              SizedBox(height: 20),
              Text('문제 의견', style: nanum20sB),
              SizedBox(height: 10),
              Expanded(
                child: ListView.separated(
                  shrinkWrap: true,
                  itemCount: _ratings.length,
                  itemBuilder: (context, index) {
                    final rating = _ratings[index];
                    return ListTile(
                        title: Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Text(rating['user'], style: nanum15sEB),
                            Text(rating['date'], style: nanum15sR),
                          ],
                        ),
                        subtitle: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Row(
                              children: [
                                Icon(Icons.circle,
                                    color: primaryColor, size: 20),
                                Text(
                                  rating['rating'].toString(),
                                  style: nanum15sR,
                                ),
                                SizedBox(width: 10),
                                // Spoiler(child: rating['comment'], style: nanum15sR, initialShow: !rating['spoiler'],),
                                Flexible(
                                    child: Spoiler(
                                  child: rating['comment'],
                                  style: nanum15sR,
                                  initialShow: !rating['spoiler'],
                                )),
                              ],
                            ),
                          ],
                        ));
                  },
                  separatorBuilder: (context, index) => Divider(
                      color: colorScheme.secondary,
                      thickness: 0.5,
                      indent: 20,
                      endIndent: 20),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
