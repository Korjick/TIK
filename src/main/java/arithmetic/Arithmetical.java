package arithmetic;

import javafx.util.Pair;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Arithmetical {

    public static final int SCALE = 1024;
    private static File inputPath, codeOutputPath, cipherOutputPath, outputPath;
    private static Integer totalLength;
    private static boolean first;
    private static List<Map.Entry<Character, Integer>> freq;

    public static void encode() throws IOException {
        totalLength = 0;

        ArithmeticalUtils.fillUp();
        ArithmeticalUtils.setIterator(inputPath);
        ArithmeticalUtils.doLineWithIterator(s -> {
            addValueToTotalLength(s.length());
            for(int i = 0; i < s.length(); i++)
                ArithmeticalUtils.fillCharFrequenciesMap(s.charAt(i));
        });
        freq = new ArrayList<>(ArithmeticalUtils.getCharFrequencies().entrySet());

        List<Map<Character, Pair<BigDecimal, BigDecimal>>> encoder = new ArrayList<>();
        final BigDecimal[] min = {BigDecimal.ZERO};
        final BigDecimal[] max = { BigDecimal.ONE };

        ArithmeticalUtils.setIterator(inputPath);
        ArithmeticalUtils.doLineWithIterator(s -> {
            for(int i = 0; i < s.length(); i++){
                Map<Character, Pair<BigDecimal, BigDecimal>> probs = stage(min[0], max[0]);
                min[0] = probs.get(s.charAt(i)).getKey();
                max[0] = probs.get(s.charAt(i)).getValue();

                encoder.add(probs);
            }
        });

        Map<Character, Pair<BigDecimal, BigDecimal>> probs = stage(min[0], max[0]);
        encoder.add(probs);

        FileUtils.writeStringToFile(codeOutputPath, getEncodedValue(encoder).toString(), "UTF-8");

        StringBuilder sb = new StringBuilder();
        sb.append(totalLength).append('\n');
        ArithmeticalUtils.getCharFrequencies().forEach((c, s) -> sb.append(c).append(s).append('\n'));
        FileUtils.writeStringToFile(cipherOutputPath, sb.toString(), "UTF-8");
        System.out.println("Encoded");
    }

    public static void decode() throws IOException {
        totalLength = 0;
        first = true;

        ArithmeticalUtils.fillUp();
        ArithmeticalUtils.setIterator(cipherOutputPath);
        ArithmeticalUtils.doLineWithIterator(s -> {
            if(first) {
                addValueToTotalLength(Integer.parseInt(s));
                first = false;
            } else {
                ArithmeticalUtils.getCharFrequencies().put(s.charAt(0), Integer.parseInt(s.substring(1)));
            }
        });
        freq = new ArrayList<>(ArithmeticalUtils.getCharFrequencies().entrySet());

        StringBuilder decodedMessage = new StringBuilder();
        ArithmeticalUtils.setIterator(codeOutputPath);
        ArithmeticalUtils.doLineWithIterator(decodedMessage::append);

        BigDecimal encodedValue = new BigDecimal(decodedMessage.toString());
        decodedMessage.delete(0, decodedMessage.length());

        List<Map<Character, Pair<BigDecimal, BigDecimal>>> decoder = new ArrayList<>();
        BigDecimal min = BigDecimal.ZERO;
        BigDecimal max = BigDecimal.ONE;

        for(int i = 0; i < totalLength; i++) {
            Map<Character, Pair<BigDecimal, BigDecimal>> probs = stage(min, max);

            Character find = null;
            for(Map.Entry<Character, Pair<BigDecimal, BigDecimal>> prob : probs.entrySet()) {
                if(encodedValue.compareTo(prob.getValue().getKey()) >= 0 && encodedValue.compareTo(prob.getValue().getValue()) <= 0) {
                    find = prob.getKey();
                    break;
                }
            }

            decodedMessage.append(find);
            min = probs.get(find).getKey();
            max = probs.get(find).getValue();

            decoder.add(probs);
        }

        Map<Character, Pair<BigDecimal, BigDecimal>> probs = stage(min, max);
        decoder.add(probs);

        FileUtils.writeStringToFile(outputPath, decodedMessage.toString(), "UTF-8");
        System.out.println("Decoded");
    }

    private static BigDecimal getEncodedValue(List<Map<Character, Pair<BigDecimal, BigDecimal>>> encoder){
        List<BigDecimal> lastStageValues = new ArrayList<>();
        for(Pair<BigDecimal, BigDecimal> d : encoder.get(encoder.size() - 1).values()) {
            lastStageValues.add(d.getKey());
            lastStageValues.add(d.getValue());
        }

        Optional<BigDecimal> lastStageMax = lastStageValues.stream().max(BigDecimal::compareTo);
        Optional<BigDecimal> lastStageMin = lastStageValues.stream().min(BigDecimal::compareTo);

        return (lastStageMax.get().add(lastStageMin.get())).divide(BigDecimal.valueOf(2), SCALE, RoundingMode.HALF_UP);
    }

    private static Map<Character, Pair<BigDecimal, BigDecimal>> stage(BigDecimal min, BigDecimal max){
        Map<Character, Pair<BigDecimal, BigDecimal>> probs = new LinkedHashMap<>();
        BigDecimal domain = max.subtract(min);

        for(int i = 0; i < freq.size(); i++) {
            Map.Entry<Character, Integer> term = freq.get(i);
            BigDecimal termProb = BigDecimal.valueOf(term.getValue()).divide(BigDecimal.valueOf(totalLength), 256, RoundingMode.HALF_UP);
            BigDecimal cumProb = termProb.multiply(domain).add(min);
            probs.put(term.getKey(), new Pair<>(min, cumProb));
            min = cumProb;
        }

        return probs;
    }

    private static void addValueToTotalLength(Integer value){
        totalLength += value;
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

                Arithmetical.encode();
            }
            else if(command == 1) {
                codeOutputPath = new File(args[1]);
                cipherOutputPath = new File(args[2]);
                outputPath = new File(args[3]);

                Arithmetical.decode();
            }
            else {
                System.out.println("Wrong Code");
            }
        } catch (IOException e) {
            System.out.println("Error with reading file. Please check path or/and file!");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Not enough parameters (minimum 4 path)");
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
