import java.io.*;
import java.security.*;
import java.util.Base64;
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

            PublicKey publicKey = keyPair.getPublic();
            String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());                    
            System.out.println(publicKeyString);
            // MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsKrByGKhBaScvikRLlYeFnbVA/GWC5KHrHvgE1P7npanpe3FaTOKrLOckO8IBPaYwzL6KAlH23kuKM29MVLjVRBJk8PtgMvTaNb095uL8Rk38ReT1iHqF3O2zcqq3bt9w/ux/Gdqf6bqolUnRM1lwG/yMUktHAeEyphoOKsfXIohh/FJVFqN9aRYeHx5K6LfAfo8VSTPt4RdM+l3xu1Z1khzOoGEdxNegkHMmK0pXLYIKINDxfL5/NXpWNyQNDPcYDrkYjOOLr7BgWBzeidxorcdBVAC5gAysZxJmeGzM7JjlJ9t+M+s1LcVfPOxazePmha7vau38NiRZdbunuUXkQIDAQAB

            // use this to get back public key:
            // String algorithm = "RSA"; // or whatever algorithm you used to generate the key pair
            // boolean isPublic = true;
            // PublicKey publicKey = (PublicKey) DigitalSignatureExample.decodeKey(publicKeyString, algorithm, isPublic);
            // (PublicKey) DigitalSignatureExample.decodeKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsKrByGKhBaScvikRLlYeFnbVA/GWC5KHrHvgE1P7npanpe3FaTOKrLOckO8IBPaYwzL6KAlH23kuKM29MVLjVRBJk8PtgMvTaNb095uL8Rk38ReT1iHqF3O2zcqq3bt9w/ux/Gdqf6bqolUnRM1lwG/yMUktHAeEyphoOKsfXIohh/FJVFqN9aRYeHx5K6LfAfo8VSTPt4RdM+l3xu1Z1khzOoGEdxNegkHMmK0pXLYIKINDxfL5/NXpWNyQNDPcYDrkYjOOLr7BgWBzeidxorcdBVAC5gAysZxJmeGzM7JjlJ9t+M+s1LcVfPOxazePmha7vau38NiRZdbunuUXkQIDAQAB", "RSA", true);

            
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
