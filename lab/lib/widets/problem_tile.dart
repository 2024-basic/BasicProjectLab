import 'package:flutter/material.dart';

import '../pages/problem_detail.dart';
import '../styles.dart';
import '../types/problem.dart';

ListTile toListTile(Problem p, BuildContext context) {
  return ListTile(
    title: Text('${p.id}: ${p.title}', style: nanum20sEB),
    trailing: Text("푼 사람: ${p.solved}", style: nanum15sR),
    onTap: () {
      Navigator.push(context, MaterialPageRoute(builder: (context) => ProblemDetail(problem: p)));
    },
  );
}

ListTile toListTileHalf(Problem p, BuildContext context) {
  return ListTile(
    title: Text('${p.id}: ${p.title}', style: nanum15sEB),
  );
}