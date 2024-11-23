import 'package:flutter/material.dart';
import 'package:lab/api_handler.dart';
import 'package:lab/pages/problem_detail.dart';
import 'package:lab/styles.dart';
import 'package:lab/types/problem.dart';
import 'package:lab/widets/spoiler.dart';

class SearchScreen extends SearchDelegate<String> {
  final ScrollController _scrollController = ScrollController();
  List<Problem> _problems = [];
  bool _isLoading = false;
  int _currentPage = 0;

  SearchScreen()
      : super(
    searchFieldLabel: 'Search problems...',
    keyboardType: TextInputType.text,
    searchFieldStyle: TextStyle(fontSize: 20),
    textInputAction: TextInputAction.search,
  ) {
    _scrollController.addListener(_onScroll);
  }

  void _onScroll() {
    print('Scrolling... ${_scrollController.position.pixels} / ${_scrollController.position.maxScrollExtent}');
    if (_scrollController.position.pixels == _scrollController.position.maxScrollExtent) {
      _fetchMoreProblems();
    }
  }

  Future<void> _fetchMoreProblems() async {
    _isLoading = true;
    _currentPage++;
    final newProblems = await _fetchProblems(query, _currentPage);
    _problems.addAll(newProblems);
    print('Fetched ${newProblems.length} more problems');
    _isLoading = false;
  }

  @override
  void dispose() {
    // TODO: implement dispose
    _scrollController.dispose();
    super.dispose();
  }

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
      future: _fetchProblems(query, 0),
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return Center(child: CircularProgressIndicator());
        } else if (snapshot.hasError) {
          return Center(child: Text('Error: ${snapshot.error}'));
        } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
          return Center(child: Text('No results found.'));
        }

        _problems = snapshot.data!;
        return ListView.separated(
          controller: _scrollController,
          itemCount: _problems.length + (_isLoading ? 1 : 0),
          itemBuilder: (context, index) {
            if (index == _problems.length) {
              return Center(child: CircularProgressIndicator());
            }
            final problem = _problems[index];
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
      future: _fetchProblems(query, 0),
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return Center(child: CircularProgressIndicator());
        } else if (snapshot.hasError) {
          return Center(child: Text('Error: ${snapshot.error}'));
        } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
          return Center(child: Text('No suggestions.'));
        }

        _problems = snapshot.data!;
        return ListView.separated(
          controller: _scrollController,
          itemCount: _problems.length + (_isLoading ? 1 : 0),
          itemBuilder: (context, index) {
            if (index == _problems.length) {
              return Center(child: CircularProgressIndicator());
            }
            final suggestion = _problems[index];
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
              subtitle: Spoiler(child: suggestion.description, style: nanum10sB),
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

  Future<List<Problem>> _fetchProblems(String query, int page) async {
    if (query.isEmpty) return [];

    print('Fetching problems with query: $query and page: $page');

    var ret = await ApiHandler().requestProblemsByKeyword(page, query);
    return ret.toList();
  }
}