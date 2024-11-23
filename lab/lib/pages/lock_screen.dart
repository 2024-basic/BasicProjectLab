import 'package:flutter/material.dart';
import 'package:lab/api_handler.dart';
import 'package:lab/main.dart';
import 'package:lab/styles.dart';

class LockScreen extends StatelessWidget {
  LockScreen({super.key});

  final TextEditingController _idcontroller = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        color: Color(0xFFF0F0F1),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Column(
              mainAxisAlignment: MainAxisAlignment.center,
              mainAxisSize: MainAxisSize.max,
              children: [
                Text('evenUp', style: nanum(size: 60, color: primaryColor, weight: FontWeight.w900)),
                SizedBox(height: 40),
                Column(
                  mainAxisSize: MainAxisSize.max,
                  children: [
                    SizedBox(
                      width: 300, // 명시적으로 너비를 설정하거나 Expanded를 사용할 수 있습니다.
                      child: TextFormField(
                        controller: _idcontroller,
                        decoration: const InputDecoration(
                          labelText: 'Input your Backjoon ID',
                          labelStyle: TextStyle(
                            color: Colors.grey,
                          ),
                          floatingLabelStyle: TextStyle(
                            color: primaryColor,
                          )
                        ),
                      ),
                    ),
                    SizedBox(height: 30,),
                    ElevatedButton(onPressed: () {
                      ApiHandler().login(_idcontroller.text);
                      Navigator.pushReplacement(context, MaterialPageRoute(builder: (context) => MyApp(),));
                    }, style: ElevatedButton.styleFrom(
                      padding: EdgeInsets.symmetric(vertical: 20, horizontal: 40),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(5),
                      ),
                      foregroundColor: Colors.black,
                      backgroundColor: Colors.white,
                    ),
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: [
                            Image.asset('assets/boj_icon.png', width: 40,),
                            SizedBox(width: 40,),
                            Text('백준 id로 시작하기', style: nanum20sB),
                          ],
                        ))
                  ],
                ),
              ],
            ),
            SizedBox(height: 60,)
          ],
        ),
      ),
    );
  }
}

class LockScreenApp extends StatelessWidget {
  const LockScreenApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: colorScheme,
        useMaterial3: true,
      ),
      home: LockScreen(),
    );
  }
}