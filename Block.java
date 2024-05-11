import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.PublicKey;
import java.util.HashMap;

public class Block implements Serializable{
    private HashMap<String, PublicKey> userKeyPairs = new HashMap<>();
    private Message message;
    private String previousHash;
    private String hash;

    public Block(String previousHash) {
        this.previousHash = previousHash;
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

    
}
