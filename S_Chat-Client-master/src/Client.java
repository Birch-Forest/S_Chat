/**
 * Copyright © 2016 WetAQB&DreamCityAdminGroup All right reserved.
 * Welcome to DreamCity Server Address:dreamcity.top:19132
 * Created by WetAQB(Administrator) on 2017/2/4.
 * Developers:WerABQ,Shanwer;
 * |||    ||    ||||                           ||        ||||||||     |||||||
 * |||   |||    |||               ||         ||  |      |||     ||   |||    |||
 * |||   |||    ||     ||||||  ||||||||     ||   ||      ||  ||||   |||      ||
 * ||  |||||   ||   |||   ||  ||||        ||| |||||     ||||||||   |        ||
 * ||  || ||  ||    ||  ||      |        |||||||| ||    ||     ||| ||      ||
 * ||||   ||||     ||    ||    ||  ||  |||       |||  ||||   |||   ||||||||
 * ||     |||      |||||||     |||||  |||       |||| ||||||||      |||||    |
 * ||||
 */
import com.sun.jndi.cosnaming.IiopUrl;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/*
 *   client线程主要是负责：
 *   1.发送信息
 *   2.一直接收信息，并解析
 * */
public class Client {

    public static void main(String[] args) {
        try {
            String address = args[0];
            System.out.print("正在尝试连接S_Chat服务端\n");
            if (args[0] == null) {
                System.out.println("命令行实参为空，默认使用本地服务器");
                System.out.println("请输入java Client %address来选择服务器");
                address = "localhost";
            } else {
                System.out.println("你连接的服务器是:" + args[0]);
            }
            int port = 9999;
            Socket socket = new Socket(address, port);
            //开启一个线程接收信息，并解析
            ClientThread thread = new ClientThread(socket);
            thread.setName("Client");
            thread.start();
            //主线程用来发送信息
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            System.out.print("\n连接成功!服务器Ip:" + address + ":" + port + "|本机Ip:" + getLocalHostIP() + "\n");
            System.out.println("你大可使用help指令来查询你能用这个软件干什么 :-D");
            while (true) {
                String s = br.readLine();
                String[] str = s.split(" ");
                switch (str[0]) {
                    /**
                     * 本地拦截不正确语法以减少服务器负担
                     */
                    case "login":
                        if (str.length != 3) {
                            s = ".";
                            System.out.print("不正确的指令语法\n");
                            System.out.print("用法:login 用户名 密码\n");
                        }
                        break;
                    case "reg":
                        if (str.length != 3) {
                            s = ".";
                            System.out.print("不正确的指令语法\n");
                            System.out.print("用法:reg 用户名 密码\n");
                        }
                        break;
                    case "say":
                        if (str.length != 3) {
                            s = ".";
                            System.out.print("不正确的指令语法\n");
                            System.out.print("用法:say 用户名 信息\n");
                        }
                        break;
                    case "lock":
                        if (str.length != 2) {
                            s = ".";
                            System.out.print("不正确的指令语法\n");
                            System.out.print("用法:lock 用户名\n");
                        }
                        break;
                    case "exit":
                        thread.stop();
                        System.exit(1);
                        break;
                    case "help":
                        System.out.println("欢迎使用帮助系统！");
                        System.out.println("login 用法:login 用户名 密码--使用它登录账户");
                        System.out.println("reg 用法:reg 用户名 密码--使用它注册账户");
                        System.out.println("say 用法:say 用户名 信息--使用它找人聊天");
                        System.out.println("lock 用法:lock 用户名--使用它锁定用户");
                        System.out.println("exit 用法:exit--使用它依依不舍地退出该聊天程序 %>_<%");
                        System.out.println("logout 用法:logout--使用它退出当前的账号");
                        break;
                }
                out.println(s);
                //out.write(s+"\n");
                out.flush();
            }

        } catch (Exception e) {
            System.out.println("服务器异常\n");
        }
    }

        /**
         * 获取本机的IP
         *
         * @return Ip地址
         */
        public static String getLocalHostIP() {
            String ip;
            try {
                /**返回本地主机。*/
                InetAddress addr = InetAddress.getLocalHost();
                /**返回 IP 地址字符串（以文本表现形式）*/
                ip = addr.getHostAddress();
            } catch (Exception ex) {
                ip = "";
            }
            return ip;
        }
    }