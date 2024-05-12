import java.io.Serializable;
import java.util.LinkedList;

public class BlockChain implements Serializable {
    private LinkedList<Block> blockChain = new LinkedList<>();

    public void addBlock(Block block) {
        blockChain.add(block);
    }

    public Block getBlock(int index) {
        return blockChain.get(index);
    }

    public int size() {
        return blockChain.size();
    }

    
    
}
