package huffman;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HuffmanED {
    private static File inputPath, codeOutputPath, cipherOutputPath, outputPath;

    public static void encode() throws IOException {
        HuffmanUtils.fillUp();
        HuffmanUtils.setIterator(inputPath);
        HuffmanUtils.doLineWithIterator((s) -> {
            for(int i = 0; i < s.length(); i++)
                HuffmanUtils.fillCharFrequenciesMap(s.charAt(i));
        });
        solveCode();

        StringBuilder sb = new StringBuilder();
        HuffmanUtils.setIterator(inputPath);
        HuffmanUtils.doLineWithIterator((s) -> {
            for(int i = 0; i < s.length(); i++)
                sb.append(HuffmanUtils.getHuffmanCodes().get(s.charAt(i)));
        });
        FileUtils.writeStringToFile(codeOutputPath, sb.toString(), "UTF-8");

        sb.delete(0, sb.length());
        HuffmanUtils.getHuffmanCodes().forEach((c, s) -> sb.append(c).append(s).append('\n'));
        FileUtils.writeStringToFile(cipherOutputPath, sb.toString(), "UTF-8");

        System.out.println("Encoded");
    }

    public static void decode() throws IOException {
        HuffmanUtils.fillUp();
        HuffmanUtils.setIterator(cipherOutputPath);
        HuffmanUtils.doLineWithIterator((s) -> {
            HuffmanUtils.getHuffmanCodes().put(s.charAt(0), s.substring(1));
        });

        HuffmanUtils.setIterator(codeOutputPath);
        StringBuilder sb = new StringBuilder();
        HuffmanUtils.doLineWithIterator(sb::append);


        List<String> values = new ArrayList<>(HuffmanUtils.getHuffmanCodes().values());
        String res = sb.toString();
        StringBuilder out = new StringBuilder();
        sb.delete(0, sb.length());
        for(int i = 0; i < res.length(); i++) {
            sb.append(res.charAt(i));
            Optional<String> tmp = values.stream().filter(s -> s.equals(sb.toString())).findAny();
            tmp.ifPresent(s -> {
                out.append(HuffmanUtils.getKeyByValue(HuffmanUtils.getHuffmanCodes(), s));
                sb.delete(0, sb.length());
            });
        }

        FileUtils.writeStringToFile(outputPath, out.toString(), "UTF-8");
        System.out.println("Decoded");
    }

    private static void solveCode(){
        Queue<Node> queue = new PriorityQueue<>(HuffmanUtils.getCharFrequencies().size());
        HuffmanUtils.getCharFrequencies().forEach((c, f) -> queue.add(new Leaf(c, f)));
        while(queue.size() > 1) queue.add(new Node(queue.poll(), queue.poll()));
        generifyHuffRecv(queue.poll(), "");
    }

    private static void generifyHuffRecv(Node node, String code){
        if(node instanceof Leaf)
            HuffmanUtils.getHuffmanCodes().put(((Leaf) node).character, code);

        if(node.left == null || node.right == null) return;

        generifyHuffRecv(node.left, code + "0");
        generifyHuffRecv(node.right, code + "1");
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        try {
            if(args.length < 4) throw new ArrayIndexOutOfBoundsException();
            System.out.println("Please, enter command, where 0 - encode, 1 - decode");
            int command = Integer.parseInt(in.nextLine());
            if(command == 0) {
                inputPath = new File(args[0]);
                codeOutputPath = new File(args[1]);
                cipherOutputPath = new File(args[2]);

                HuffmanED.encode();
            }
            else if(command == 1) {
                codeOutputPath = new File(args[1]);
                cipherOutputPath = new File(args[2]);
                outputPath = new File(args[3]);

                HuffmanED.decode();
            }
            else {
                System.out.println("Wrong Code");
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Not enough parameters (minimum 4 path)");
        } catch (IOException e) {
            System.out.println("Error with reading file. Please check path or/and file!");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
