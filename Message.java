import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Message implements Serializable {
    private String content;
    private long timestamp;
    private PublicKey publicKey;
    byte[] signature;

    public Message(String content, PublicKey publicKey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, SignatureException, ClassNotFoundException, IOException {
        this.publicKey = publicKey;
        this.timestamp = System.currentTimeMillis();
        
        if (content != ""){
            this.content = DigitalSignatureExample.encrypt(content, publicKey);
            this.signature = DigitalSignatureExample.sign(content, (PrivateKey) KeyDeserialiser.loadKeyFromFile("private_key.ser"));
        } else {
            this.content = content;
            this.signature = DigitalSignatureExample.sign(content, (PrivateKey) KeyDeserialiser.loadKeyFromFile("verifier.ser"));
        }
    }
    public String getContent() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException {
        if (content == "") return content;
        return DigitalSignatureExample.decrypt(content, (PrivateKey) KeyDeserialiser.loadKeyFromFile("private_key.ser"));
    }

    public long getTimestamp() {
        return timestamp;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public byte[] getSignature() {
        return signature;
    }
}