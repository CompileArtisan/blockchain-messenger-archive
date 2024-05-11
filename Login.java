import java.io.*;
import java.security.*;
import java.util.HashMap;

public class Login {
    private static final String PRIVATE_KEY_FILE = "private_key.ser";
    private static final String PUBLIC_KEY_FILE = "public_key.ser";

    public static void main(String[] args) {
        KeyPair keyPair = null;

        // Check if key pair files exist
        File privateKeyFile = new File(PRIVATE_KEY_FILE);
        File publicKeyFile = new File(PUBLIC_KEY_FILE);

        if (privateKeyFile.exists() && publicKeyFile.exists()) {
            keyPair = deserializeKeyPair();
        } else {
            keyPair = generateKeyPair();
            System.out.println(keyPair.getPublic());
            serializeKeyPair(keyPair);
        }

        
    }

    private static KeyPair deserializeKeyPair() {
        try (ObjectInputStream privateInput = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
             ObjectInputStream publicInput = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE))) {

            PrivateKey privateKey = (PrivateKey) privateInput.readObject();
            PublicKey publicKey = (PublicKey) publicInput.readObject();

            return new KeyPair(publicKey, privateKey);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void serializeKeyPair(KeyPair keyPair) {
        try (ObjectOutputStream privateOutput = new ObjectOutputStream(new FileOutputStream(PRIVATE_KEY_FILE));
             ObjectOutputStream publicOutput = new ObjectOutputStream(new FileOutputStream(PUBLIC_KEY_FILE))) {

            privateOutput.writeObject(keyPair.getPrivate());
            publicOutput.writeObject(keyPair.getPublic());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    // userKeyPairs.put(userName, new UserKeyPair(publicKeyFile, privateKeyFile)); to insert a keypair
    // UserKeyPair userKeyPair = userKeyPairs.get(userName); to get a keypair
    
    // Similar for other users
}
