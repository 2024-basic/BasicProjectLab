import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:lab/api_handler.dart';
import 'package:lab/pages/problem_detail.dart';
import 'dart:convert';
import 'package:lab/styles.dart';
import 'package:lab/types/problem.dart';

import '../widets/problem_tile.dart';

class SearchScreen extends SearchDelegate<String> {

  SearchScreen()
      : super(
    searchFieldLabel: 'Search problems...',
    keyboardType: TextInputType.text,
    searchFieldStyle: TextStyle(fontSize: 20),
    textInputAction: TextInputAction.search,
  );

  @override
  ThemeData appBarTheme(BuildContext context) {
    final ThemeData theme = Theme.of(context);
    return theme.copyWith(
      appBarTheme: AppBarTheme(
        backgroundColor: Colors.white,
        elevation: 0,
        shadowColor: Colors.transparent,
      ),
      inputDecorationTheme: const InputDecorationTheme(
        hintStyle: TextStyle(color: Colors.grey),
        enabledBorder: UnderlineInputBorder(
          borderSide: BorderSide(color: Colors.white),
        ),
        focusedBorder: UnderlineInputBorder(
          borderSide: BorderSide(color: Colors.white),
        ),
      ),
      textTheme: theme.textTheme.copyWith(
        titleLarge: const TextStyle(color: Colors.black),
      ),
    );
  }

  @override
  PreferredSizeWidget buildBottom(BuildContext context) {
    return PreferredSize(
      preferredSize: const Size.fromHeight(2),
      child: Container(
        color: primaryColor,
        height: 3,
      ),
    );
  }

  @override
  List<Widget> buildActions(BuildContext context) {
    return [
      IconButton(
        icon: Icon(Icons.clear, size: 40, color: primaryColor),
        onPressed: () {
          query = ''; // 검색어 초기화
        },
      ),
    ];
  }

  @override
  Widget buildLeading(BuildContext context) {
    return IconButton(
      icon: const Icon(Icons.arrow_back_ios, size: 40, color: primaryColor),
      onPressed: () {
        close(context, ''); // 검색 화면 닫기
      },
    );
  }

  @override
  Widget buildResults(BuildContext context) {
    return FutureBuilder<List<Problem>>(
      future: _fetchProblems(query),
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return Center(child: CircularProgressIndicator());
        } else if (snapshot.hasError) {
          return Center(child: Text('Error: ${snapshot.error}'));
        } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
          return Center(child: Text('No results found.'));
        }

        final results = snapshot.data!;
        return ListView.separated(
          itemCount: results.length,
          itemBuilder: (context, index) {
            final problem = results[index];
            return ListTile(
              title: Column(
                crossAxisAlignment: CrossAxisAlignment.start,  // 왼쪽 정렬
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Expanded(
                        child: Text(
                          problem.title,
                          style: nanum15sEB,
                          overflow: TextOverflow.ellipsis,  // 넘치는 텍스트는 말줄임표로 처리
                          maxLines: 1,  // 한 줄로만 표시
                        ),
                      ),
                      Text(
                        '난이도: ${problem.level}',
                        style: nanum15sB,
                      ),
                    ],
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.end,
                    children: [
                      Text(
                        '푼 사람: ${problem.solved}',
                        style: nanum10sEB,
                      ),
                    ],
                  ),
                ],
              ),
              subtitle: Text(
                problem.description,
                style: nanum10sB,
              ),
              onTap: () {
                Navigator.push(context, MaterialPageRoute(builder: (context) => ProblemDetail(problem: problem,)));
              },
            );
          },
          separatorBuilder: (context, index) =>
              Divider(color: colorScheme.secondary),
        );
      },
    );
  }

  @override
  Widget buildSuggestions(BuildContext context) {
    return FutureBuilder<List<Problem>>(
      future: _fetchProblems(query),
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return Center(child: CircularProgressIndicator());
        } else if (snapshot.hasError) {
          return Center(child: Text('Error: ${snapshot.error}'));
        } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
          return Center(child: Text('No suggestions.'));
        }

        final suggestions = snapshot.data!;
        return ListView.separated(
          itemCount: suggestions.length,
          itemBuilder: (context, index) {
            final suggestion = suggestions[index];
            return ListTile(
              title: Column(
                crossAxisAlignment: CrossAxisAlignment.start,  // 왼쪽 정렬
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Expanded(
                        child: Text(
                          suggestion.title,
                          style: nanum15sEB,
                          overflow: TextOverflow.ellipsis,  // 넘치는 텍스트는 말줄임표로 처리
                          maxLines: 1,  // 한 줄로만 표시
                        ),
                      ),
                      Text(
                        '난이도: ${suggestion.level}',
                        style: nanum15sB,
                      ),
                    ],
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.end,
                    children: [
                      Text(
                        '푼 사람: ${suggestion.solved}',
                        style: nanum10sEB,
                      ),
                    ],
                  ),
                ],
              ),
              subtitle: Text(
                suggestion.description,
                style: nanum10sB,
              ),  // 문제 설명
              onTap: () {
                query = suggestion.title;
                //showResults(context);
                Navigator.push(context, MaterialPageRoute(builder: (context) => ProblemDetail(problem: suggestion,)));
              },
            );
          },
          separatorBuilder: (context, index) =>
              Divider(color: colorScheme.secondary),
        );
      },
    );
  }

  Future<List<Problem>> _fetchProblems(String query) async {
    if (query.isEmpty) return [];

    var ret = await ApiHandler().requestProblemsByKeyword(0, query);
    return ret.toList();
  }
}