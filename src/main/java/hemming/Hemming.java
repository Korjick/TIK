package hemming;

import arithmetic.Arithmetical;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Function;

public class Hemming {

    private static File inputPath, codeOutputPath, cipherOutputPath, outputPath;
    private static LineIterator it;

    public static void decode() throws IOException {
        int len = 0;
        it = FileUtils.lineIterator(cipherOutputPath, "UTF-8");
        try {
            while (it.hasNext())
                len = Integer.parseInt(it.nextLine());
        } finally {
            LineIterator.closeQuietly(it);
        }

        StringBuilder decoded = new StringBuilder();
        StringBuilder res = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        boolean p1m, p2m, p3m;
        it = FileUtils.lineIterator(codeOutputPath, "UTF-8");
        try {
            while (it.hasNext()) {
                sb.append(it.nextLine());
                while (sb.length() >= 7) {
                    StringBuilder tmp = new StringBuilder(sb.substring(0, 7));
                    int p1 = (Integer.parseInt(String.valueOf(tmp.charAt(2)))
                            + Integer.parseInt(String.valueOf(tmp.charAt(4)))
                            + Integer.parseInt(String.valueOf(tmp.charAt(6)))) % 2;
                    int p2 = (Integer.parseInt(String.valueOf(tmp.charAt(2)))
                            + Integer.parseInt(String.valueOf(tmp.charAt(5)))
                            + Integer.parseInt(String.valueOf(tmp.charAt(6)))) % 2;
                    int p3 = (Integer.parseInt(String.valueOf(tmp.charAt(4)))
                            + Integer.parseInt(String.valueOf(tmp.charAt(5)))
                            + Integer.parseInt(String.valueOf(tmp.charAt(6)))) % 2;

                    int pos = -1;
                    if(tmp.charAt(0) != String.valueOf(p1).charAt(0))
                        pos += 1;
                    if(tmp.charAt(1) != String.valueOf(p2).charAt(0))
                        pos += 2;
                    if(tmp.charAt(3) != String.valueOf(p3).charAt(0))
                        pos += 4;
                    if(pos != -1) tmp.setCharAt(pos, tmp.charAt(pos) == '0' ? '1' : '0');

                    res.append(tmp.deleteCharAt(0).deleteCharAt(0).deleteCharAt(1));
                    sb.delete(0, 7);

                    if(res.length() >= len) {
                        decoded.append((char) Integer.parseInt(res.substring(0, len), 2));
                        res.delete(0, len);
                    }
                }
            }
        } finally {
            LineIterator.closeQuietly(it);
        }

        FileUtils.writeStringToFile(outputPath, decoded.toString(), "UTF-8");
        System.out.println("Decoded");
    }

    public static void encode() throws IOException {
        Set<Character> symb = new HashSet<>();
        it = FileUtils.lineIterator(inputPath, "UTF-8");
        try {
            String line;
            while (it.hasNext()) {
                line = it.nextLine();
                for(int i = 0; i < line.length(); i++)
                    symb.add(line.charAt(i));
            }
        } finally {
            LineIterator.closeQuietly(it);
        }

        int maxLen = symb.stream().map(character -> Integer.toBinaryString((int) character).length()).max(Integer::compareTo).get();
        while (maxLen % 4 != 0) maxLen++;
        cleanBefore(cipherOutputPath);
        FileUtils.writeStringToFile(cipherOutputPath, maxLen + "", "UTF-8");

        StringBuilder sb = new StringBuilder();
        cleanBefore(codeOutputPath);
        try {
            it = FileUtils.lineIterator(inputPath, "UTF-8");
            String line;
            while (it.hasNext()) {
                line = it.nextLine();
                for (int i = 0; i < line.length(); i++) {
                    String test = toBinary(line.charAt(i), maxLen);
                    sb.append(test);
                    while (sb.length() >= 4) {
                        String tmp = sb.substring(0, 4);
                        int p1 = (Integer.parseInt(String.valueOf(tmp.charAt(0)))
                                + Integer.parseInt(String.valueOf(tmp.charAt(1)))
                                + Integer.parseInt(String.valueOf(tmp.charAt(3)))) % 2;
                        int p2 = (Integer.parseInt(String.valueOf(tmp.charAt(0)))
                                + Integer.parseInt(String.valueOf(tmp.charAt(2)))
                                + Integer.parseInt(String.valueOf(tmp.charAt(3)))) % 2;
                        int p3 = (Integer.parseInt(String.valueOf(tmp.charAt(1)))
                                + Integer.parseInt(String.valueOf(tmp.charAt(2)))
                                + Integer.parseInt(String.valueOf(tmp.charAt(3)))) % 2;
                        tmp = "" + p1 + p2 + tmp.charAt(0) + p3 + tmp.substring(1);
                        Files.write(codeOutputPath.toPath(), tmp.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
                        sb.delete(0, 4);
                    }
                }
            }
        } finally {
            LineIterator.closeQuietly(it);
        }

        System.out.println("Encoded");
    }

    private static void cleanBefore(File file) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(file);
        writer.print("");
        writer.close();
    }

    private static String toBinary(int x, int len)
    {
        if (len > 0)
            return String.format("%" + len + "s",
                    Integer.toBinaryString(x)).replaceAll(" ", "0");

        return null;
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

                Hemming.encode();
            }
            else if(command == 1) {
                codeOutputPath = new File(args[1]);
                cipherOutputPath = new File(args[2]);
                outputPath = new File(args[3]);

                Hemming.decode();
            }
            else {
                System.out.println("Wrong Code");
            }
        } catch (IOException e) {
            System.out.println("Error with reading file. Please check path or/and file!");
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Not enough parameters (minimum 4 path)");
        } catch(Exception e) {
            System.out.println(e);
        }
    }
}
