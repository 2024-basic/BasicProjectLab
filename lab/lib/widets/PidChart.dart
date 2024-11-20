import 'dart:math';
import 'dart:ui' as ui;
import 'package:flutter/material.dart';
import 'package:lab/styles.dart';

class PieChart extends CustomPainter {
  final double percentage; // 도달률
  final double textScaleFactor;
  final int goal; // 목표
  final String chart; // chart 타입

  PieChart({
    required this.percentage,
    required this.goal,
    this.textScaleFactor = 1.0,
    required this.chart,
  });

  @override
  void paint(Canvas canvas, Size size) {
    Paint paint = Paint()
      ..color = Colors.grey[300]!
      ..strokeWidth = 10.0
      ..style = PaintingStyle.stroke
      ..strokeCap = StrokeCap.round;

    double radius = min(size.width / 2 - paint.strokeWidth / 2, size.height / 2 - paint.strokeWidth / 2);
    Offset center = Offset(size.width / 2, size.height / 2);
    canvas.drawCircle(center, radius, paint);

    double arcAngle = 2 * pi * (percentage / goal);
    paint.color = primaryColor;
    canvas.drawArc(Rect.fromCircle(center: center, radius: radius), -pi / 2, arcAngle, false, paint);

    drawText(canvas, size, "${percentage.toStringAsFixed(1)} / $goal");
  }

  void drawText(Canvas canvas, Size size, String text) {
    double fontSize = getFontSize(size, text);
    TextSpan sp = TextSpan(
      style: TextStyle(fontSize: fontSize, fontWeight: FontWeight.bold, color: Colors.black),
      text: text,
    );
    TextPainter tp = TextPainter(text: sp, textDirection: ui.TextDirection.ltr);
    tp.layout();

    double dx = size.width / 2 - tp.width / 2;
    double dy = size.height / 2 - tp.height / 2;
    Offset offset = Offset(dx, dy);
    tp.paint(canvas, offset);
  }

  double getFontSize(Size size, String text) {
    return size.width / text.length * textScaleFactor;
  }

  @override
  bool shouldRepaint(PieChart old) {
    return old.percentage != percentage;
  }
}