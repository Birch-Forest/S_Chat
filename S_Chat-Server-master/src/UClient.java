/**
 * Developers:WetABQ,Shanwer
 * S_Chat Project
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class UClient {
    private String ip;
    private Socket socket;
    private BufferedReader br;
    private PrintWriter pw;
    private String Mac;

    public Socket getSocket() {
        return socket;
    }

    public String getIp() {
        return ip;
    }

    public void setSocket(final Socket socket) {
        this.socket = socket;
    }

    public BufferedReader getBr() {
        return br;
    }

    public void setBr(BufferedReader br) {
        this.br = br;
    }

    public PrintWriter getPw() {
        return pw;
    }

    public String getMac() {
        return Mac;
    }

    public void setPw(PrintWriter pw) {
        this.pw = pw;
    }

    public UClient(String address, final Socket socket) throws IOException {
        this.ip = address;
        this.socket = socket;
        this.br = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        this.pw = new PrintWriter(socket.getOutputStream());
    }

    @Override
    public String toString() {
        return "UClient [name=" + ip + ", socket="
                + socket + "]";
    }
}
