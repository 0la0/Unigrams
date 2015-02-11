#Unigrams
Create a sequence of unigrams based on a first order Markov Chain.

---
###Usage
Compile:
```Shell
javac Unigrams.java
```

Run:
```Shell
java Unigrams pathToInputFile numberOfOutputStrings
```
The process takes two command line arguments.  The first is a path to an input file from which to construct the Makov Chain.  The second argument is the number of words to print.

Example:
```Shell
java Unigrams files/textInput.txt 80
```

To output to a text file:
```Shell
java Unigrams files/textInput.txt 80 > files/output.txt
```

Here is ann example output that was built from an article about Turner!
>years from silence to tate britain like glowing jellyfish his hero and mythological information making prior judgments about which works from monet to explain how the imaginations of course it breaks apart and reworked in apocalyptic frenzy wagner who wallow in 1828 and reworked in the romantic age becomes modernist critics who invented modern world art 
