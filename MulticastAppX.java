import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.security.*;

public class MulticastAppX extends Thread{
    private MulticastSocket socket;
    private InetAddress group;
    private int port;
    private volatile boolean running = true;
    private PublicKey receiverKey;

    public MulticastAppX(String multicastAddress, int port) throws IOException {
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
                if (!s.equals(ipv4Address)&&DigitalSignatureExample.verify(receivedMessage.getContent(), receivedMessage.getSignature(), this.receiverKey)){
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
    	MulticastAppX m = new MulticastAppX("239.255.255.250", 8888);
        java.util.Scanner sc = new java.util.Scanner(System.in);
        KeyPair keyPair = null;

        // Check if key pair files exist
        File privateKeyFile = new File("private_key.ser");
        File publicKeyFile = new File("public_key.ser");

        if (!privateKeyFile.exists() && !publicKeyFile.exists()) {
            keyPair = Login.generateKeyPair();

            PublicKey publicKey = keyPair.getPublic();
            String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());   
            System.out.print("Enter your name: ");
            System.out.println("Name: "+sc.nextLine());
            System.out.println("PublicKey: "+publicKeyString);
            System.out.println();
            
            // MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsKrByGKhBaScvikRLlYeFnbVA/GWC5KHrHvgE1P7npanpe3FaTOKrLOckO8IBPaYwzL6KAlH23kuKM29MVLjVRBJk8PtgMvTaNb095uL8Rk38ReT1iHqF3O2zcqq3bt9w/ux/Gdqf6bqolUnRM1lwG/yMUktHAeEyphoOKsfXIohh/FJVFqN9aRYeHx5K6LfAfo8VSTPt4RdM+l3xu1Z1khzOoGEdxNegkHMmK0pXLYIKINDxfL5/NXpWNyQNDPcYDrkYjOOLr7BgWBzeidxorcdBVAC5gAysZxJmeGzM7JjlJ9t+M+s1LcVfPOxazePmha7vau38NiRZdbunuUXkQIDAQAB

            // use this to get back public key:
            // String algorithm = "RSA"; // or whatever algorithm you used to generate the key pair
            // boolean isPublic = true;
            // PublicKey publicKey = (PublicKey) DigitalSignatureExample.decodeKey(publicKeyString, algorithm, isPublic);
            // (PublicKey) DigitalSignatureExample.decodeKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsKrByGKhBaScvikRLlYeFnbVA/GWC5KHrHvgE1P7npanpe3FaTOKrLOckO8IBPaYwzL6KAlH23kuKM29MVLjVRBJk8PtgMvTaNb095uL8Rk38ReT1iHqF3O2zcqq3bt9w/ux/Gdqf6bqolUnRM1lwG/yMUktHAeEyphoOKsfXIohh/FJVFqN9aRYeHx5K6LfAfo8VSTPt4RdM+l3xu1Z1khzOoGEdxNegkHMmK0pXLYIKINDxfL5/NXpWNyQNDPcYDrkYjOOLr7BgWBzeidxorcdBVAC5gAysZxJmeGzM7JjlJ9t+M+s1LcVfPOxazePmha7vau38NiRZdbunuUXkQIDAQAB", "RSA", true)

            Login.serializeKeyPair(keyPair);
        } else {
            // Deserialize the key pair
            keyPair = Login.deserializeKeyPair();
            // publicKeyString is taken from local file
            PublicKey publicKey = keyPair.getPublic();
            String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            System.out.println("Public Key: " + publicKeyString);
        }

        


        
        System.out.println("Enter receiver's Public Key: ");
        String receiverPublicKeyString = sc.nextLine();
        System.out.println("Enter receiver's name: ");
        String receiverName = sc.nextLine();
        // block initialization
        Block currentBlock = new Block("0"); // genesis block  
        m.receiverKey = (PublicKey) DigitalSignatureExample.decodeKey(receiverPublicKeyString, "RSA", true);
        currentBlock.addUserKeyPair(receiverName, (PublicKey) DigitalSignatureExample.decodeKey(receiverPublicKeyString, "RSA", true));
        m.start();
        while (true) {
            System.out.print("Enter message: ");
            String text = sc.nextLine();
            if (text.equalsIgnoreCase("exit")) {
                System.exit(0);
            }
// Similarly we'll have hard-coded public keys for users
            // Message message = new Message(text, currentBlock.getPublicKey("Praanesh"));
            Message message = new Message(text, currentBlock.getPublicKey(receiverName));
            //Message message = new Message(text, (PublicKey) DigitalSignatureExample.decodeKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsKrByGKhBaScvikRLlYeFnbVA/GWC5KHrHvgE1P7npanpe3FaTOKrLOckO8IBPaYwzL6KAlH23kuKM29MVLjVRBJk8PtgMvTaNb095uL8Rk38ReT1iHqF3O2zcqq3bt9w/ux/Gdqf6bqolUnRM1lwG/yMUktHAeEyphoOKsfXIohh/FJVFqN9aRYeHx5K6LfAfo8VSTPt4RdM+l3xu1Z1khzOoGEdxNegkHMmK0pXLYIKINDxfL5/NXpWNyQNDPcYDrkYjOOLr7BgWBzeidxorcdBVAC5gAysZxJmeGzM7JjlJ9t+M+s1LcVfPOxazePmha7vau38NiRZdbunuUXkQIDAQAB", "RSA", true));
            m.sendMessage(message);
        }
    	// m.sendMessage(new java.util.Scanner(System.in).nextLine());
        // m.listen();
    }
}