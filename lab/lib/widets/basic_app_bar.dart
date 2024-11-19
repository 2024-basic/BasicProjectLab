import 'package:flutter/material.dart';

import '../styles.dart';

class BasicAppBar extends StatelessWidget implements PreferredSizeWidget {
  const BasicAppBar({super.key});

  @override
  Widget build(BuildContext context) {
    return AppBar(
        backgroundColor: colorScheme.inversePrimary,
        leading: null,
        automaticallyImplyLeading: false,
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
    );
  }

  @override
  // TODO: implement preferredSize
  Size get preferredSize => Size.fromHeight(kToolbarHeight);
}
