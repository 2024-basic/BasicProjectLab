import 'package:flutter/material.dart';

import '../styles.dart';
import '../types/problem.dart';

ListTile toListTile(Problem p, BuildContext context, dynamic onTapCallback) {
  return ListTile(
    title: Text(p.title, style: nanum20sEB),
    trailing: Text("푼 사람: ${p.solved}", style: nanum15sR),
    // onTap: onTapCallback,
  );
}

ListTile toListTileHalf(Problem p, BuildContext context, dynamic onTapCallback) {
  return ListTile(
    title: Text(p.title, style: nanum15sEB),
  );
}