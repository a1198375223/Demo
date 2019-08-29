package com.example.androidxdemo.mianshi;

import android.os.Parcel;

import java.util.Arrays;

/**
 * Binder (数据拷贝只拷贝1次)
 * 1. Binder基于Client-Server通信模式，传输过程只需一次拷贝，为发送发添加UID/PID身份，既支持实名Binder也支持匿名Binder，安全性高
 * 2. Binder框架定义了四个角色：Server，Client，ServiceManager以及Binder驱动。这四个角色的关系和互联网类似：
 *    Server是服务器，Client是客户终端，ServiceManager是域名服务器（DNS），驱动是路由器。
 * 3. 通信过程：
 *    a. 首先，一个进程使用 BINDER_SET_CONTEXT_MGR 命令通过 Binder 驱动将自己注册成为 ServiceManager；
 *    b. Server 通过驱动向 ServiceManager 中注册 Binder（Server 中的 Binder 实体），表明可以对外提供服务。
 *       驱动为这个 Binder 创建位于内核中的实体节点以及 ServiceManager 对实体的引用，将名字以及新建的引用打包
 *       传给 ServiceManager，ServiceManger 将其填入查找表。
 *    c. Client 通过名字，在 Binder 驱动的帮助下从 ServiceManager 中获取到对 Binder 实体的引用，通过这个引用
 *       就能实现和 Server 进程的通信。
 *
 *
 * AIDL:
 * 1. 定义了一个IBookManager.aidl文件, android studio 会自动生成一个类(如下)
 * 分析：
 * 1. 该类继承了android.os.IInterface接口, 有一个asBinder()方法
 * 2. 内部有一个Default内部类, 实现了IBookManager, 但是所有的方法都是空方法
 * 3. 内部还有一个Stub内部类, 继承了Binder类并且实现了IBookManager接口
 * 4. Stub构造方法通过attachInterface()方法将IBookManager注册到本地Binder中去
 * 5. Stub内部有一个Proxy内部类实现了IBookManager接口, 传入一个IBinder对象来实现IPC机制, 并且在IBookManager
 *    接口对应的方法中调用Binder.transact()方法来处理跨进程通信
 * 6. Stub中的asInterface(IBinder)方法用来获取一个IBookManager接口, 如果不是IPC, 则意味着本地就有在Binder注册
 *    的IBookManager接口, 直接从Binder取出然后使用. 如果是IPC说明该Binder对象是一个远程Binder,则创建一个Proxy类
 *    来代理. 最终还是回调Stub.onTransact()方法
 * 7. Stub中的asBinder()方法返回当前的Binder对象
 * 8. Stub中的onTransact()方法是对IBookManager方法的具体操作
 *
 *
 * Bundle:
 * 使用Bundle来启动Activity service并携带数据, 这是最简单的一种跨进程通信的方式了
 *
 * 文件共享:(数据拷贝0次)
 * 不同的进程对同一个文件进行读写
 *
 * Messenger:
 * 实际上就是AIDL的使用
 *
 * ContentProvider
 *
 * Socket
 *
 * 问题：
 * 1. Android中进程和线程的关系？区别？
 *   a. 关系:
 *     * 进程是操作系统分配和管理资源的单位，线程是CPU调度和管理的单位，是CPU调度的最小单元
 *     * 进程拥有独立的地址空间，一个进程崩溃后，在保护模式下不会对其他进程产生影响，而线程间共享地址空间，线程有
 *       自己的堆栈和局部变量，一个线程崩溃会导致整个进程崩溃掉。
 *     * 一个进程可包含多个线程，即一个应用程序上可以同时执行多个任务。
 *   b. 区别:
 *     * 进程是系统进行资源分配的独立单元, 线程是cpu调度的基本单元
 *
 * 2. 为何需要进行IPC？多进程通信可能会出现什么问题？
 *   所有运行在不同进程的四大组件，只要它们之间需要通过内存共享数据，都会共享失败。由于Android为每个应用分配了独立的虚拟机，不同的虚拟机在内存分配上有不同的地址空间
 *   问题：
 *     * 因为Android机制的原因, 跨进程通信无法共享静态变量数据, 单例模式失效
 *     * 线程同步机制失效
 *     * SharedPreferences的可靠性下降
 *     * Application会多次创建
 *
 * 3. 什么是序列化？Serializable接口和Parcelable接口的区别？为何推荐使用后者？(为因在Android常常用到Bundle进行数据传输, 在内存中的序列化推荐是用Parcelable)
 *    序列化是将对象的状态信息转换为可以存储或传输的形式的过程。
 *
 *    区别：
 *    Serializable接口是Java中的序列化接口，使用简单但是开销大，序列化和反序列化过程需要大量的I/O操作，一般用
 *    于将对象序列化到存储设备中或是将对象序列化后通过网络进行传输
 *
 *    Parcelable接口是Android中的序列化方式，使用稍微麻烦，但是效率很高，是Android中推荐的序列化方式，主要用在内存序列化上
 *
 * 4. Android中为何新增Binder来作为主要的IPC方式？
 *    1. 内存只拷贝一次效率高
 *    2. Binder是基于C/S架构的, C/S架构稳定性好
 *    3. 传统Linux IPC的接收方无法获得对方进程可靠的UID/PID. 从而无法鉴别对方身份；而Binder机制为每个进程分配了UID/PID且在Binder通信时会根据UID/PID进行有效性检测。
 *
 * 5. 使用Binder进行数据传输的具体过程？
 *    a. 首先，一个进程使用 BINDER_SET_CONTEXT_MGR 命令通过 Binder 驱动将自己注册成为 ServiceManager；
 *    b. Server 通过驱动向 ServiceManager 中注册 Binder（Server 中的 Binder 实体），表明可以对外提供服务。
 *       驱动为这个 Binder 创建位于内核中的实体节点以及 ServiceManager 对实体的引用，将名字以及新建的引用打包
 *       传给 ServiceManager，ServiceManger 将其填入查找表。
 *    c. Client 通过名字，在 Binder 驱动的帮助下从 ServiceManager 中获取到对 Binder 实体的引用，通过这个引用
 *       就能实现和 Server 进程的通信。
 *
 * 6. Binder框架中ServiceManager的作用？
 *    每一个server都会通过Binder驱动向ServiceManager注册, 表示可以接受外部的访问. 当客户端发送一个访问请求的时候
 *    通过Binder驱动ServiceManager将Binder名字转换为Client中对该Binder的引用，使得Client可以通过Binder名字获得Server中Binder实体的引用
 *
 * 7. Android中有哪些基于Binder的IPC方式？简单对比下？
 *   AIDL       : 支持一对多并发通信，支持实时通信
 *   Messenger  : 底层实现是AIDL，即对AIDL进行了封装，更便于进行进程间通信。支持一对多串行通信，支持实时通信
 *
 *
 * 8. 是否了解AIDL？原理是什么？如何优化多模块都使用AIDL的情况？
 *    原理是基于Binder的IPC通信
 *    优化：使用Binder连接池来统一管理Binder
 */
