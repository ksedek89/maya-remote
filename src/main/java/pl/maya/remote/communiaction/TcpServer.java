package pl.maya.remote.communiaction;

import org.apache.commons.io.IOUtils;
import pl.maya.remote.usb.UsbService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TcpServer {

    ReceiveData receiveData;

    //TODO change implementation
    public TcpServer() {
        receiveData = new ReceiveData();
    }

    public void listen() {
        try {
            ServerSocket welcomeSocket = new ServerSocket(4444);
            while (true) {
                try {
                    Socket socket = welcomeSocket.accept();
                    InputStream in = socket.getInputStream();
                    DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
                    byte[] bytes = new byte[50];
                    in.read(bytes);
                    byte[] measure = receiveData.parseFrame(bytes);
                    outToClient.write(measure);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
