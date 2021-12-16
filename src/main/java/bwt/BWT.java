package bwt;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class BWT {

    private static File inputPath, codeOutputPath, cipherOutputPath, outputPath;
    private static LineIterator it, sec;

    public static void decode() throws IOException {
        cleanBefore(outputPath);
        StringBuilder sb = new StringBuilder();
        int size;

        sec = FileUtils.lineIterator(cipherOutputPath, "UTF-8");
        it = FileUtils.lineIterator(codeOutputPath, "UTF-8");

        try {
            boolean nextLine = true;
            String line;
            int charListSize = 0;
            while (it.hasNext()) {
                line = it.nextLine();
                if (nextLine) {
                    String[] data = line.trim().split(" ", 2);
                    size = Integer.parseInt(data[0]);
                    for (int i = 0; i < data[1].length(); i++) sb.append(data[1].charAt(i));

                    if (sb.length() < size) {
                        nextLine = false;
                    }
                    else {
                        solveDecode(sb.toString());
                        sb.delete(0, sb.length());
                    }
                } else {
                    for (int i = 0; i < line.length(); i++) sb.append(line.charAt(i));
                    if (sb.length() >= charListSize) {
                        solveDecode(sb.toString());
                        nextLine = true;
                        sb.delete(0, sb.length());
                    }
                }
            }
        } finally {
            LineIterator.closeQuietly(sec);
            LineIterator.closeQuietly(it);
        }

        System.out.println("Decoded");
    }

    public static void encode() throws IOException {
        it = FileUtils.lineIterator(inputPath, "UTF-8");
        cleanBefore(codeOutputPath);
        cleanBefore(cipherOutputPath);
        try {
            String line;
            while (it.hasNext()) {
                line = it.nextLine();
                solveEncode(line);
            }
        } finally {
            LineIterator.closeQuietly(it);
        }

        System.out.println("Encoded");
    }

    private static void solveDecode(String word) throws IOException {
        List<String> list = new ArrayList<>(word.length());

        if (sec.hasNext()) {
            int pos = Integer.parseInt(sec.nextLine());
            for (int i = 0; i < word.length(); i++) {
                for (int j = 0; j < word.length(); j++) {
                    if (j >= list.size())
                        list.add(String.valueOf(word.charAt(j)));
                    else
                        list.set(j, word.charAt(j) + list.get(j));
                }
                Collections.sort(list);
            }
            Files.write(outputPath.toPath(), (list.get(pos) + '\n').getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        }
    }

    private static void solveEncode(String text) throws IOException {
        StringBuilder sb = new StringBuilder();
        List<String> shift = new ArrayList<>(text.length());
        shift.add(text);
        for (int i = 1; i < text.length(); i++) {
            sb.append(text);
            String tmp = sb.substring(0, i);
            shift.add(sb.append(tmp).substring(i));
            sb.delete(0, sb.length());
        }
        Collections.sort(shift);
        int pos = shift.indexOf(text);

        final String[] word = {""};
        for (String s : shift) {
            word[0] += s.substring(s.length() - 1);
        }

        sb.delete(0, sb.length());
        sb.append(word[0].length()).append(" ").append(word[0]).append('\n');
        Files.write(codeOutputPath.toPath(), sb.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);

        sb.delete(0, sb.length());
        sb.append(pos).append('\n');
        Files.write(cipherOutputPath.toPath(), sb.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
    }

    private static void cleanBefore(File file) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(file);
        writer.print("");
        writer.close();
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        try {
            if (args.length < 4) throw new ArrayIndexOutOfBoundsException();
            System.out.println("Please, enter command, where 0 - encode, 1 - decode");
            int command = Integer.parseInt(in.nextLine());
            if (command == 0) {
                inputPath = new File(args[0]);
                codeOutputPath = new File(args[1]);
                cipherOutputPath = new File(args[2]);

                BWT.encode();
            } else if (command == 1) {
                codeOutputPath = new File(args[1]);
                cipherOutputPath = new File(args[2]);
                outputPath = new File(args[3]);

                BWT.decode();
            } else {
                System.out.println("Wrong Code");
            }
        } catch (IOException e) {
            System.out.println("Error with reading file. Please check path or/and file!");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Not enough parameters (minimum 4 path)");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
