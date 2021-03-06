#  **UCT算法实现五子棋AI**

## 环境

Java 1.8 (界面使用的是1.8内置的JavaFx)

## 运行方式

运行UI类 src.mcts.ui.GoBangUI.java

（目前UI类功能比较简单，仅仅实现AI对局功能）
## UCT基本思想
![UCT基本思想](https://gitee.com/kdldbq/uct-gobang/raw/master/result/MCTS_(English).svg.png "UCT基本思想")

（本图来自于维基百科[蒙特卡洛树搜索]页面）

大多数当代蒙特卡洛树搜索的实现都是基于UCT的一些变形。

[1] selection（选择）从根节点开始，选择连续的子节点向下至叶子节点，选择的方式使用UCB公式。

[2] expansion（扩展）除非任意一方的输赢使得游戏在叶节点结束，否则创建一个或多个子节点并选取其中一个节点。（本项目中选择创建多个子节点）

[3] simulation（模拟）在从选择的子节点开始，用随机策略进行游戏

[4] backPropagation（传播）使用随机游戏的结果，更新从选择子节点至根节点。

（以上说明部分借鉴于维基百科[蒙特卡洛树搜索]页面）

【UCB公式】
![UCB公式](https://gitee.com/kdldbq/uct-gobang/raw/master/result/4d380bf26dc9feb4d3cb45c58adb1027cd575479.svg "UCB公式")
（本图来自于维基百科[蒙特卡洛树搜索]页面）
- wi代表第i次移动后取胜的次数；
- n_i代表第i次移动后仿真的次数；
- c为探索参数—理论上等于 sqrt2 在实际中通常可凭经验选择（我的理解：C越大越倾向于那些分数不高，但是模拟次数少的，尽量去尝试更多也许看起来不好的下法）；
- t代表仿真总次数，等于所有n_i的和。

（以上说明部分借鉴于维基百科[蒙特卡洛树搜索]页面）

## 总结

1. 将模拟时间设置为5秒，大部分情况都可以赢我（我执黑棋的情况），我的水平不是很高；
2. 将模拟时间设置至30秒，勉强可以和网上一个五子棋高手模式战平（因为棋盘大小原因，没有模拟到最后，大部分情况没有输赢），但是个人感觉处于落后状态；
3. 如果仅仅使用树搜索，一直随机模拟对局，最后的结果并不理想，增加了形势判断，对三连子、四连子等情况的判断打分，大大增加了胜算；
4. 特定情况（对方四连字或己方四连子）直接堵子，不进行模拟对局，缩短垃圾搜索时间。

