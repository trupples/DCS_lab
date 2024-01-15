package lab2;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

class ReceiveThread extends Thread {
    private final DatagramSocket soc;
    private final JTextArea area;

    public ReceiveThread(DatagramSocket soc, JTextArea area) {
        this.soc = soc;
        this.area = area;
    }

    @Override
    public void run() {
        while(true) {

            String msg;
            byte[] buf = new byte[1000];
            DatagramPacket pak = new DatagramPacket(buf, buf.length);
            try {
                soc.receive(pak);
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
            System.out.println(bb + "; " + bb.limit() + "; " + bb.position());
            msg = StandardCharsets.UTF_8.decode(bb.position(0)).toString();

            area.setText(area.getText() + ">> " + msg + "\n");
        }
    }
}

public class ChatClient extends JFrame {
    DatagramSocket soc;

    JTextArea area;
    JScrollPane pane;
    JTextField msg;
    public ChatClient() throws IOException {
        super("Chat client - connecting");

        setSize(300, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(2, 1));

        area = new JTextArea();
        area.setText("UDP Chat App :)\n\n");
        area.setEditable(false);

        pane = new JScrollPane(area);
        pane.setSize(300, 300);
        pane.setAutoscrolls(true);
        add(pane);

        msg = new JTextField();
        msg.setSize(300, 24);
        add(msg);

        setVisible(true);

        soc = new DatagramSocket();
        soc.connect(new InetSocketAddress("localhost", 1234));
        send(" ");

        setTitle("Chat client - connected");

        msg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = msg.getText();
                msg.setText("");
                area.setText(area.getText() + "> " + message + "\n");

                send(message);
            }
        });

        new ReceiveThread(soc, area).start();
    }

    void send(String message) {
        ByteBuffer buf = StandardCharsets.UTF_8.encode(message);
        try {
            soc.send(new DatagramPacket(buf.array(), buf.array().length));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void main(String[] args) throws IOException {
        new ChatClient();
        new ChatClient();
    }
}
