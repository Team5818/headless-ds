package org.rivierarobotics.enablekey;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;

public class Main {
	public static void main(String[] args) throws IOException, InterruptedException {
		DatagramSocket socket = new DatagramSocket();
		InetAddress addr = InetAddress.getByName("192.168.1.100");
		
		String strData = "28 00 01 04 10 03 08 0c 03 f5 f5 f5 02 00 00 08 0c 03 f5 f3 f1 02 00 00 08 0c 03 fd fd ff 02 00 00 08 0c 03 03 fe ff 02 00 00 0c 0c 00 20 00 00 00 00 02 ff ff ff ff 0c 0c 00 20 00 00 00 00 02 ff ff ff ff";
		String strSplit[] = strData.split(" ");
		byte data[] = new byte[68];
		for(int i = 0;i < strSplit.length;i++) {
			data[i] = (byte) Integer.parseInt(strSplit[i], 16);
		}
		
		GpioController gpio = GpioFactory.getInstance();
		GpioPinDigitalInput toggleOn = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);
		int globalCounter = 0;
		while(true) {
			for(int i = globalCounter;toggleOn.isHigh();i++) {
				data[1] = (byte) i;
				DatagramPacket packet = new DatagramPacket(data, 68, addr, 1110);
				socket.send(packet);
				globalCounter = i;
				Thread.sleep(20);
			}
			Thread.sleep(100);
		}
	}
}
