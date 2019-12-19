package com.example.androidxdemo.mianshi;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * Bit最小的二进制单位 ，是计算机的操作部分。取值0或者1
 * Byte（字节）是计算机操作数据的最小单位由8位bit组成 取值（-128-127）
 * Char（字符）是用户的可读写的最小单位，在Java里面由16位bit组成 取值（0-65535）
 *
 * 字节流
 * 操作byte类型数据，主要操作类是OutputStream、InputStream的子类；不用缓冲区，直接对文件本身操作。
 *
 * 字符流
 * 操作字符类型数据，主要操作类是Reader、Writer的子类；使用缓冲区缓冲字符，不关闭流就不会输出任何内容。
 *
 * 互相转换
 * 整个IO包实际上分为字节流和字符流，但是除了这两个流之外，还存在一组字节流-字符流的转换类。
 * OutputStreamWriter：是Writer的子类，将输出的字符流变为字节流，即将一个字符流的输出对象变为字节流输出对象。
 * InputStreamReader：是Reader的子类，将输入的字节流变为字符流，即将一个字节流的输入对象变为字符流的输入对象。
 *
 * InputStream、OutputStream 基于字节操作的 IO
 * Writer、Reader 基于字符操作的 IO
 * File 基于磁盘操作的 IO
 * Socket 基于网络操作的 IO
 *
 * BIO
 *
 * java io都有哪些设计模式?
 * 装饰模式
 * 适配器模式
 */
public class IODetail {

    // File Stream
    private void fileInputStream() {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("D:\\log.txt");
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            String str = new String(bytes, StandardCharsets.UTF_8);
            System.out.println(str);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void fileOutputStream() {
        OutputStream outputStream = null; // 参数二，表示是否追加，true=追加
        try {
            outputStream = new FileOutputStream("D:\\log.txt",true);
            outputStream.write("你好，老王".getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // Writer
    private void fileWriter() {
        Writer writer = null; // 参数二，是否追加文件，true=追加
        try {
            writer = new FileWriter("D:\\log.txt",true);
            writer.append("老王，你好");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void fileReader(String filePath) {
        Reader reader = null;
        BufferedReader bufferedReader = null;
        try {
            reader = new FileReader(filePath);
            bufferedReader = new BufferedReader(reader);
            StringBuilder bf = new StringBuilder();
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                bf.append(str).append("\n");
            }
            System.out.println(bf.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void nIO(String infile, String outfile) {
        try {
            // 1. 获取数据源 和 目标传输地的输入输出流（此处以数据源 = 文件为例）
            FileInputStream fin = new FileInputStream(infile);
            FileOutputStream fout = new FileOutputStream(outfile);

            // 2. 获取数据源的输入输出通道
            FileChannel fcin = fin.getChannel();
            FileChannel fcout = fout.getChannel();

            // 3. 创建 缓冲区 对象：Buffer（共有2种方法）
            // 方法1：使用allocate()静态方法
            ByteBuffer buff = ByteBuffer.allocate(256);
            // 上述方法创建1个容量为256字节的ByteBuffer
            // 注：若发现创建的缓冲区容量太小，则重新创建一个大小合适的缓冲区

//        byte[] byteArray = new byte[1024];
            // 方法2：通过包装一个已有的数组来创建
            // 注：通过包装的方法创建的缓冲区保留了被包装数组内保存的数据
//        ByteBuffer buff = ByteBuffer.wrap(byteArray);

            // 额外：若需将1个字符串存入ByteBuffer，则如下
            String sendString="你好,服务器. ";
            ByteBuffer sendBuff = ByteBuffer.wrap(sendString.getBytes(StandardCharsets.UTF_16));

            // 4. 从通道读取数据 & 写入到缓冲区
            // 注：若 以读取到该通道数据的末尾，则返回-1
            fcin.read(buff);

            // 5. 传出数据准备：将缓存区的写模式 转换->> 读模式
            buff.flip();

            // 6. 从 Buffer 中读取数据 & 传出数据到通道
            fcout.write(buff);

            // 7. 重置缓冲区
            // 目的：重用现在的缓冲区,即 不必为了每次读写都创建新的缓冲区，在再次读取之前要重置缓冲区
            // 注：不会改变缓冲区的数据，只是重置缓冲区的主要索引值
            buff.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
