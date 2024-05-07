import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;

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

public class MulticastApp5 extends Thread {
    private MulticastSocket socket;
    private InetAddress group;
    private int port;
    private volatile boolean running = true;
    private SecretKey secretKey;

    public MulticastApp5(String multicastAddress, int port) throws IOException {
        this.group = InetAddress.getByName(multicastAddress);
        this.port = port;
        this.socket = new MulticastSocket(port);
        this.socket.joinGroup(group);
    }

    public void sendMessage(Message message) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(message);
        oos.flush();
        byte[] serializedMessage = baos.toByteArray();

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedContent = cipher.doFinal(serializedMessage);

        DatagramPacket packet = new DatagramPacket(encryptedContent, encryptedContent.length, group, port);
        socket.send(packet);
    }

    public void run() {
        byte[] buffer = new byte[4096];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (running) {
            try {
                socket.receive(packet);

                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
                byte[] decryptedContent = cipher.doFinal(packet.getData());

                ByteArrayInputStream bais = new ByteArrayInputStream(decryptedContent);
                ObjectInputStream ois = new ObjectInputStream(bais);
                Message receivedMessage = (Message) ois.readObject();
                ois.close();

                System.out.println("Received message: " + receivedMessage.getContent() + ", Timestamp: " + new Date(receivedMessage.getTimestamp()));

            } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            try {
                socket.leaveGroup(group);
                socket.close(); // Close the socket
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        MulticastApp5 m = new MulticastApp5("239.255.255.250", 8888);
        Scanner sc = new Scanner(System.in);
        m.start();

        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        m.secretKey = keyGen.generateKey();

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
        sc.close();
    }
}
