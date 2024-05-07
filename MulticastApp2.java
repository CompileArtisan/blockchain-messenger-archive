import java.io.*;
import java.net.*;
import java.lang.*;

public class MulticastApp2 extends Thread{
    private MulticastSocket socket;
    private InetAddress group;
    private int port;
    private volatile boolean running = true;

    public MulticastApp2(String multicastAddress, int port) throws IOException {
        this.group = InetAddress.getByName(multicastAddress);
        this.port = port;
        this.socket = new MulticastSocket(port);
        this.socket.joinGroup(group);
    }

    public void sendMessage(String message) throws IOException {
        message = "Received message: "+ message;
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
        socket.send(packet);
    }

    public void run() {
        byte[] buffer = new byte[4096];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (running) {
            try {
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println(received);
            } catch (Exception e) {
                System.out.println("IOException: " + e.getMessage());
            } finally {
                packet.setLength(buffer.length);
            }
        }
    }

    public void shutdown() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            try {
                socket.leaveGroup(group);
                socket.close();
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
    }

    public static void main(String[] args) throws IOException {
    	MulticastApp2 m = new MulticastApp2("239.255.255.250", 8888);
        java.util.Scanner sc = new java.util.Scanner(System.in);
        m.start();
        while (true) {
            System.out.print("Enter message: ");
            String message = sc.nextLine();
            if (message.equalsIgnoreCase("exit")) {
                m.shutdown();
                break;
            }
            m.sendMessage(message);
        }
    	// m.sendMessage(new java.util.Scanner(System.in).nextLine());
        // m.listen();
    }
}