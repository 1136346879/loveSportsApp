****************命名规则****************
Activity命名：XXXActivity
Fragment命名：XXXFragment
类名：
 1、有相同后缀则以相同后缀结尾例如：
     Adapter：功能+Adapter
 	 Service：功能+Service
	 Broadcast：功能+Broadcast
	 Util：功能+Utils
 2、以功能命名例如：  数据结构、自定义控件
成员变量命名：
  m标签缩写 逻辑名称（例：mTvChangeCity）
Strings资源命名：
 1、模块名_功能
 2、公用的资源global_功能
 3、在每个模块的命名上添加注释<!--XXX-->
layout命名：
 1、activity的布局：activity_功能
 2、fragment的布局：fragment_功能
 3、自定义控件的布局：view_功能
 4、item布局：item_Adapter的类名
id命名：
 1、view字母缩写_功能
 2、LinearLayout:ll
   RelativeLayout:rl
   TextView:tv
   ImageView:iv
   ImageButton:im
   Button:btn
   EditText:et
图片命名：
 1、状态分为normal,pressed,selected,checked
 2、selector命名：图片命名_selector
 3、按钮：btn_功能_状态
 4、背景：bg_功能（如果区分状态则加上_状态）
 5、图标类：ic_功能（如果区分状态则加上_状态）
 6、图片放在drawable-xhdpi文件夹下，selector放在drawable下

****************工具类使用****************
 1、Log工具类
    LogUtil.i(tag,"msg");
2、Toast工具类 
    ToastUtils.show(参数) 即可。

****************代码注释****************
SVN提交尽量增加注释
主要函数、核心算法、关键变量需要添加注释
