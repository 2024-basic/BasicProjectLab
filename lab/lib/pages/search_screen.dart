import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:lab/styles.dart';

class SearchScreen extends SearchDelegate<String> {
  final String baseUrl = 'https://solved.ac/api/v3/search/suggestion';

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
    return FutureBuilder<List<String>>(
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
                problem,
                style: nanum15sB,
              ),
              onTap: () {
                close(context, problem); // 문제 제목 반환
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
    return FutureBuilder<List<String>>(
      future: _fetchSuggestions(query),
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
                suggestion,
                style: nanum15sB,
              ),
              onTap: () {
                query = suggestion;
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

  Future<List<String>> _fetchProblems(String query) async {
    if (query.isEmpty) return [];
    final url = Uri.parse('$baseUrl/problem?query=$query');
    final response = await http.get(url, headers: {
      'x-solvedac-language': 'ko', // 필요한 언어 설정
    });

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body) as Map<String, dynamic>;
      final problems = data['items'] as List<dynamic>;
      return problems.map((item) => item['title'] as String).toList();
    } else {
      throw Exception('Failed to fetch problems');
    }
  }

  Future<List<String>> _fetchSuggestions(String query) async {
    if (query.isEmpty) return [];

    final url = Uri.parse('$baseUrl?query=${Uri.encodeComponent(query)}');
    final response = await http.get(
      url,
      headers: {
        'Accept': 'application/json',
        'x-solvedac-language': 'ko',
      },
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body) as Map<String, dynamic>;
      final autocomplete = data['autocomplete'] as List<dynamic>;
      return autocomplete.map((item) => item['caption'] as String).toList();
    } else {
      throw Exception('Failed to fetch suggestions');
    }
  }
}
