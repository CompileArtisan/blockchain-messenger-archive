import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class BlockChainView {
    // this class should be able to traverse through the linkedlist of the multicast block chain
    // and display the contents of each block
    // it should also be able to display the contents of the messages in each block

    private BlockChain blockChain;
    
    public void displayBlockChain() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException {
        for (int i = 0; i < blockChain.size(); i++) {
            Block block = blockChain.getBlock(i);
            System.out.println("Block " + i + ":");
            // System.out.println("Previous Hash: " + block.getPreviousHash());
            System.out.println("Hash: " + block.hash);
            //System.out.println("Message: " + block.getMessage());
            // System.out.println("Sender: " + block.getMessage().getSender());
            // System.out.println("Receiver: " + block.getMessage().getReceiver());
            System.out.println();
        }
    }

    public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException {
        BlockChainView blockChainView = new BlockChainView();
        blockChainView.blockChain = BlockChain.deserializeBlockChain("BlockChain.ser");
        blockChainView.displayBlockChain();
    }
    
}
