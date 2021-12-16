package huffman;

public class Node implements Comparable<Node> {
    public final int frequency;
    protected Node left;
    protected Node right;

    public Node(Node left, Node right) {
        this.left = left;
        this.right = right;
        this.frequency = left.frequency + right.frequency;
    }

    public Node(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public int compareTo(Node node) {
        return Integer.compare(frequency, node.frequency);
    }
}
