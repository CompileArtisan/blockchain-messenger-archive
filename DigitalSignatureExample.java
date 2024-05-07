import java.security.*;
import javax.crypto.*;
import java.util.Base64;

public class DigitalSignatureExample {

  public static void main(String[] args) {
    try {
      // Generate public-private key pair
      KeyPair keyPair = generateKeyPair();
      System.out.println("RSA key pair generated:");
      System.out.println("Public key: " + keyPair.getPublic());

      System.out.println("Private key: " + keyPair.getPrivate());
      // Alice sends a message to Bob
      String originalMessage = "Hello, Bob!";

      // Encrypt the message with Bob's public key
      String encryptedMessage = encrypt(originalMessage, keyPair.getPublic());

      // Alice signs the original message with her private key
      byte[] signature = sign(originalMessage, keyPair.getPrivate());

      // Bob receives the message, decrypts it, and verifies the sender (Alice)
      String decryptedMessage = decrypt(encryptedMessage, keyPair.getPrivate());
      boolean isVerified = verify(originalMessage, signature, keyPair.getPublic());

      if (isVerified) {
        System.out.println("Message from Alice verified: " + decryptedMessage);
      } else {
        System.out.println("Message verification failed!");
      }
    } catch (Exception e) {
      e.printStackTrace();
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
}
