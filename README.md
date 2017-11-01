# Shared-Route

### 介绍

New way to explore your campus life.

快递来了没时间取？你可以在这里发布你的需求，附加一定的赏金，马上就会有通过我们信誉度认证的用户看到，为您取到您的快递，送达您的手中。

![](http://opmza2br0.bkt.clouddn.com/17-10-30/90325758.jpg)

![](http://opmza2br0.bkt.clouddn.com/17-10-30/30828145.jpg)

### 项目进度

**10.13**

完成了所有页面的跳转逻辑，动画等，下一步要修改各种recyclerview的框架为swipe refresh.

**10.20**

1. 完成了寻找需求页面到购物车的传值。
2. 服务端的severlet完成，现在有一个本地服务器，基于这个本地服务器我们实现了网络登录的Demo，这里放出来demo，里面的Async框架和Post方法可供入门学习。


**10.21**

Task：

- [x] 使用sqlite数据库作为本地缓存，将所有页面更改为读取数据库的形式。——阿臻


- [x] Login 页面接入网络请求。——李博


Bug：

- [x] Login 页面所需要的版本过高，在低版本手机上闪退。——一鸣

      fixed by Wudehao

**10.22**

任务：

- [x] 完成发布需求upload task到云端，以及修改页面UI——武德浩
- [x] 完成寻找需求的从数据库fetch信息的操作，完成了利用缓存实现默认登录的效果——李博


记录：

现在一鸣和志宇都在准备考试，服务器那边的工作暂时闲置，周日我们（阿臻，我，浩哥）主要完成了一些本地功能的完善和修改，以及UI的修改。

接下来还需要考虑的重点在服务器端的逻辑设计上，等到周三晚上可以开始重新设计。

还有一点在于页面的下拉刷新，上拉加载的效果与网络请求的衔接，这里找到了一个很合适的模板，正在进行接入。

**10.27-28**

记录：

- [x] 完成订单的上传以及获取——李博
- [x] 主页查看我的订单和导航栏进入的我的订单互换，主页使用卡片式查询，导航栏进入使用recyclerview显示更详细的信息，同时保留长按动画效果。——一鸣
- [x] 服务器的云端配置——志宇
- [x] 订单逻辑完善，保障接单完成之后的信息能够显示到主页以及我的订单界面，同时完善订单状态信息的更新。——李博
- [x] 对于订单类与服务器端数据的适配，若订单类有改动，则需要改进原代码里各处涉及到这些改动的地方。——阿臻
- [x] 发布订单时加入保证金的选项——浩哥



**10.31**

登陆的时候在本地存储了学号，在后续操作中一直以这个学号作为账户的标识	--武德浩
目前设想：

发布订单的时候，同一个学号可以添加多个发单者，所以可以选择发单的人、手机、地点。

接受订单的时候，为了确保货物安全，只能让账户（学号）拥有者接单，因此不能选择接单人的信息，接单的时候从本地取出学号，发给服务器即可。

本地学号使用方法：

```java
SharedPreferences sp = getSharedPreferences("now_account", Context.MODE_PRIVATE);
String stuNum=sp.getString("now_stu_num",null);
```



**11.1**

- 本地数据库设想只用于管理购物车物品状态

- 一开始进入寻找需求页面无数据，执行下拉从网络获取数据，点击购物车，本地数据库获取这个item加入sqlite。

- 每次刷新时与本地数据库中的购物车表比对TaskID，本地数据库已有的不再出现。

- 寻找需求刷新页面的设计：

  以ID作为时间戳，ID靠后的表示时间更新，靠前显示。

  MoreTask：

  - 发送参数：Task.ID
  - 返回：服务器返回 ID - 10 条的数据，如果没有 ID - 10，则返回这个ID后所有的数据。

  RefreshTask：

  - 发送参数：无
  - 返回：**当前MAXID - 10 条任务状态为1的数据**，以及ID-1，ID-2，…..ID-9的数据，如果ID < 10，则返回MaxID~1的数据。



