package lab1;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

class ConnectionThread extends Thread {
    Socket s;
    public ConnectionThread(Socket s) {
        this.s = s;
    }

    public void run() {
        System.out.println("Connection " + this.s);
        Scanner sc;
        try {
            sc = new Scanner(s.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        float x = sc.nextFloat();
        float y = sc.nextFloat();
        System.out.println("Got x=" + x + " y=" + y);
        float result = x * 100.0f / y;
        System.out.println("Sending result=" + result);

        PrintWriter wr = null;
        try {
            wr = new PrintWriter(s.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        wr.println(result);
        wr.flush();
        try {
            this.s.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Done");
    }
}

public class XYServer {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(1234);

        while(true) {
            new ConnectionThread(ss.accept()).start();
        }
    }
}
