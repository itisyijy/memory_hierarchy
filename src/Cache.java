class Block{
    float price;
    public Block() {
        this.price = 0;
    }
}

public class Cache {
    int valid;
    int tag;
    Block block;
    int block_size = 2;
    public Cache() {
        this.valid = 0;
        this.tag = 0;
        this.block = new Block();
    }
    public void setValid(int valid) {
        this.valid = valid;
    }
    public void setTag(int tag) {
        this.tag = tag;
    }
    public void setBlock(float value) {
        this.block.price = value;
    }
}