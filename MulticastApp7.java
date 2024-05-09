import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.Date;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.security.*;


class Message implements Serializable {
    private String content;
    private long timestamp;
    private PublicKey publicKey;
    byte[] signature;

    public Message(String content, PublicKey publicKey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, IOException {
        this.publicKey = publicKey;
        this.content = DigitalSignatureExample.encrypt(content, publicKey);
        this.timestamp = System.currentTimeMillis();
        this.signature = DigitalSignatureExample.sign(content, (PrivateKey) KeyDeserialiser.loadKeyFromFile("private_key.ser"));
    }

    public String getContent() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException {
        
        return DigitalSignatureExample.decrypt(content, (PrivateKey) KeyDeserialiser.loadKeyFromFile("private_key.ser"));
    }

    public long getTimestamp() {
        return timestamp;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public byte[] getSignature() {
        return signature;
    }
}
  
// lmoa
public class MulticastApp7 extends Thread{
    private MulticastSocket socket;
    private InetAddress group;
    private int port;
    private volatile boolean running = true;

    public MulticastApp7(String multicastAddress, int port) throws IOException {
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
                if (!s.equals(ipv4Address)&&DigitalSignatureExample.verify(receivedMessage.getContent(), receivedMessage.getSignature(), receivedMessage.getPublicKey())){
                    System.out.println("Received message: " + receivedMessage.getContent() + ", Timestamp: " + new Date(receivedMessage.getTimestamp()));  
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                //System.out.println("IOException: " + e.getMessage());
            } finally {
                packet.setLength(buffer.length);
            }
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, SignatureException {
    	MulticastApp7 m = new MulticastApp7("239.255.255.250", 8888);
        java.util.Scanner sc = new java.util.Scanner(System.in);
        m.start();
        while (true) {
            System.out.print("Enter message: ");
            String text = sc.nextLine();
            if (text.equalsIgnoreCase("clear")) {
                System.out.print("\033[H\033[2J");
                continue;
            }
            Message message = new Message(text, (PublicKey) KeyDeserialiser.loadKeyFromFile("public_key.ser"));
            m.sendMessage(message);
        }
    	// m.sendMessage(new java.util.Scanner(System.in).nextLine());
        // m.listen();
    }
}