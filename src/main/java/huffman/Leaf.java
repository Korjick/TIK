package huffman;

public class Leaf extends Node {
    public final char character;

    public Leaf(char character, int frequency) {
        super(frequency);
        this.character = character;
    }
}
