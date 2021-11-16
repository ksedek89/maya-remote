package pl.maya.remote;

import pl.maya.remote.communiaction.TcpServer;
import pl.maya.remote.usb.UsbService;

public class StartApplication {
    public static void main(String[]  args){
        new TcpServer().listen();
    }
}
