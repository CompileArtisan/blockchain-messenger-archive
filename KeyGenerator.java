import java.io.*;
import java.security.*;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) throws Exception {
        // Generate key pair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(512); // You can change the key size as per your requirement
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Get public and private keys
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // print keys
        System.out.println("Public Key: " + Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        System.out.println("Private Key: " + Base64.getEncoder().encodeToString(privateKey.getEncoded()));
        // Save keys to files
        saveKeyToFile(publicKey, "public_key.ser");
        saveKeyToFile(privateKey, "private_key.ser");

        System.out.println("Keys generated and saved successfully.");
    }

    // Method to save key to file in .ser format
    public static void saveKeyToFile(Key key, String fileName) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(key);
        }
        System.out.println("Key saved to file " + fileName);
    }
}
