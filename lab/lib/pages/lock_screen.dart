import 'package:flutter/material.dart';
import 'package:lab/main.dart';
import 'package:lab/styles.dart';

class LockScreen extends StatelessWidget {
  const LockScreen({super.key});

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
                    ElevatedButton(onPressed: () {
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
                        Text('백준 계정으로 시작하기', style: nanum20sB),
                      ],
                    ))
                  ],
                ),
                SizedBox(height: 4,),
                InkWell(
                  onTap: () {
                    Navigator.pushReplacement(context, MaterialPageRoute(builder: (context) => MyApp(),));
                  },
                  child: Text('Guest Login', style: nanum15sR),
                )
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
      home: const LockScreen(),
    );
  }
}
