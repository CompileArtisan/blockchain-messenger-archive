import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.Date;

class Message implements Serializable {
    private String content;
    private long timestamp;
  
    public Message(String content) {
      this.content = content;
      this.timestamp = System.currentTimeMillis();
    }
  
    public String getContent() {
      return content;
    }
  
    public long getTimestamp() {
      return timestamp;
    }
  }
  

public class MulticastApp4 extends Thread{
    private MulticastSocket socket;
    private InetAddress group;
    private int port;
    private volatile boolean running = true;

    public MulticastApp4(String multicastAddress, int port) throws IOException {
        this.group = InetAddress.getByName(multicastAddress);
        this.port = port;
        this.socket = new MulticastSocket(port);
        this.socket.joinGroup(new InetSocketAddress(group, port), NetworkInterface.getByInetAddress(InetAddress.getLocalHost()));
    }

    public void sendMessage(Message message) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(message);
        oos.flush();
        byte[] buffer = baos.toByteArray();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
        socket.send(packet);
      }
      

    public void run() {
        byte[] buffer = new byte[4096];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (running) {
            try {
                socket.receive(packet);
                byte[] data = packet.getData();
                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                ObjectInputStream ois = new ObjectInputStream(bais);
                Message receivedMessage = (Message) ois.readObject();
                InetAddress sourceAddress = packet.getAddress();
                String s = sourceAddress.toString();
                InetAddress localHost = Inet4Address.getLocalHost();
                String ipv4Address = "/" + localHost.getHostAddress();
                if (!s.equals(ipv4Address)) {
                    System.out.println("Received message: " + receivedMessage.getContent() + ", Timestamp: " + new Date(receivedMessage.getTimestamp()));  
                }
                
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
    	MulticastApp4 m = new MulticastApp4("239.255.255.250", 8888);
        java.util.Scanner sc = new java.util.Scanner(System.in);
        m.start();
        while (true) {
            System.out.print("Enter message: ");
            String text = sc.nextLine();
            if (text.equalsIgnoreCase("exit")) {
                m.shutdown();
                break;
            }
            Message message = new Message(text);

            m.sendMessage(message);
        }
    	// m.sendMessage(new java.util.Scanner(System.in).nextLine());
        // m.listen();
    }
}