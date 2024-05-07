import java.security.*;
import javax.crypto.*;
import java.util.Base64;
import java.security.spec.*;
import java.security.interfaces.*;

public class DigitalSignatureExample {

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
