/**
 * Developers:WetABQ,Shanwer
 * S_Chat Project
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *   服务器线程暂时的作用主要是:
 *   1.接收来自客户端的信息
 *   2.将接收到的信息解析，并转发给目标客户端
 *   3.存储登录访问用户
 * */
public class UClientThread extends Thread {

    private UClient user;
    private static List<UClient> list;

    public UClientThread(UClient user, List<UClient> list) {
        this.user = user;
        this.list = list;
    }

    public static List<User> list2 = new ArrayList<User>();
    ;

    public void run() {
        try {
            while (true) {
                // 信息的格式：(login||logout||say),发送人,收发人,信息体
                //不断地读取客户端发过来的信息
                boolean c = true;
                try {
                    for (User u : list2) {
                        if (u.getSocket().equals(user.getSocket())) {
                            c = false;
                        }
                    }
                } catch (NullPointerException e) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String msg = "";
                if (c) {
                    //System.out.print(list);
                    msg = user.getBr().readLine();
                    String[] str = msg.split(" ");
                    switch (str[0]) {
                        case "reg":
                            //System.out.print("Code: 001");
                            boolean reg = false;
                            try {
                                String spassword = str[2];
                                Map<String, Map<String, String>> map2 = Server.UserList;
                                for (Map.Entry<String, Map<String, String>> entry : map2.entrySet()) {
                                    if (entry.getKey().equals(str[1])) {
                                        reg = true;
                                    }
                                }
                                if (!reg) {
                                    if (spassword.length() >= 8) {
                                        if (spassword.length() <= 32) {
                                            Map<String, String> map = new HashMap<>();
                                            map.put("password", spassword);
                                            Server.UserList.put(str[1], map);
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
                                                    String sql = "insert into user(user,password) values ('" + str[1] + "','" + spassword + "')";
                                                    Boolean query2 = statement.execute(sql);
                                                    if (query2) { //返回false为正常 true为异常
                                                        System.out.print("数据库错误 003 - 创建S_Chat用户数据库时出错");
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
                                            sendToClient(user.getSocket(), "注册成功！->正在登陆");
                                            User u2 = new User(str[1], user.getSocket());
                                            list2.add(u2);
                                            ServerThread thread2 = new ServerThread(u2, list2);
                                            thread2.start();
                                            System.out.print("用户" + u2.getName() + "注册并登录了S_Chat\n");
                                            sendToClient(user.getSocket(), "登录成功-用户组:UClient->User\n");
                                            sendToClient(user.getSocket(), "可以使用logout进行退出");
                                        } else {
                                            sendToClient(user.getSocket(), "注册失败,密码太长了 ≤ 32位");
                                        }
                                    } else {
                                        sendToClient(user.getSocket(), "注册失败,密码太短了 ≥ 8位");
                                    }
                                } else {
                                    sendToClient(user.getSocket(), "注册失败,账户已有人注册");
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                sendToClient(user.getSocket(), "reg 账号 密码");
                            }
                            break;
                        case "login":
                            //System.out.print("Code: 002");
                            boolean a = false;
                            String password = null;
                            try {
                                Map<String, Map<String, String>> map = Server.UserList;
                                for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
                                    if (entry.getKey().equals(str[1])) {
                                        a = true;
                                        password = entry.getValue().get("password");
                                    }
                                }

                                //System.out.print(a);
                                //System.out.print(password);
                                if (a && !password.equals(null)) {
                                    if (password.equals(str[2])) {
                                        User u3 = new User(str[1], user.getSocket());
                                        list2.add(u3);
                                        ServerThread thread = new ServerThread(u3, list2);
                                        thread.start();
                                        sendToClient(user.getSocket(), "登录成功-用户组:UClient->User");
                                        sendToClient(user.getSocket(), "可以使用logout进行退出");
                                        System.out.print("用户" + u3.getName() + "登录了S_Chat\n");
                                    } else {
                                        sendToClient(user.getSocket(), "登录失败,账户名或者密码不正确");
                                    }
                                } else {
                                    sendToClient(user.getSocket(), "登录失败,账户名或者密码不正确");
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                sendToClient(user.getSocket(), "login 账号 密码");
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (SocketException e) {
            if (e.getMessage().equals("Connection reset")) {
                System.out.print("用户" + user.getIp() + "直接关闭了客户端\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("异常");
        } finally {
            try {
                user.getBr().close();
                user.getSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void sendToClient(Socket socket, String msg) {
        for (UClient user : list) {
            if (user.getSocket() == socket) {
                try {
                    PrintWriter pw = user.getPw();
                    pw.println(msg);
                    pw.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void remove(User user2) {
        list.remove(user2);
    }
}
