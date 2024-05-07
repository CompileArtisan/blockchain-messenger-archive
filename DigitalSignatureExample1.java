import java.security.*;
import javax.crypto.*;
import java.util.Base64;
import java.util.Scanner;
import java.security.spec.*;
import java.security.interfaces.*;

public class DigitalSignatureExample1 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // 1. Key Selection
            int keyChoice;
            System.out.println("1. Generate new key pair");
            System.out.println("2. Use existing key pair (Enter key manually) - NOT RECOMMENDED FOR PRODUCTION");
            System.out.print("Enter your choice: ");
            keyChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            KeyPair keyPair;
            if (keyChoice == 1) {
                keyPair = generateKeyPair();
                System.out.println("New key pair generated!");
            } else if (keyChoice == 2) {
                // User enters public and private keys manually (not recommended for production)
                System.out.print("Enter Public Key (Base64 encoded): ");
                String publicKeyStr = scanner.nextLine();

                System.out.print("Enter Private Key (Base64 encoded): ");
                String privateKeyStr = scanner.nextLine();

                PublicKey publicKey = (PublicKey) decodeKey(publicKeyStr, "RSA", true);
                PrivateKey privateKey = (PrivateKey) decodeKey(privateKeyStr, "RSA", false);
                keyPair = new KeyPair(publicKey, privateKey);
            } else {
                System.out.println("Invalid choice!");
                return;
            }

            // 2. Message Input
            System.out.print("Enter your message to Bob: ");
            String originalMessage = scanner.nextLine();

            // 3. Encryption, Signing, Decryption, Verification
            String encryptedMessage = encrypt(originalMessage, keyPair.getPublic());
            byte[] signature = sign(originalMessage, keyPair.getPrivate());

            String decryptedMessage = decrypt(encryptedMessage, keyPair.getPrivate());
            boolean isVerified = verify(originalMessage, signature, keyPair.getPublic());

            if (isVerified) {
                System.out.println("Message from Alice verified: " + decryptedMessage);
            } else {
                System.out.println("Message verification failed!");
            }
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error: Cryptographic algorithm not found! - " + e.getMessage());
        } catch (NoSuchPaddingException e) {
            System.err.println("Error: Padding scheme not found! - " + e.getMessage());
        } catch (InvalidKeyException e) {
            System.err.println("Error: Invalid key! - " + e.getMessage());
        } catch (IllegalBlockSizeException e) {
            System.err.println("Error: Block size is not valid! - " + e.getMessage());
        } catch (BadPaddingException e) {
            System.err.println("Error: Decryption failed due to bad padding! - " + e.getMessage());
            // You may want to handle this exception differently depending on your use case.
        } catch (SignatureException e) {
            System.err.println("Error: Signature operation failed! - " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    // Method to generate a public-private key pair
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // Key size
        return keyPairGenerator.generateKeyPair();
    }

    // Method to encrypt a message using public key
    public static String encrypt(String message, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Method to decrypt a message using private key
    public static String decrypt(String encryptedMessage, PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
        return new String(decryptedBytes);
    }

    // Method to sign a message using private key
    public static byte[] sign(String message, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(message.getBytes());
        return signature.sign();
    }

    // Method to verify the sender of a message using public key
    public static boolean verify(String message, byte[] signature, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature verifier = Signature.getInstance("SHA256withRSA");
        verifier.initVerify(publicKey);
        verifier.update(message.getBytes()); // Verify the original message
        return verifier.verify(signature);
    }

    // Method to decode a Base64 encoded key
    public static Key decodeKey(String keyStr, String algorithm, boolean isPublic) {
        byte[] keyBytes = Base64.getDecoder().decode(keyStr);
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance(algorithm);
            if (isPublic)
                return keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
            else
                return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }
}
