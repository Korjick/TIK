package huffman;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class HuffmanUtils {

    private static LineIterator it;
    private static final Map<Character, Integer> charFrequencies;
    private static final Map<Character, String> huffmanCodes;

    static {
        charFrequencies = new HashMap<>();
        huffmanCodes = new HashMap<>();
    }

    public static void fillCharFrequenciesMap(Character character){
        Integer value = charFrequencies.get(character);
        charFrequencies.put(character, value != null ? value + 1 : 1);
    }

    public static Map<Character, Integer> getCharFrequencies() {
        return charFrequencies;
    }

    public static Map<Character, String> getHuffmanCodes(){
        return huffmanCodes;
    }

    public static void fillUp(){
        if(charFrequencies != null)
            charFrequencies.clear();
        if(huffmanCodes != null)
            huffmanCodes.clear();
    }

    public static void setIterator(File inputPath) throws IOException {
        it = FileUtils.lineIterator(inputPath, "UTF-8");
    }

    public static void doLineWithIterator(Consumer<String> characterConsumer) throws IOException {
        try {
            String line;
            while (it.hasNext()) {
                characterConsumer.accept(it.nextLine());
            }
        } finally {
            LineIterator.closeQuietly(it);
        }
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
