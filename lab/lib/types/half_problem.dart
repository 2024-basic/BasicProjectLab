import 'dart:math';

import 'package:flutter/material.dart';

import '../styles.dart';

class HalfProblem {
  final int id;
  final String title;
  final String description;
  final int level;
  final int solved;

  HalfProblem(this.id, this.title, this.description, this.level, this.solved);

  static HalfProblem randomDummy() {
    var rand = Random();
    var randId = rand.nextInt(35000 - 1000) + 1000;
    return HalfProblem(
        randId,
        '더미 문제 ${rand.nextInt(1000)}',
        '$randId번 문제\n대충 설명\n사용 알고리즘: asdasd',
        rand.nextInt(1),
        rand.nextInt(100)
    );
  }

  ListTile toListTile(BuildContext context, dynamic onTapCallback) {
    return ListTile(
      title: Text(title, style: nanum15sEB),
    );
  }
}