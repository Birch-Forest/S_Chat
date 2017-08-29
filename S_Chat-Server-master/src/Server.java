/**
 * Copyright © 2016 WetAQB&DreamCityAdminGroup All right reserved.
 * Welcome to DreamCity Server Address:dreamcity.top:19132
 * Created by WetAQB(Administrator) on 2017/2/4.
 * |||    ||    ||||                           ||        ||||||||     |||||||
 * |||   |||    |||               ||         ||  |      |||     ||   |||    |||
 * |||   |||    ||     ||||||  ||||||||     ||   ||      ||  ||||   |||      ||
 * ||  |||||   ||   |||   ||  ||||        ||| |||||     ||||||||   |        ||
 * ||  || ||  ||    ||  ||      |        |||||||| ||    ||     ||| ||      ||
 * ||||   ||||     ||    ||    ||  ||  |||       |||  ||||   |||   ||||||||
 * ||     |||      |||||||     |||||  |||       |||| ||||||||      |||||    |
 * ||||
 */

import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//服务器类
public class Server {
    /*
    创建一个User库 用于储存所有的User [临时]
     */
    public static Map<String, Map<String, String>> UserList = new HashMap<>();

    public static void main(String[] args) throws Exception {
        long starttime = System.nanoTime();
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.print("数据库错误 001 - 无法找到数据库驱动\n");
            e.printStackTrace();
        }
        String url = "jdbc:mysql://localhost:3306/schat?useUnicode=true&characterEncoding=gbk";
        String username = "root";
        String password = "root";
        String driver = "com.mysql.jdbc.Driver";
        try {
            Class.forName(driver);
            Connection mysql = DriverManager.getConnection(url, username, password);
            Statement statement = mysql.createStatement();
            if (!mysql.isClosed()) {
                System.out.print("已经连接上S_Chat用户数据库\n");
                String sql = "select * from user";
                ResultSet resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("password", resultSet.getString("password"));
                    UserList.put(resultSet.getString("user"), map);
                }
            } else {
                System.out.print("数据库错误 002 - 连接S_Chat用户数据库失败!\n");
            }
            mysql.close();
            statement.close();
        } catch (ClassNotFoundException e) {
            System.out.print("数据库错误 001 - 无法找到数据库驱动\n");
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Map<String, String> map = new HashMap<>();
        map.put("password", "2333");
        UserList.put("WetABQ", map);
        List<UClient> list2 = new ArrayList<>();
        // 创建绑定到特定端口的服务器套接字
        @SuppressWarnings("resource")
        ServerSocket serverSocket = new ServerSocket(9999);
        System.out.println("S_Chat Server 加载完毕\n");
        long nanotime = System.nanoTime() - starttime;
        BigDecimal b = new BigDecimal(((double) nanotime / 1000000000));
        double loadtime = b.setScale(8, BigDecimal.ROUND_HALF_UP).doubleValue();
        System.out.print("S_Chat Server 加载使用了 " + loadtime + "s\n");
        // 循环监听客户端连接
        while (true) {
            Socket socket = serverSocket.accept();
            // 每接受一个线程，就随机生成一个一个新用户
            //User user = new User("user" + Math.round(Math.random() * 100),socket);
            System.out.println(socket.getInetAddress().getHostAddress() + "正在尝试登录。。。");
            UClient UClient = new UClient(socket.getInetAddress().getHostAddress(), socket);
            list2.add(UClient);
            // 创建一个新的线程，接收信息并转发
            UClientThread thread = new UClientThread(UClient, list2);
            thread.sendToClient(socket, "\n请输入login 账号 密码 来进行登录");
            thread.sendToClient(socket, "或输入reg 账号 密码 来进行注册");
            thread.start();
        }
    }
}
