import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;

public class BlockChain implements Serializable{
    private LinkedList<Block> blockChain = new LinkedList<>();


    public void addBlock(Block block) {
        blockChain.add(block);
    }

    public Block getBlock(int index) {
        return blockChain.get(index);
    }

    public Block getLastBlock() {
        return blockChain.getLast();
    }

    public int size() {
        return blockChain.size();
    }

    // method to serialize the block chain
    public void serializeBlockChain(String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // method to deserialize the block chain
    public static BlockChain deserializeBlockChain(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (BlockChain) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
