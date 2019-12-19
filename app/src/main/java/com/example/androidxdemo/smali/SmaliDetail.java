package com.example.androidxdemo.smali;

/**
 * .class       //类名
 * .super       // 父类名
 * .source      // 源文件名
 * .implements  // 实现接口
 *
 * .annotation  // 注解
 *
 * .field       // 字段
 * .method      // 方法
 *     <clinit> : 初始化静态变量和静态代码块的方法
 *     <init>   : 构造方法
 *
 * .registers   // 寄存器的数量
 * .prologue    // 逻辑代码的开始处
 * .line        // java源代码中对应的行数
 * const-string v0, "Hello, World!" // 将字符串Hello, World!"的引用放入v0寄存器中
 * sput-object v0, LHello;->HELLO_WORLD:Ljava/lang/String; // 将寄存器v0的值赋值给Hello对象
 * return-void  // 表示无返回值
 * .end method  // 方法结束
 *
 * invoke-direct // 直接调用
 * invoke-super  // 调用最近超类的虚方法
 * invoke-virtual // 调用正常的虚方法
 * invoke-static // 调用static方法
 * invoke-interface // 调用interface方法
 *
 * move-result-object // 将结果保存在某个寄存器中
 * new-instance  // 创建一个新对象
 *
 *
 */
public class SmaliDetail {
}
