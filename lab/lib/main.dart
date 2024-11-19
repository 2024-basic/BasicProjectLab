import 'package:flutter/material.dart';
import 'package:lab/pages/home.dart';
import 'package:lab/pages/problem_list.dart';
import 'package:lab/pages/settings.dart';
import 'package:lab/pages/status.dart';
import 'styles.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp>
    with SingleTickerProviderStateMixin{
  TabController? _tabController;

  List<dynamic> pages = [
    {
      'page': const HomePage(),
      'title': '홈',
      'icon': Icons.home,
    },
    {
      'page': const ProblemList(),
      'title': '문제 목록',
      'icon': Icons.list,
    },
    {
      'page': const Status(),
      'title': '학습 현황',
      'icon': Icons.bar_chart,
    },
    {
      'page': const Settings(),
      'title': '설정',
      'icon': Icons.settings,
    }
  ];

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: pages.length, vsync: this);
  }

  @override
  void dispose() {
    // TODO: implement dispose
    _tabController!.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: colorScheme,
        useMaterial3: true,
      ),
      home: Scaffold(
        appBar: AppBar(
          backgroundColor: colorScheme.inversePrimary,
          title: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Padding(
                padding: const EdgeInsets.only(left: 25, top: 10),
                child: !Navigator.canPop(context) ?
                  Text('evenUP', style: nanum30pEB,) :
                  IconButton(onPressed: (){
                    Navigator.pop(context);
                  }, icon: const Icon(Icons.arrow_back_ios, size: 40, color: primaryColor,),),
              ),
              IconButton(onPressed: (){}, icon: const Icon(
                Icons.search,
                size: 40,
                color: primaryColor,
              ),)
            ],
          )
        ),
        body: TabBarView(
          controller: _tabController,
          children: pages.map((e) => e['page']).toList().cast(),
        ),
        bottomNavigationBar: SizedBox(
          height: 70,
          child: TabBar(
            tabs: pages.map((e) => Tab(
              icon: Icon(e['icon'], size: 40,),
              text: e['title'],
            )).toList().cast(),
            labelStyle: nanum15pEB,
            unselectedLabelColor: colorScheme.secondary,
            controller: _tabController,
          ),
        )
      ),
    );
  }
}
