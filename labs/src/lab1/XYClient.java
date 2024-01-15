package lab1;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class XYClient {
    public static void main(String[] args) throws IOException {

        Scanner s = new Scanner(System.in);
        System.out.println("Enter 2 numbers:");
        float x = s.nextFloat();
        float y = s.nextFloat();

        Socket so = new Socket(InetAddress.getByName("localhost"), 1234);
        BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(so.getOutputStream()));
        Scanner rd = new Scanner(so.getInputStream());

        wr.write(x + " " + y + "\n");
        wr.flush();
        float result = rd.nextFloat();
        System.out.println("Result from server: " + result + "%");
        so.close();

    }
}
