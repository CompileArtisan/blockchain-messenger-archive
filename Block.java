import java.io.IOException;
import java.security.PublicKey;
import java.util.HashMap;

public class Block {
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

    public PublicKey getPublicKey(String userName) {
        return userKeyPairs.get(userName);
    }
}
