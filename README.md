# Theory of Information Coding 11-902 Fakhrutdinov Bulat
## Read carefully before running programs
### First start

The entire project was made personally by the author and is guaranteed not to contain fragments of someone else's code

[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://github.com/Korjick)

For convenience, all JAR files are moved to the **out/** folder and run using run.bat in the appropriate folder.
The text will need to be entered in the input.txt file, which is also located in the **out/** folder

*Optional:* All programs are started with a console command: java -jar .\FileName.jar []

**Important! In place of [] you must specify 4 paths:**

- {inputPath} - file where the text will be taken from
- {codeOutputPath} - file where the code will be written
- {cipherOutputPath} - file where the necessary meta-information will be written
- {outputPath} - file where the decryption will be written

**If at least 1 path is not specified, the program will not start. UTF-8 encoding**

## Huffman

> Greedy algorithm for optimal prefix encoding of the alphabet with minimal redundancy.

When creating this algorithm, the auxiliary class HuffmanUtils was used to store:

- {charFrequencies} - character frequency
- {huffmanCodes} - for storing received codes

And also 2 classes - **Node** and **Leaf**, which represent the intermediate sum and ends of the tree, respectively.
The structure of the algorithm is divided into 2 parts - **encoding** and **decoding**

### Encoding

Two pass algorithm. First collects the required symbol rate. Based on it, a priority queue is built,
which arranges the frequencies in ascending order. Further, until the only node remains in the queue, all nodes start
collect in common blocks. When one value remains, DFS starts, adding the value - **0** to all left branches,
and right - **1**. Codes, along with symbols, are written to the output file.

### Decoding

The algorithm reads the character and its code, as well as the encrypted string. Next, decryption is performed by the value key

## Arithmetical

>A lossless information compression algorithm that, when encoding, matches the text with a real number from the segment [0;1).

Based on the Huffman algorithm, an auxiliary class ArithmeticalUtils was created in the implementation,
where the necessary data about the frequency of occurrence of a character is stored,

### Encoding

**Attention! The algorithm works with large numbers, which automatically limits the length of the translated string
from the computer hardware. Encoding was tested on a string of 300 characters long and gave the result: **
- e - ~1.5 sec
- d - ~20 sec

**There is a SCALE parameter that allows you to control the number of decimal places - respectively,
improve accuracy**

The program receives text as input, then calculates the frequency of occurrence of each character,
writing data to Map. Then there is a calculation by blocks: an array of the type *symbol - [segment]* is created
where at each iteration the segment decreases according to the formula. Before writing to a file, the program
applies the split 1 more time.

### Decoding

Occurs according to the Encoding principle. The program reads the frequency of occurrence of characters, as well as the length of the encrypted word
and the word itself. After that, it gradually begins to decipher the text, by beating into blocks


## BWT

> BWT - (Burrows-Wheeler transform, BWT, also historically called block sort compression, although it is not a compression) is an algorithm used in data compression techniques to transform the original data. BWT is used in the bzip2 archiver. The algorithm was invented by Michael Burrows and David Wheeler.

### Encoding

The program does not use additional classes, in fact it only reads the text, creating an array
long number of characters of this text, and shifting each line. The array is then sorted into the output array,
using the 'Stack-of-Books' algorithm, writes
the last characters of each line, as well as meta information in the form of the position of the original text in the sorted array

### Decoding

The program receives the encrypted string and meta information. Deciphers the 'Pile-Books' sequence, then adds element by element
into an array of long strings from right to left and sorts at each stage. At the end of the pass, a line is obtained for the received position.

## Hemming

> Self-monitoring and self-correcting code. Built with reference to the binary number system. Allows you to correct a single error (an error in one bit of a word) and find a double one. Named after the American mathematician Richard Hamming who proposed the code.

### Encoding

The algorithm is a simple implementation of Hamming (7, 4). First, the symbol with the maximum binary code is calculated,
after which all characters are reduced to the maximum length + a multiple of four. On the basis of 4 blocks of binary codes are calculated:

*p1 = (d1 + d2 + d4) % 2*

*p2 = (d1 + d3 + d4) % 2*

*p3 = (d2 + d3 + d4) % 2*

The result is written as: *p1 | p2 | d1 | p3 | d2 | d3 | d4*

### Decoding

![Build Status](https://i.imgur.com/H49iORT.png)

Based on the table, we get the influence of the control bits. Calculating in the received
message control bits and finding an error, we can easily invert the wrong bit,
defining its position as:

*pos = -1 + (p1 == error ? 1 : 0) + (p2 == error ? 2 : 0) + (p1 == error ? 4 : 0)*
