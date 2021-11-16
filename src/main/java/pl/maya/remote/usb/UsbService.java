package pl.maya.remote.usb;
import org.usb4java.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import org.apache.commons.lang3.*;

public class UsbService {
    private final byte INTERFACE_NUMBER = 0x00;


    private static final byte OUT1_ENDPOINT = 0x01;
    private static final byte IN1_ENDPOINT = (byte) 0x81;
    private static final byte IN2_ENDPOINT = (byte) 0x82;

    private final static short ID_PRODUCT = 0X102A;
    private final static short ID_VENDOR = 0X2457;
    private DeviceHandle handle;
    public static final int INTEGRATION_TIME= 1000000;
    private int timeout = 60000;
    private static int DATA_SIZE = 4609;

    private static final byte[] INITIALIZE_MAYA = new byte[] { 0x01 };
    private static final byte[] SET_INTEGRATION_TIME = new byte[] { 0x02 };
    private static final byte[] REQUEST_SPECTRA = new byte[] { 0x09 };
    private static final byte[] QUERY_REQUEST = new byte[] {(byte) 0xFE };


    private byte diodeNumber;
    private byte orderCode ;

    public UsbService() {
        super();
    }

    public void init() {
        try {
            initUsb();
            openDevice();
            claimInterface();
            initializeMaya();
            setIntegrationTime(INTEGRATION_TIME);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIntegrationTime(int time) {

        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(time);
        byte[] b = byteBuffer.array();
        byte[] concatBytes = ArrayUtils.addAll(SET_INTEGRATION_TIME, b);
        sendCommand(concatBytes);
    }

    public void setIntegrationTime(byte[] time){
        sendCommand(ArrayUtils.addAll(SET_INTEGRATION_TIME,time));
    }

    public void initUsb() {
        int result = LibUsb.init(null);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to initialize libusb", result);
        }

    }

    public void openDevice() {
        handle = LibUsb.openDeviceWithVidPid(null, ID_VENDOR, ID_PRODUCT);
        if (handle == null) {
            System.out.println("Device not found");

        }
    }

    public void claimInterface() {
        int result = LibUsb.claimInterface(handle, INTERFACE_NUMBER);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to claim interface", result);
        }
    }

    public void initializeMaya() {
        sendCommand(INITIALIZE_MAYA);
    }


    public void spectraRequest() {
        sendCommand(REQUEST_SPECTRA);
    }

    public String queryRequest(){
        sendCommand(QUERY_REQUEST);
        ByteBuffer data = readCommand();
        byte[] b = new byte[data.remaining()];
        data.get(b);
        return new String(b);

    }


    public ByteBuffer readCommand(){
        System.out.println("Read command");


        ByteBuffer buffer = BufferUtils.allocateByteBuffer(16).order(ByteOrder.LITTLE_ENDIAN);
        IntBuffer transferred = BufferUtils.allocateIntBuffer();
        int result = LibUsb.bulkTransfer(handle, IN1_ENDPOINT, buffer, transferred, timeout);

        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to read data", result);
        }
//        System.out.println(transferred.get() + " bytes read from device");
        return buffer;
    }



    public void sendCommand(byte[] data) {
        ByteBuffer buffer = BufferUtils.allocateByteBuffer(data.length);
        buffer.put(data);
        IntBuffer transferred = BufferUtils.allocateIntBuffer();
        int result = LibUsb.bulkTransfer(handle, OUT1_ENDPOINT, buffer, transferred, timeout);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to send data", result);

        }
//        System.out.println(transferred.get() + " bytes sent to device");


    }

    public ByteBuffer readData() {
//        System.out.println("Read data");
        ByteBuffer buffer = BufferUtils.allocateByteBuffer(DATA_SIZE).order(ByteOrder.LITTLE_ENDIAN);
        IntBuffer transferred = BufferUtils.allocateIntBuffer();
        int result = LibUsb.bulkTransfer(handle, IN2_ENDPOINT, buffer, transferred, timeout);

        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to read data", result);
        }
//        System.out.println(transferred.get() + " bytes read from device");
        return buffer;
    }

    public String receiveData() {
        spectraRequest();
        ByteBuffer data = readData();
        byte[] b = new byte[data.remaining()];
        System.out.println(b.length);
        data.get(b);
        return new String(b);
    }

    public byte[] executeMeasure() {
        spectraRequest();
        ByteBuffer data = readData();
        byte[] b = new byte[data.remaining()];
        System.out.println(b.length);
        data.get(b);
        return b;
    }

}
