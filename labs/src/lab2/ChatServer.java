package lab2;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ChatServer extends Thread {
    public static void main(String[] args) {
        new ChatServer().start();
    }

    @Override
    public void run() {
        ArrayList<SocketAddress> peers = new ArrayList<>();
        DatagramSocket ss;
        try {
            ss = new DatagramSocket(1234);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        while(true) {
            byte[] buf = new byte[1000];
            DatagramPacket pak = new DatagramPacket(buf, buf.length);
            try {
                ss.receive(pak);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            int n = 0;
            for(int i = 0; i < buf.length; i++) {
                if(buf[i] == 0) {
                    n = i;
                    break;
                }
            }

            ByteBuffer bb = ByteBuffer.allocate(n);
            bb.put(buf, 0, n);
            String s = StandardCharsets.UTF_8.decode(bb.position(0)).toString();

            SocketAddress sender = pak.getSocketAddress();
            if (!peers.contains(sender)) {
                System.out.println("Connection from " + sender);
                peers.add(sender);
            }

            System.out.println(sender + ": " + s);

            buf = StandardCharsets.UTF_8.encode(s).array();

            for (SocketAddress peer : peers) {
                if (peer.equals(sender)) continue;

                try {
                    ss.send(new DatagramPacket(buf, buf.length, peer));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}