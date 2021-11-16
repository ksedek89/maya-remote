package pl.maya.remote.communiaction;

import pl.maya.remote.usb.UsbService;

public class ReceiveData {
    int integrationTime = UsbService.INTEGRATION_TIME;
    UsbService usbService;

    public ReceiveData() {
        usbService = new UsbService();
        usbService.init();
    }

    public byte[] parseFrame(byte[]  bytes){
        String frame = new String(bytes).trim();
        try {
            String[] values = frame.split(",");
            String date = values[0];
            boolean onlyDiode = values[1].equalsIgnoreCase("0");
            int integrationTime = Integer.valueOf(values[2]) * 1000;
            String filter = values[3];
            String diode = values[4];

            System.out.println("date: " + date + ", onlyDiode: " + onlyDiode + ", integrationTime: " + integrationTime + " µs, filter: " + filter + ", diode: " + diode);

            byte[] responseByte = null;
            if (this.integrationTime != integrationTime) {
                System.out.println("Zmieniam IntegrationTime");
                usbService.setIntegrationTime(integrationTime);
                this.integrationTime = integrationTime;
            }
            if (onlyDiode) {
                responseByte = usbService.executeMeasure();
            } else {
                if (ustawDiody()) {
                    responseByte = usbService.executeMeasure();
                }
            }
            if(responseByte!=null){
                return zwrocRamke(responseByte, "1");
            }else{
                return zwrocRamke(new byte[]{},"0");
            }
        }catch (Exception e){
            e.printStackTrace();

        }
        return null;
    }

    public boolean ustawDiody(){
        //TODO
        return true;
    }

    public byte[] zwrocRamke(byte[] data, String status){
        return concatenateByteArrays(status.getBytes(),data);

    }

    byte[] concatenateByteArrays(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}
