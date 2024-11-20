import 'package:flutter/material.dart';
import 'package:lab/styles.dart';
import 'package:lab/widets/basic_card.dart';
import 'lock_screen.dart';

class Settings extends StatefulWidget {
  const Settings({super.key});

  @override
  State<Settings> createState() => _SettingsState();
}

class _SettingsState extends State<Settings> {
  // 슬라이더 값 저장 변수
  double dpValue = 2;
  double implementationValue = 3;
  double graphValue = 0;
  double dataStructureValue = 5;
  double stringValue = 1;
  double sortingValue = 2;
  double greedyValue = 0;
  double shortestPathValue = 5;

  // 난이도 설정 값
  String difficultyLevel = "Gold";

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: ThemeData(colorScheme: colorScheme, useMaterial3: true),
      home: Scaffold(
        body: SingleChildScrollView(
          child: Container(
            color: colorScheme.surface,
            child: Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  BasicCard(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          '문제 추천 설정',
                          style: nanum25pEB
                        ),
                        SizedBox(height: 16),
                        _buildSliderSetting("D.P", dpValue, (value) {
                          setState(() {
                            dpValue = value;
                          });
                        }),
                        _buildSliderSetting("구현", implementationValue, (value) {
                          setState(() {
                            implementationValue = value;
                          });
                        }),
                        _buildSliderSetting("그래프", graphValue, (value) {
                          setState(() {
                            graphValue = value;
                          });
                        }),
                        _buildSliderSetting("자료 구조", dataStructureValue, (value) {
                          setState(() {
                            dataStructureValue = value;
                          });
                        }),
                        _buildSliderSetting("문자열", stringValue, (value) {
                          setState(() {
                            stringValue = value;
                          });
                        }),
                        _buildSliderSetting("정렬", sortingValue, (value) {
                          setState(() {
                            sortingValue = value;
                          });
                        }),
                        _buildSliderSetting("그리디", greedyValue, (value) {
                          setState(() {
                            greedyValue = value;
                          });
                        }),
                        _buildSliderSetting("최단 경로", shortestPathValue, (value) {
                          setState(() {
                            shortestPathValue = value;
                          });
                        }),
                        Padding(
                          padding: EdgeInsets.only(top: 8, bottom: 8),
                          child: Divider(
                            color: colorScheme.secondary,
                            thickness: 1,
                          ),
                        ),
                        Text(
                          '난이도 설정',
                          style: nanum25pEB
                        ),
                        SizedBox(height: 8),
                        DropdownButton<String>(
                          value: difficultyLevel,
                          isExpanded: true,
                          onChanged: (String? newValue) {
                            setState(() {
                              difficultyLevel = newValue!;
                            });
                          },
                          items: <String>['Bronze', 'Silver', 'Gold', 'Platinum', 'Diamond', 'Ruby']
                              .map<DropdownMenuItem<String>>((String value) {
                            return DropdownMenuItem<String>(
                              value: value,
                              child: Text(value, style: nanum15sB,),
                            );
                          }).toList(),
                        ),
                        Padding(
                          padding: EdgeInsets.only(top: 8, bottom: 8),
                          child: Divider(
                            color: colorScheme.secondary,
                            thickness: 1,
                          ),
                        ),
                        Center(
                          child: ElevatedButton.icon(
                            onPressed: () {
                              // 로그아웃 로직
                              Navigator.pushReplacement(
                                context,
                                MaterialPageRoute(builder: (context) => LockScreen()),
                              );
                            },
                            icon: Icon(Icons.logout),
                            label: Text('로그아웃', style: nanum15sB,),
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildSliderSetting(String label, double currentValue, ValueChanged<double> onChanged) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Expanded(
          flex: 4,
          child: Padding(
            padding: EdgeInsets.only(left: 12.0),
            child: Text(label, style: nanum20sEB),
          )
        ),
        Expanded(
          flex: 6,
          child: Slider(
            value: currentValue,
            min: 0,
            max: 10,
            divisions: 10,
            label: currentValue.round().toString(),
            onChanged: onChanged,
          ),
        ),
      ],
    );
  }
}