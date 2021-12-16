package arithmetic;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Consumer;

public class ArithmeticalUtils {
    private static LineIterator it;
    private static final Map<Character, Integer> charFrequencies;

    static {
        charFrequencies = new HashMap<>();
    }

    public static void fillCharFrequenciesMap(Character character) {
        Integer value = charFrequencies.get(character);
        charFrequencies.put(character, value != null ? value + 1 : 1);
    }

    public static Map<Character, Integer> getCharFrequencies() {
        return charFrequencies;
    }

    public static void fillUp() {
        if (charFrequencies != null)
            charFrequencies.clear();
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
}
