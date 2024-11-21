import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:lab/api_handler.dart';
import 'dart:convert';
import 'package:lab/styles.dart';
import 'package:lab/types/problem.dart';

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
              title: Text(
                problem.title,
                style: nanum15sB,
              ),
              onTap: () {
                close(context, problem.title); // 문제 제목 반환
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
              title: Text(
                suggestion.title,
                style: nanum15sB,
              ),
              onTap: () {
                query = suggestion.title;
                showResults(context); // 검색 결과 표시
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
    return ret.map((item) => Problem.fromJson(item)).toList();
  }
}