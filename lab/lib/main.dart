import 'package:flutter/material.dart';
import 'package:lab/home.dart';
import 'package:lab/welcome.dart';
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
  static const Length = 2;
  TabController? _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: Length, vsync: this);
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
        useMaterial3: true
      ),
      home: Scaffold(
        appBar: AppBar(
          backgroundColor: colorScheme.inversePrimary,
          title: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Padding(
                padding: const EdgeInsets.only(left: 25, top: 10),
                child: Text('evenUP', style: nanum30pEB,),
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
          children: const [
            WelcomeHome(),
            HomePage(),
          ],
        ),
        bottomNavigationBar: TabBar(tabs: const [
          Tab(icon: Icon(Icons.home_filled),),
          Tab(icon: Icon(Icons.usb),)
        ], controller: _tabController,),
      )
    );
  }
}
