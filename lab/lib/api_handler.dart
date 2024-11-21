import 'dart:convert';
import 'dart:math';
import 'package:http/http.dart' as http;
import 'package:lab/types/problem.dart';

void main() {
  print('Hello, World!');
  // ApiHandler().requestProblem(1000).then((value) => print(value));
  // ApiHandler().requestProblemsRange(1000, 1010).then((list) => list.forEach((element) => print(element)));
  ApiHandler().requestProblemsByKeyword(0, "A + B").then((list) => list.forEach((element) => print(element)));
}

// 사용 방법: ApiHandler().requestString('https://localhost:8080/problem/1000').then((value) => print(value));
class ApiHandler {
  static const String BASE_URL = 'http://localhost:8080/';
  static final ApiHandler _instance = ApiHandler._internal();

  String userId = '';

  factory ApiHandler() {
    return _instance;
  }

  ApiHandler._internal();

  void login(String userId) {
    this.userId = userId;
  }

  void logout() {
    this.userId = '';
  }

  Future<String> requestString(String url) {
    return http.get(Uri.parse(url)).then((resp) {
      if (resp.statusCode == 200) {
        return utf8.decode(resp.bodyBytes);
      } else {
        throw Exception('Failed to load data from $url');
      }
    });
  }

  Future<Map<String, dynamic>> requestJson(String url) {
    return requestString(url).then((value) => json.decode(value));
  }

  Future<List<dynamic>> requestJsonList(String url) {
    return requestString(url).then((value) => json.decode(value));
  }

  Future<Problem> requestProblem(int id) {
    return requestJson('${BASE_URL}problem/$id').then((value) => Problem.fromJson(value));
  }

  Future<List<Problem>> requestProblemsRange(int startId, int endId) {
    if (startId > endId) throw Exception('Invalid range');
    return requestJsonList('${BASE_URL}problems?start=$startId&end=$endId').then((v) {
      var ret = <Problem>[];
      for (var item in v) {
        ret.add(Problem.fromJson(item));
      }
      return ret;
    });
  }

  Future<List<Problem>> requestProblemsByKeyword(int page, String kw) {
    if (page < 0) throw Exception('Invalid page number');
    return requestJson('${BASE_URL}recommended-problems?page=$page&kw=${Uri.encodeComponent(kw)}&levelStart=1&levelEnd=30&isAsc=false&searchMode=true').then((v) {
      var lst = v['content'] as List<dynamic>;
      var ret = <Problem>[];
      for (var item in lst) {
        ret.add(Problem.fromJson(item));
      }
      return ret;
    });
  }

}