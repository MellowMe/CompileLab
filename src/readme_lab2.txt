Lab2致力于对Java中对象调用方法的语句进行语法分析，如这样的语句：
req.getRequestDispatcher(path.toString()).forward(req, resp);
variable.function(“zero”,0);
variable.method(str.trim().replace(‘a’,”mark”).concat(3.14).equals(string));

特别说明：
如你所见，这个语法分析器是比较有技术含量的^^，包含了23个状态，11个symbol
lab2的语法分析器建立在lab1的词法分析器的基础上，无法单独运行，需要Java环境
为与实际代码相符，该语句以分号;作为右边界符而不是$符号，


定义文法如下(不包含;)：
(0)S'->S
(1)S->i.i(A)C
(2)C->.i(A)C
(3)C->ε
(4)A->SB
(5)A->DB
(6)A->ε
(7)B->,A
(8)B->ε
(9)D->i
(10)D->v
说明：i为identifier，v为value：int(非负)、double、字符串、char、true、false
为使语法分析简明，将以上具体值都用v这个非终结符表示。
本语法分析器使用LALR(1)分析法，其中只标明了可规约式的LOOK AHEAD:

以下为构造出的LALR(1)文法DFA的式子:
0:S'->·S
   S->·i.i(A)C
0-S-1:S'->S·   FOLLOW:;
0-i-2:S->i·.i(A)C
2-.-3:S->i.·i(A)C
3-i-4:S->i.i·(A)C
4-(-5:S->i.i(·A)C
         A->·SB
         A->·DB
         A->·   FOLLOW:)
         S->·i.i(A)C
         D->·i
         D->·v
5-A-6:S->i.i(A·)C
5-S-7:A->S·B
          B->·,A
          B->·  FOLLOW:)
5-D-8:A->D·B
          B->·,A
          B->·     FOLLOW:)
5-i-9:S->i·.i(A)C
         D->i·     FOLLOW:)|,
5-v-10:D->v·       FOLLOW:)|,
6-)-11:S->i.i(A)·C
           C->·.i(A)C
           C->·         FOLLOW:)|,|;
7-B-12:A->SB·       FOLLOW:)
7-,-13:B->,·A
           A->·SB
           A->·DB
           A->·             FOLLOW:)
           S->·i.i(A)C
           D->·i
           D->·v
8-B-14:A->DB·       FOLLOW:)
8-,-13
9-.-3
11-C-15:S->i.i(A)C·      FOLLOW:)|,|;
11-.-16:C->.·i(A)C
13-A-17:B->,A·          FOLLOW:)
13-S-7
13-D-8
13-i-9
13-v-10
16-i-18:C->.i·(A)C
18-(-19:C->.i(·A)C
              A->·SB
              A->·DB
              A->·              FOLLOW:)
              S->·i.i(A)C
              D->·i
              D->·v
19-A-20:C->.i(A·)C
19-S-7
19-D-8
19-i-9
19-v-10
20-)-21:C->.i(A)·C
             C->·.i(A)C
             C->·           FOLLOW:)|,|;
21-C-22:C->.i(A)C·          FOLLOW:)|,|;
21-.-16