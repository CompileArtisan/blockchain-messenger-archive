import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Block implements Serializable{
    private HashMap<String, PublicKey> userKeyPairs = new HashMap<>();
    private Message message;
    private String previousHash;
    public String hash;

    public Block(String previousHash) {
        this.previousHash = previousHash;
        
    }

    public String getMessage() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException{
        return message.getContent();
    }

    public void setMessage(Message message) throws InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException {
        this.message = message;
        this.hash = getHash();
    }
    
    public void addUserKeyPair(String name, String publicKeyFile) {
        try {
            PublicKey publicKey = (PublicKey) KeyDeserialiser.loadKeyFromFile(publicKeyFile);
            userKeyPairs.put(name, publicKey);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addUserKeyPair(String name, PublicKey publicKey) {
        userKeyPairs.put(name, publicKey);
    }

    public PublicKey getPublicKey(String userName) {
        return userKeyPairs.get(userName);
    }

    // method to serialize the block
    public void serializeBlock(String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // method to deserialize the block
    public static Block deserializeBlock(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (Block) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getHash() throws InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String data = "Block{" +
                    ", previousHash='" + previousHash +
                    ", message=" + message.getContent() +
                    '}';
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }  catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    
}
