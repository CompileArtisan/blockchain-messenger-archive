import java.net.*;
import java.util.Scanner;

public class Sender {

    private static final int PORT = 8888;
    private static String DISCOVERY_MESSAGE;

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the message to broadcast: ");
            DISCOVERY_MESSAGE = scanner.nextLine();
            InetAddress group = InetAddress.getByName("239.255.255.250");
            MulticastSocket socket = new MulticastSocket(PORT);
            socket.joinGroup(group);

            // Broadcast discovery message
            DatagramPacket packet = new DatagramPacket(DISCOVERY_MESSAGE.getBytes(), DISCOVERY_MESSAGE.length(), group, PORT);
            socket.send(packet);

            socket.leaveGroup(group);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
