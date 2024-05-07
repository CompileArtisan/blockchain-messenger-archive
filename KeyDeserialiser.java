import java.security.*;
import java.util.Base64;
import java.io.*;

public class KeyDeserialiser{
    public static void main(String[] args) throws Exception {
        // Load public key and private key from files
        PublicKey publicKey = (PublicKey) loadKeyFromFile("public_key.ser");
        PrivateKey privateKey = (PrivateKey) loadKeyFromFile("private_key.ser");     
        System.out.println("Keys loaded successfully.");
        // print keys
        System.out.println("Public Key: " + Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        System.out.println("Private Key: " + Base64.getEncoder().encodeToString(privateKey.getEncoded()));
    }

    // Method to load key from file
    public static Key loadKeyFromFile(String fileName) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (Key) ois.readObject();
        }
    }
}