public class IPCDetail {
    public static void main(String[] args) {
        // 不能运行
        Parcel parcel = Parcel.obtain();// 获取Parcel对象
        parcel.writeInt(1);
        parcel.writeCharArray(new char[]{'s', 'b', 'c'});
        byte[] data = parcel.marshall(); // parcel数据序列化
        System.out.println(Arrays.toString(data));
        parcel.setDataPosition(0); // 将指针手动指向到最初的位置
        parcel.recycle();

        parcel = Parcel.obtain();
        parcel.unmarshall(data, 0, data.length); // 反序列化
        System.out.println(parcel.readInt());
        char[] array = new char[0];
        parcel.readCharArray(array);
        System.out.println(array);
        parcel.recycle(); // parcel 回收
    }


    /*public interface IBookManager extends android.os.IInterface
    {
        *//** Default implementation for IBookManager. *//*
        public static class Default implements com.example.androidxdemo.IBookManager
        {
            @Override public java.util.List<com.example.androidxdemo.Book> getBookList() throws android.os.RemoteException
            {
                return null;
            }
            @Override public void addBook(com.example.androidxdemo.Book book) throws android.os.RemoteException
            {
            }
            @Override
            public android.os.IBinder asBinder() {
                return null;
            }
        }
        *//** Local-side IPC implementation stub class. *//*
        public static abstract class Stub extends android.os.Binder implements com.example.androidxdemo.IBookManager
        {
            private static final java.lang.String DESCRIPTOR = "com.example.androidxdemo.IBookManager";
            *//** Construct the stub at attach it to the interface. *//*
            public Stub()
            {
                this.attachInterface(this, DESCRIPTOR);
            }
            *//**
             * Cast an IBinder object into an com.example.androidxdemo.IBookManager interface,
             * generating a proxy if needed.
             *//*
            public static com.example.androidxdemo.IBookManager asInterface(android.os.IBinder obj)
            {
                if ((obj==null)) {
                    return null;
                }
                android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
                if (((iin!=null)&&(iin instanceof com.example.androidxdemo.IBookManager))) {
                    return ((com.example.androidxdemo.IBookManager)iin);
                }
                return new com.example.androidxdemo.IBookManager.Stub.Proxy(obj);
            }
            @Override public android.os.IBinder asBinder()
            {
                return this;
            }
            @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
            {
                java.lang.String descriptor = DESCRIPTOR;
                switch (code)
                {
                    case INTERFACE_TRANSACTION:
                    {
                        reply.writeString(descriptor);
                        return true;
                    }
                    case TRANSACTION_getBookList:
                    {
                        data.enforceInterface(descriptor);
                        java.util.List<com.example.androidxdemo.Book> _result = this.getBookList();
                        reply.writeNoException();
                        reply.writeTypedList(_result);
                        return true;
                    }
                    case TRANSACTION_addBook:
                    {
                        data.enforceInterface(descriptor);
                        com.example.androidxdemo.Book _arg0;
                        if ((0!=data.readInt())) {
                            _arg0 = com.example.androidxdemo.Book.CREATOR.createFromParcel(data);
                        }
                        else {
                            _arg0 = null;
                        }
                        this.addBook(_arg0);
                        reply.writeNoException();
                        return true;
                    }
                    default:
                    {
                        return super.onTransact(code, data, reply, flags);
                    }
                }
            }
            private static class Proxy implements com.example.androidxdemo.IBookManager
            {
                private android.os.IBinder mRemote;
                Proxy(android.os.IBinder remote)
                {
                    mRemote = remote;
                }
                @Override public android.os.IBinder asBinder()
                {
                    return mRemote;
                }
                public java.lang.String getInterfaceDescriptor()
                {
                    return DESCRIPTOR;
                }
                @Override public java.util.List<com.example.androidxdemo.Book> getBookList() throws android.os.RemoteException
                {
                    android.os.Parcel _data = android.os.Parcel.obtain();
                    android.os.Parcel _reply = android.os.Parcel.obtain();
                    java.util.List<com.example.androidxdemo.Book> _result;
                    try {
                        _data.writeInterfaceToken(DESCRIPTOR);
                        boolean _status = mRemote.transact(Stub.TRANSACTION_getBookList, _data, _reply, 0);
                        if (!_status && getDefaultImpl() != null) {
                            return getDefaultImpl().getBookList();
                        }
                        _reply.readException();
                        _result = _reply.createTypedArrayList(com.example.androidxdemo.Book.CREATOR);
                    }
                    finally {
                        _reply.recycle();
                        _data.recycle();
                    }
                    return _result;
                }
                @Override public void addBook(com.example.androidxdemo.Book book) throws android.os.RemoteException
                {
                    android.os.Parcel _data = android.os.Parcel.obtain();
                    android.os.Parcel _reply = android.os.Parcel.obtain();
                    try {
                        _data.writeInterfaceToken(DESCRIPTOR);
                        if ((book!=null)) {
                            _data.writeInt(1);
                            book.writeToParcel(_data, 0);
                        }
                        else {
                            _data.writeInt(0);
                        }
                        boolean _status = mRemote.transact(Stub.TRANSACTION_addBook, _data, _reply, 0);
                        if (!_status && getDefaultImpl() != null) {
                            getDefaultImpl().addBook(book);
                            return;
                        }
                        _reply.readException();
                    }
                    finally {
                        _reply.recycle();
                        _data.recycle();
                    }
                }
                public static com.example.androidxdemo.IBookManager sDefaultImpl;
            }
            static final int TRANSACTION_getBookList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
            static final int TRANSACTION_addBook = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
            public static boolean setDefaultImpl(com.example.androidxdemo.IBookManager impl) {
                if (Stub.Proxy.sDefaultImpl == null && impl != null) {
                    Stub.Proxy.sDefaultImpl = impl;
                    return true;
                }
                return false;
            }
            public static com.example.androidxdemo.IBookManager getDefaultImpl() {
                return Stub.Proxy.sDefaultImpl;
            }
        }
        public java.util.List<com.example.androidxdemo.Book> getBookList() throws android.os.RemoteException;
        public void addBook(com.example.androidxdemo.Book book) throws android.os.RemoteException;
    }*/
}
