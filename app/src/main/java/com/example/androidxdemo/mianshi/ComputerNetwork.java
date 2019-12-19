package com.example.androidxdemo.mianshi;

/**
 * https://www.jianshu.com/p/65605622234b
 *
 * 1. 五层协议的体系结构分别是什么？每一层都有哪些协议？
 * 物理层
 * 数据链路层：逻辑链路控制LLC、媒体接入控制MAC
 * 网络层：IP协议、地址解析协议ARP、逆地址解析协议RARP、因特网控制报文协议ICMP
 * 传输层：传输控制协议TCP、用户数据报协议UDP
 * 应用层：文件传输协议FTP、远程登录协议TELNET、超文本传输协议HTTP、域名系统DNS、简单邮件协议SMTP、简单网络管理协议SNMP
 * 表示层：这一层的主要功能是定义数据格式及加密。例如，FTP允许你选择以二进制或ASCII格式传输。如果选
 *          择二进制，那么发送方和接收方不改变文件的内容。如果选择ASCII格式，发送方将把文本从发送方
 *          的字符集转换成标准的ASCII后发送数据。在接收方将标准的ASCII转换成接收方计算机的字符集。
 *          示例：加密，ASCII等。
 * 会话层：它定义了如何开始、控制和结束一个会话，包括对多个双向消息的控制和管理，以便在只完成连续消
 *          息的一部分时可以通知应用，从而使表示层看到的数据是连续的，在某些情况下，如果表示层收到了
 *          所有的数据，则用数据代表表示层。示例：RPC，SQL等。
 *
 *
 * 2. 为何有MAC地址还要IP地址？
 * 每台主机在出厂时都有一个唯一的MAC地址，但是IP地址的分配是根据网络的拓朴结构，得以保证路由选择方案建立在网络所处的拓扑位置基础而不是设备制造商的基础上
 * 使用IP地址更方便数据传输。数据包在这些节点之间的移动都是由ARP协议负责将IP地址映射到MAC地址上来完成的。
 *
 * 3. TCP和UDP的区别？
 * TCP传输控制协议：面向连接；使用全双工的可靠信道；提供可靠的服务，即无差错、不丢失、不重复且按序到达；拥塞控制、
 *                  流量控制、超时重发、丢弃重复数据等等可靠性检测手段；面向字节流；每条TCP连接只能是点到点的；用于传输可靠性要求高的数据
 * UDP用户数据报协议：无连接；使用不可靠信道；尽最大努力交付，即不保证可靠交付；无拥塞控制等；面向报文；支持一对一
 *                  、一对多、多对一和多对多的交互通信；用于传输可靠性要求不高的数据
 *
 * 4. 拥塞控制和流量控制都是什么，两者的区别？
 * 拥塞控制：对网络中的路由和链路传输进行速度限制，避免网络过载；包含四个过程：慢启动、拥塞避免、快重传和快恢复
 * 流量控制:对点和点/发送方和接收方之间进行速度匹配，由于接收方的应用程序读取速度不一定很迅速，加上缓存有限，因此需要避免发送速度过快；相关技术：TCP滑动窗口、回退N针协议
 *
 * 5. 谈谈TCP为什么要三次握手？为什么要四次挥手？
 *
 * 6. 播放视频用TCP还是UDP？为什么？
 * 播放视频适合用UDP。UDP适用于对网络通讯质量要求不高、要求网络通讯速度能尽量快的实时性应用；而TCP适用于对网络通
 * 讯质量有要求的可靠性应用。而且视频区分关键帧和普通帧，虽然UDP会丢帧但如果只是丢普通帧损失并不大，取而代之的是高速率和实时性
 *
 * 7. 了解哪些响应状态码？
 * 1xx：表示服务器已接收了客户端请求，客户端可继续发送请求
 * 2xx：表示服务器已成功接收到请求并进行处理
 *      200 OK：表示客户端请求成功
 * 3xx：表示服务器要求客户端重定向
 * 4xx：表示客户端的请求有非法内容
 *      400 Bad Request：表示客户端请求有语法错误，不能被服务器所理解
 *      401 Unauthonzed：表示请求未经授权，该状态代码必须与 WWW-Authenticate 报头域一起使用
 *      403 Forbidden：表示服务器收到请求，但是拒绝提供服务，通常会在响应正文中给出不提供服务的原因
 *      404 Not Found：请求的资源不存在，例如，输入了错误的URL
 * 5xx：表示服务器未能正常处理客户端的请求而出现意外错误
 *      500 Internal Server Error：表示服务器发生不可预期的错误，导致无法完成客户端的请求
 *      503 Service Unavailable：表示服务器当前不能够处理客户端的请求，在一段时间之后，服务器可能会恢复正常
 *
 * 8. get和post的区别？
 * GET：当客户端要从服务器中读取某个资源时使用GET；一般用于获取/查询资源信息；GET参数通过URL传递，传递的参数是有长度限制，不能用来传递敏感信息
 * POST：当客户端给服务器提供信息较多时可以使用POST；POST会附带用户数据，一般用于更新资源信息；POST将请求参数封装在HTTP 请求数据中，可以传输大量数据，传参方式比GET更安全
 *
 * 9. HTTP和TCP的区别
 * TCP是传输层协议，定义数据传输和连接方式的规范。通过三次握手建立连接、四次挥手释放连接。
 * HTTP是应用层协议，定义的是传输数据的内容的规范。HTTP的连接使用"请求-响应"方式。基于TCP协议传输，默认端口号是80。
 *
 * 10. HTTP和HTTPS的区别
 * HTTP（超文本传输协议）：运行在TCP之上；传输的内容是明文；端口是80
 * HTTPS（安全为目标的HTTP）：运行在SSL/TLS之上，SSL/TLS运行在TCP之上；传输的内容经过加密；端口是443
 *
 * 11. 下载文件多线程如何实现？
 * 通过Http报文的range字段来来实现多线程下载任务，服务器会返回content-range字段来告诉客户端文件的的大小和下载进度
 *
 * 12.DNS寻址过程
 * 1、客户机发出查询请求，在本地计算机缓存查找，若没有找到，就会将请求发送给dns服务器
 * 2、先发送给本地dns服务器，本地的就会在自己的区域里面查找，若找到，根据此记录进行解析，若没有找到，就会在本地的缓存里面查找
 * 3、本地服务器没有找到客户机查询的信息，就会将此请求发送到根域名dns服务器
 * 4、根域名服务器解析客户机请求的根域部分，它把包含的下一级的dns服务器的地址返回到客户机的dns服务器地址
 * 5、客户机的dns服务器根据返回的信息接着访问下一级的dns服务器
 * 6、这样递归的方法一级一级接近查询的目标，最后在有目标域名的服务器上面得到相应的IP信息
 * 7、客户机的本地的dns服务器会将查询结果返回给我们的客户机
 * 8、客户机根据得到的ip信息访问目标主机，完成解析过程
 *
 * 13.Http过程
 * 1.对www.baidu.com这个网址进行DNS域名解析，得到对应的IP地址
 * 2.根据这个IP，找到对应的服务器，发起TCP的三次握手
 * 3.建立TCP连接后发起HTTP请求
 * 4.服务器响应HTTP请求，浏览器得到html代码
 * 5.浏览器解析html代码，并请求html代码中的资源（如js、css图片等）（先得到html代码，才能去找这些资源）
 * 6.浏览器对页面进行渲染呈现给用户
 */
public class ComputerNetwork {
    private int field;

    class InnerClass {
        private void method() {
            ComputerNetwork.this.field = 1;
        }
    }
}
