1、Basic functions: input character stream from source code file, output tokens and its category to text file.
2、RE: 1/keyword(53 totally): boolean|byte|char|double|false|float|int|long|new|null|short|true|void|instanceof|break|case|catch|continue|default|do|else|for|if|return|switch|try|while|finally|throw|this|super|abstract|final|private|protected|public|static|synchronized|transient|volatile|class|extends|implements|interface|package|import|throws|enum|native|strictfp|goto|const|assert
          2/identifier: [a-z_A-Z][a-zA-Z0-9_]*
          3/operator: [|+-*/=><&!~^%]|++|--|&&| || |==|>=|<=|!=|+=|-=|*=|/=|%=
          4/separator: \. |: |, |; |{ |} |( |) |] |[ |@
          5/constant( integer/string/character/float ):
                          [0-9][0-9]* | ".*" |'.' | [0-9][0-9] *\. [0-9][0-9]*
3、Tokens are divided into 8 categories: keyword、identifier、operator、separator、integer、float、character and string.
     And the error is called "wrong token" in the result file.
4、The FA chart is showed in the lexicalAnalyzerFA.png file, some meta characters in the FA chart:
     \d---[0-9]         \D---[^0-9]
     \w---[_a-zA-Z0-9]   \W---[^_a-zA-Z0-9]
     \s---blank character
     operator---single character operator
5、The input file is demo.txt, the output file will be result.txt.
     This lexical analyzer is used to parse java code , it can automatically ignore comments in the input file.
     After launch,  the status of the process can be observed on the console and errors found by the analyzer will be presented.
    WARNING: Do not change the two files' path and filename. You can only change their content.
6、The lexical analyzer is completed all by myself, I think it is a good work.