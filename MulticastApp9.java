import java.io.*;
import java.net.*;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.security.*;

public class MulticastApp9 extends Thread {
    private MulticastSocket socket;
    private InetAddress group;
    private int port;
    private volatile boolean running = true;
    private Block currentBlock;

    public MulticastApp9(String multicastAddress, int port) throws IOException {
        this.group = InetAddress.getByName(multicastAddress);
        this.port = port;
        this.socket = new MulticastSocket(port);
        this.socket.joinGroup(new InetSocketAddress(group, port), NetworkInterface.getByInetAddress(InetAddress.getLocalHost()));
        this.currentBlock = new Block("0"); // Initialize the block
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

                if (!s.equals(ipv4Address) && DigitalSignatureExample.verify(receivedMessage.getContent(), receivedMessage.getSignature(), receivedMessage.getPublicKey())) {
                    if (isPublicKeyMessage(receivedMessage)) {
                        PublicKey publicKey = receivedMessage.getPublicKey();
                        String userName = "User"; // Assuming the name is "User" for every public key
                        currentBlock.addUserKeyPair(userName, publicKey);
                        System.out.println("Added new public key for User: " + Base64.getEncoder().encodeToString(publicKey.getEncoded()));
                    } else {
                        System.out.println("Received message: " + receivedMessage.getContent() + ", Timestamp: " + new Date(receivedMessage.getTimestamp()));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                packet.setLength(buffer.length);
            }
        }
    }

    private boolean isPublicKeyMessage(Message message) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException {
        // Assuming the message content is empty for public key messages
        // Modify this condition based on your actual message structure
        return message.getContent().isEmpty() && message.getPublicKey() != null && message.getSignature() != null;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, SignatureException {
        MulticastApp9 m = new MulticastApp9("239.255.255.250", 8888);

        KeyPair keyPair = null;

        // Check if key pair files exist
        File privateKeyFile = new File("private_key.ser");
        File publicKeyFile = new File("public_key.ser");

        if (!privateKeyFile.exists() && !publicKeyFile.exists()) {
            keyPair = Login.generateKeyPair();

            PublicKey publicKey = keyPair.getPublic();
            String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            System.out.println(publicKeyString);

            Message publicKeyMessage = new Message("", publicKey); // Empty content for public key message
            m.sendMessage(publicKeyMessage);

            Login.serializeKeyPair(keyPair);
        }

        java.util.Scanner sc = new java.util.Scanner(System.in);
        m.start();
        while (true) {
            System.out.print("Enter message: ");
            String text = sc.nextLine();
            if (text.equalsIgnoreCase("exit")) {
                System.exit(0);
            }

            Message message = new Message(text, currentBlock.getPublicKey("User"));
            m.sendMessage(message);
        }
    }
}
