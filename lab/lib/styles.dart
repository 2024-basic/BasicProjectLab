import 'package:flutter/material.dart';

const Color primaryColor = Color(0xFF7878FF);
const Color secondaryColor = Color(0xFF7F8082);

ColorScheme colorScheme = const ColorScheme(
  primary: primaryColor,
  secondary: secondaryColor,
  surface: Color(0xFFFFFFFF),
  error: Color(0xFFB00020),
  onPrimary: Color(0xFFFFFFFF),
  onSecondary: Color(0xFF000000),
  onSurface: Color(0xFF000000),
  onError: Color(0xFFFFFFFF),
  brightness: Brightness.light,
);

var nanum = ({size = 20, weight = FontWeight.w700, color = primaryColor}) {
  return TextStyle(
    fontSize: size,
    color: color,
    fontFamily: 'NanumSquareNeo',
    fontWeight: weight,
  );
};

var nanum20pR = nanum();
var nanum30pR = nanum(size: 30);
var nanum15pR = nanum(size: 15);
var nanum20pB = nanum(weight: FontWeight.w800);
var nanum30pB = nanum(size: 30, weight: FontWeight.w800);
var nanum15pB = nanum(size: 15, weight: FontWeight.w800);
var nanum20sR = nanum(color: secondaryColor);
var nanum30sR = nanum(size: 30, color: secondaryColor);
var nanum15sR = nanum(size: 15, color: secondaryColor);
var nanum20sB = nanum(weight: FontWeight.w800, color: secondaryColor);
var nanum30sB = nanum(size: 30, weight: FontWeight.w800, color: secondaryColor);
var nanum15sB = nanum(size: 15, weight: FontWeight.w800, color: secondaryColor);
var nanum20pEB = nanum(weight: FontWeight.w900);
var nanum30pEB = nanum(size: 30, weight: FontWeight.w900);
var nanum15pEB = nanum(size: 15, weight: FontWeight.w900);
var nanum20sEB = nanum(weight: FontWeight.w900, color: secondaryColor);
var nanum30sEB = nanum(size: 30, weight: FontWeight.w900, color: secondaryColor);
var nanum15sEB = nanum(size: 15, weight: FontWeight.w900, color: secondaryColor);
