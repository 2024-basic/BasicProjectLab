import 'dart:math';

class Problem {
  final int id;
  final String title;
  final String description;
  final int level;
  final int solved;
  final double averageTries;

  double rateAverage = 0;
  int rate = 0;

  Problem(this.id, this.title, this.description, this.level, this.solved, this.averageTries);

  static Problem randomDummy() {
    var rand = Random();
    var randId = rand.nextInt(35000 - 1000) + 1000;
    return Problem(
      randId,
      '더미 문제 ${rand.nextInt(1000)}',
      '$randId번 문제\n대충 설명\n사용 알고리즘: asdasd',
      rand.nextInt(1),
      rand.nextInt(100),
      rand.nextDouble(),
    );
  }

  static fromJson(Map<String, dynamic> mp) {
    String tagsString = '#${(mp['tags'] as List).map((tag) => tag['displayName']).join(', #')}';
    Problem ret = Problem(
      mp['problemId'],
      mp['title'],
      tagsString,
      mp['level'],
      mp['solvedCount'],
      mp['averageTries'],
    );
    ret.rateAverage = Random().nextDouble() * 5;
    return ret;
  }

  @override
  String toString() {
    return 'Problem{id: $id, title: $title, description: $description, level: $level, solved: $solved, averageTries: $averageTries}';
  }
}