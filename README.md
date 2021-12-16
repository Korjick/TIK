# TIK 11-902 Fakhrutdinov Bulat
## Внимательно ознакомится перед запуском программ
### Первый запуск

Весь проект был сделан лично автором и гарантированно не содержит фрагменты чужого кода

[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://github.com/Korjick)

Для удобства все JAR файлы вынесены в папку **out/** и запускаются с использованием run.bat в соответствующей папке.
Текст необходимо будет вводить в input.txt файл, который также находится в папке **out/**

*При необходимости:* Все программы запускаются консольной командой: java -jar .\FileName.jar []

**Важно! На месте [] необходимо указать 4 пути:**

- {inputPath} - файл, откуда будет браться текст
- {codeOutputPath} - файл, куда будет записываться код
- {cipherOutputPath} - файл, куда будет записываться необходимая мета-информация
- {outputPath} - файл, куда будет записываться расшифровка

**Если хотя бы 1 путь не будет указан, программа не начнет свою работу. Кодировка UTF-8**

## Huffman

> Жадный алгоритм оптимального префиксного кодирования алфавита с минимальной избыточностью.

При создании этого алгоритма был использован вспомогательный класс HuffmanUtils, для хранения:

- {charFrequencies} - частоты появления символов
- {huffmanCodes} - для хранения полученных кодов

А также 2 класса - **Node** и **Leaf**, представляющих собой соответсвенно промежуточную сумму и концы дерева.
По структуре алгоритм разбит на 2 части - **encoding** и **decoding**

### Encoding

Двухпроходной алгоритм. Сначала собирает необходимую частоту символов. На основе нее строится Приоритетная очередь,
которая расставляет частоты в порядке возрастания. Далее, пока в очереди не останется единственная нода, все ноды начинают
собираться в общие блоки. Когда останется одно значение - запускается DFS, добавляя всем левым веткам значение - **0**,
а правым - **1**. Коды, вместе с символами записываются в выходной файл.

### Decoding

Алгоритм считывает символ и его код, а также зашифрованную строку. Далее, производится расшифровка по ключу значения

## Arithmetical

>Алгоритм сжатия информации без потерь, который при кодировании ставит в соответствие тексту вещественное число из отрезка [0;1).

Опираясь на алгоритм Хаффмана, в реализации был создан вспомогательный класс ArithmeticalUtils,
где хранятся необходимые данные о частоте появлении символа, 

### Encoding

**Внимание! Алгоритм работает с большими числами, что автоматически ограничевает длину переводимой строки
от аппаратной части компьютера. Кодирование тестировалось на строке длинной в 300 символов и дало результат:**
- e - ~1.5 сек
- d - ~20 сек

**Присутствует параметр SCALE, который позволяет контролировать число знаков после запятой - соответственно,
повышать точность**

Программа получает на вход текст, после высчитывает частоту появление каждого символа, 
записывая данные в Map. После чего идет просчет по блокам: создается массив типа *символ - [отрезок]*
где на каждой итерации происходит уменьшение отрезка по формуле. Перед записью в файл, программа
применяет разделение еще 1 раз.

### Decoding

Происходит по Encoding принципу. Программа считывает частоту появления символов, а также длину зашифрованного слова
и само слово. После чего поэтапно начинает расшифровку текста, путем биения на блоки


## BWT

> BWT - (Burrows-Wheeler transform, BWT, также исторически называется блочно-сортирующим сжатием, хотя сжатием и не является) — это алгоритм, используемый в техниках сжатия данных для преобразования исходных данных. BWT используется в архиваторе bzip2. Алгоритм был изобретён Майклом Барроузом и Дэвидом Уилером.

### Encoding

Программа не использует дополнительных классов, по факту лишь считывая текст, создавая массив
длинною кол-ву символов этого текста, и смещая каждую строку. После чего массив сортируется и в выходной массив, 
используя алгоритм 'Стопка-Книг', записывается
последние символы каждой строки, а также мета-информация в виде позиции оригинального текста в отсортированном массиве

### Decoding

Программа получает зашифрованную строку и мета-информацию. Расшифровывает последовательность 'Стопки-Книг', после чего поэлементно добавляет
в массив длинны строки справа-налево и сортирует на каждом этапе. В конце прохода достается строка по полученной позиции.

## Hemming

> Самоконтролирующийся и самокорректирующийся код. Построен применительно к двоичной системе счисления. Позволяет исправлять одиночную ошибку (ошибка в одном бите слова) и находить двойную. Назван в честь американского математика Ричарда Хэмминга, предложившего код.

### Encoding

Алгоритм представляет собой простую реализацию Хэмминга (7, 4). Сначала вычислается символ с максимальным двоичным кодом,
после чего все символы приводятся к максимальной длинне + кратной четырем. На основе 4 блоков двоичного коды вычисляются:

*p1 = (d1 + d2 + d4) % 2*

*p2 = (d1 + d3 + d4) % 2*

*p3 = (d2 + d3 + d4) % 2*

Результат записывается в виде: *p1 | p2 | d1 | p3 | d2 | d3 | d4*

### Decoding

![Build Status](https://i.imgur.com/H49iORT.png)

Исходя из таблицы, мы получаем влияние контрольных битов. Высчитывая в полученном
сообщении контрольные биты и найдя ошибку, мы можем с легкостью инвертировать неправильный бит,
определив его позицию как: 

*pos = -1 + (p1 == error ? 1 : 0) + (p2 == error ? 2 : 0) + (p1 == error ? 4 : 0)*
