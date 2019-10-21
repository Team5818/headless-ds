package org.rivierarobotics.enablekey;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Calendar;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;

public class Main {
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException, InterruptedException {
		DatagramSocket socket = new DatagramSocket();
		InetAddress addr = InetAddress.getByName("10.58.18.2");
		
		GpioController gpio = GpioFactory.getInstance();
		GpioPinDigitalInput toggleOn = gpio.provisionDigitalInputPin(RaspiPin.GPIO_05, PinPullResistance.PULL_DOWN);
		
		String strData = "00 00 01 04 10 03";
		String strSplit[] = strData.split(" ");
		byte data[] = new byte[6];
		for(int i = 0;i < strSplit.length;i++) {
			data[i] = (byte) Integer.parseInt(strSplit[i], 16);
		}
		
		Calendar c = Calendar.getInstance();
		String rawMuS = padStringToLength(Integer.toHexString((int) (c.get(Calendar.MILLISECOND) * 1000)), 8);
		StringBuilder formattedMuS = new StringBuilder(" ");
		for(int i = 0;i < rawMuS.length();i+=2) {
			formattedMuS.append(rawMuS.substring(i, i+2));
			formattedMuS.append(" ");
		}
		
		String grtData = strData + " 0f" + formattedMuS;
		String grtSplit[] = grtData.split(" ");
		byte[] grtBytes = new byte[19];
		for(int i = 0;i < grtSplit.length;i++) {
			grtBytes[i] = (byte) Integer.parseInt(grtSplit[i], 16);
		}
		int time[] = { c.get(Calendar.SECOND), c.get(Calendar.MINUTE), c.get(Calendar.HOUR), c.get(Calendar.DAY_OF_MONTH), 
				c.get(Calendar.MONTH), (c.get(Calendar.YEAR) - 1900), Integer.parseInt("10", 16), Integer.parseInt("10", 16) };
		for(int i = 0;i < time.length;i++) {
			grtBytes[grtSplit.length + i] = (byte) time[i];
		}
		DatagramPacket greeting = new DatagramPacket(grtBytes, 19, addr, 1110);
		socket.send(greeting);
		
		int globalCounter = 1;
		int indexctr = 0;
		while(true) {
			for(int i = globalCounter;toggleOn.isHigh();i++) {
				if(i > 255) {
					i = 0;
					indexctr++;
				}
				data[0] = (byte) indexctr;
				data[1] = (byte) i;
				DatagramPacket packet = new DatagramPacket(data, 6, addr, 1110);
				socket.send(packet);
				globalCounter = i;
				Thread.sleep(20);
			}
			Thread.sleep(100);
		}
	}
	
	private static String padStringToLength(String s, int length) {
		StringBuilder sb = new StringBuilder(s);
		while(sb.length() < length) { sb.insert(0, "0"); }
		return sb.toString();
	}
}
