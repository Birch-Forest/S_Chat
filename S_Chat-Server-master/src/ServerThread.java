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
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.List;

/*
 *   服务器线程的作用主要是:
 *   1.接收来自客户端的信息
 *   2.将接收到的信息解析，并转发给目标客户端
 * */
public class ServerThread extends Thread {

    private User user;
    private List<User> list;
    private String lockuser = null;

    public ServerThread(User user, List<User> list) {
        this.user = user;
        this.list = list;
    }

    public void run() {
        try {
            while (true) {
                // 信息的格式：(login||logout||say),收发人,信息体
                //不断地读取客户端发过来的信息
                String msg = user.getBr().readLine();
                //System.out.println(msg);
                Boolean b = false;
                String[] str = msg.split(" ");
                switch (str[0]) {
                    case "logout":
                        sendToClient(user.getName(), "成功退出用户组:User");
                        remove(this.user);
                        stop();
                        break;
                    case "say":
                        // System.out.print("Code: 003");
                        b = true;
                        try {
                            if (str[1] != null && str[2] != null) {
                                sendToClient(user.getName(), "成功发送消息");
                                sendToClient(str[1], str[2]); // 转发信息给特定的用户
                            } else {
                                sendToClient(user.getName(), "say 对方名称 信息");
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            sendToClient(user.getName(), "say 对方名称 信息");
                        }
                        break;
                    case "lock":
                        //System.out.print("Code: 004");
                        try {
                            b = true;
                            this.lockuser = str[1];
                            sendToClient(user.getName(), "成功绑定用户聊天" + str[1]);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            sendToClient(user.getName(), "lock 对方名称");
                        }
                        break;
                    case "unlock":
                        //System.out.print("Code: 005");
                        b = true;
                        if (this.lockuser != null) {
                            this.lockuser = null;
                            sendToClient(user.getName(), "取消绑定用户聊天");
                        } else {
                            sendToClient(user.getName(), "你还没有绑定用户");
                        }
                        break;
                    case "":
                        //System.out.print("Code: 006");
                        b = true;
                        sendToClient(user.getName(), "请不要乱发空命令");
                        break;
                    default:
                        break;
                }
                if (!b) {
                    if (lockuser != null) {
                        if (msg.equals("unlock")) {
                            this.lockuser = null;
                            sendToClient(user.getName(), "取消绑定用户聊天");
                        }
                        if (msg.equals("logout")){
                            this.lockuser = null;
                            sendToClient(user.getName(), "取消绑定用户聊天");
                            sendToClient(user.getName(), "成功退出用户组:User");
                            remove(this.user);
                            stop();
                        }
                        String text = "";
                        for (int x = 0; x <= length(msg) + length(user.getName())+2; x++) {
                            text = text + "-";
                        }
                        sendToClient(lockuser, text);
                        sendToClient(lockuser, user.getName() + ": " + msg + " |");
                        sendToClient(lockuser, text);
                    }
                }
            }
        } catch (SocketException e) {
            if (e.getMessage().equals("Connection reset")) {
                System.out.print("用户" + user.getName() + "直接关闭了客户端\n");
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

    public void sendToClient(String username, String msg) {

        for (User user : list) {
            if (user.getName().equals(username)) {
                try {
                    PrintWriter pw = user.getPw();
                    pw.println("" + msg);
                    pw.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void remove(User user2) {
        UClientThread.list2.remove(user2);
        list.remove(user2);
        this.user = null;
    }

    public static int length(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int i = 0; i < value.length(); i++) {
            /* 获取一个字符 */
            String temp = value.substring(i, i + 1);
            /* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
                /* 中文字符长度为2 */
                valueLength += 2;
            } else {
                /* 其他字符长度为1 */
                valueLength += 1;
            }
        }
        return valueLength;
    }

}
