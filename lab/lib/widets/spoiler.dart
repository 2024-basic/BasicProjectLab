import 'package:flutter/material.dart';

class Spoiler extends StatefulWidget {
  final String child;
  final TextStyle style;
  final bool initialShow;

  const Spoiler({super.key, required this.child, required this.style, this.initialShow = false});

  @override
  State<Spoiler> createState() => _SpoilerState();
}

class _SpoilerState extends State<Spoiler> {
  late bool _showDescription;

  @override
  void initState() {
    super.initState();
    _showDescription = widget.initialShow;
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () {
        setState(() {
          _showDescription = !_showDescription;
        });
      },
      child: Text(
        _showDescription ? widget.child : '**스포일러**',
        style: widget.style,
      ),
    );
  }
}
