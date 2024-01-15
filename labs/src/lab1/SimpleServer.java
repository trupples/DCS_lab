package lab1;

import java.io.*;
import java.net.*;

public class SimpleServer {
    public static void main(String[] args) throws IOException {
        ServerSocket ss=null;
        Socket socket=null;
        try{
            String line="";
            ss = new ServerSocket(1900); //creates a serversocket object
            socket = ss.accept(); //start waiting at port 1900
//when a client is connected ss.accept() retruns
//a socket that identifies the connection
//creates input/output streams
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(
                            socket.getOutputStream())),true);
            while(!line.equals("END")){
                line = in.readLine(); //reads the data from the client
                out.println("ECHO "+line); //sends data to the client
            }
        }catch(Exception e){e.printStackTrace();}
        finally{
            ss.close();
            if(socket!=null) socket.close();
        }
    }
}
