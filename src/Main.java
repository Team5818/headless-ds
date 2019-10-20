
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class Main {
	public static void main(String[] args) throws IOException, InterruptedException {
		DatagramSocket socket = new DatagramSocket();
		InetAddress addr = InetAddress.getByName("10.58.18.2");
		
		String strData = "28 00 01 04 10 03 08 0c 03 f5 f5 f5 02 00 00 08 0c 03 f5 f3 f1 02 00 00 08 0c 03 fd fd ff 02 00 00 08 0c 03 03 fe ff 02 00 00 0c 0c 00 20 00 00 00 00 02 ff ff ff ff 0c 0c 00 20 00 00 00 00 02 ff ff ff ff";
		String strSplit[] = strData.split(" ");
		byte data[] = new byte[68];
		for(int i = 0;i < strSplit.length;i++) {
			data[i] = (byte) Integer.parseInt(strSplit[i], 16);
		}
		
		GpioController gpio = GpioFactory.getInstance();
		GpioPinDigitalInput toggleOn = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);
		toggleOn.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                for(int i = 0;event.getState().isHigh();i++) {
        			data[1] = (byte) i;
        			DatagramPacket packet = new DatagramPacket(data, 68, addr, 1110);
        			try {
						socket.send(packet);
						Thread.sleep(20);
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					}
        		}
            }

        });
		socket.close();
	}
}
