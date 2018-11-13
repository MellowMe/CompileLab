package lab2;

import lab1.LexicalAnalyzer;
import lab1.Token;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SyntaxAnalyzer {
	private boolean isInputCorrect;
	private List<Integer> inputs = new ArrayList<>();
	private int index;
	private Stack<Character> symbols = new Stack<>();
	private Stack<Integer> states = new Stack<>();
	private static Production[] p = new Production[11];
	private static char[] allChar = {'i', '.', '(', ')', ',', 'v', ';', 'S', 'A', 'C', 'B', 'D'};
	/*
	0~22rows represent 0~22 states,
	 0~11columns represent symbols i.(),v;SACBD by order
	in the action block, if value > 0 , Shift; value < 0 ,reduce; value = 0, accept
	value 100 represents errors
	*/
	private static int[][] table = new int[23][12];
	private PrintWriter writer;

	static class Production {
		char left;
		int len;

		Production(char c, int l) {
			left = c;
			len = l;
		}
	}

	/*
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
	(10)D->v*/

	static {
		p[1] = new Production('S', 7);
		p[2] = new Production('C', 6);
		p[3] = new Production('C', 0);
		p[4] = new Production('A', 2);
		p[5] = new Production('A', 2);
		p[6] = new Production('A', 0);
		p[7] = new Production('B', 2);
		p[8] = new Production('B', 0);
		p[9] = new Production('D', 1);
		p[10] = new Production('D', 1);
		for (int i = 0; i < 23; i++) {
			for (int j = 0; j < 12; j++) {
				table[i][j] = 100;
			}
		}
		table[0][0] = 2;
		table[0][7] = 1;
		table[1][6] = 0;
		table[2][1] = 3;
		table[3][0] = 4;
		table[4][2] = 5;
		table[5][0] = 9;
		table[5][3] = -6;
		table[5][5] = 10;
		table[5][7] = 7;
		table[5][8] = 6;
		table[5][11] = 8;
		table[6][3] = 11;
		table[7][3] = -8;
		table[7][4] = 13;
		table[7][10] = 12;
		table[8][3] = -8;
		table[8][4] = 13;
		table[8][10] = 14;
		table[9][1] = 3;
		table[9][3] = -9;
		table[9][4] = -9;
		table[10][3] = -10;
		table[10][4] = -10;
		table[11][1] = 16;
		table[11][3] = -3;
		table[11][4] = -3;
		table[11][6] = -3;
		table[11][9] = 15;
		table[12][3] = -4;
		table[13][0] = 9;
		table[13][3] = -6;
		table[13][5] = 10;
		table[13][7] = 7;
		table[13][8] = 17;
		table[13][11] = 8;
		table[14][3] = -5;
		table[15][3] = -1;
		table[15][4] = -1;
		table[15][6] = -1;
		table[16][0] = 18;
		table[17][3] = -7;
		table[18][2] = 19;
		table[19][0] = 9;
		table[19][3] = -6;
		table[19][5] = 10;
		table[19][7] = 7;
		table[19][8] = 20;
		table[19][11] = 8;
		table[20][3] = 21;
		table[21][1] = 16;
		table[21][3] = -3;
		table[21][4] = -3;
		table[21][6] = -3;
		table[21][9] = 22;
		table[22][3] = -2;
		table[22][4] = -2;
		table[22][6] = -2;
	}

	public void init(List<Token> t, String outputFile) {
		try {
			writer = new PrintWriter(new FileWriter(outputFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		index = 0;
		isInputCorrect = true;
		symbols.clear();
		states.clear();
		symbols.push(';');
		states.push(0);
		for (Token token : t) {
			switch (token.getCate()) {
				case "identifier":
					token.setCode(0);
					break;
				case "separator":
					switch (token.getToken()) {
						case ".":
							token.setCode(1);
							break;
						case "(":
							token.setCode(2);
							break;
						case ")":
							token.setCode(3);
							break;
						case ",":
							token.setCode(4);
							break;
						case ";":
							token.setCode(6);
							break;
					}
					break;
				case "integer":
				case "float":
				case "character":
				case "string":
					token.setCode(5);
					break;
				case "keyword":
					if (token.getToken().equals("true") || token.getToken().equals("false"))
						token.setCode(5);
					break;
			}
			if (token.getCode() >= 0 && token.getCode() <= 6)
				inputs.add(token.getCode());
			else {
				isInputCorrect = false;
				writer.println("wrong input: " + token.getToken());
				System.out.println("wrong input: " + token.getToken());
			}
		}
	}

	public void init(List<Token> t) {
		init(t, "src/lab2output.txt");
	}

	private void shift(int nextState) {
		states.push(nextState);
		symbols.push(allChar[inputs.get(index)]);
		index++;
		writer.println("AFTER SHIFT{states stack: " + states + " ,symbol stack: " + symbols + "}");
	}

	private void reduce(int pNumber) {
		int len = p[pNumber].len;
		char nonTerminal = p[pNumber].left;
		int nonT = String.valueOf(allChar).indexOf(nonTerminal);
		for (int i = 0; i < len; i++) {
			states.pop();
			symbols.pop();
		}
		int newState = table[states.peek()][nonT];
		states.push(newState);
		symbols.push(nonTerminal);
		writer.println("AFTER REDUCE{states stack: " + states + " ,symbol stack: " + symbols + "}");
	}

	public void parse() {
		if (isInputCorrect) {
			writer.println("Analysis is performed as follows:");
			int action = table[states.peek()][inputs.get(index)];
			while (action != 0 && action != 100) {
				if (action > 0)
					shift(action);
				else
					reduce(-1 * action);
				action = table[states.peek()][inputs.get(index)];
			}
			if (action == 0) {
				System.out.println("Accepted");
				writer.println("Accepted");
			} else {
				writer.println("An error occurs at " + allChar[inputs.get(index)]);
				System.out.println("An error occurs at " + allChar[inputs.get(index)]);
			}
		}
		writer.flush();
		writer.close();
	}

	public static void main(String[] args) {
		LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer("src/lab2Input.txt", "src/lab2Tokens.txt");
		SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer();
		syntaxAnalyzer.init(lexicalAnalyzer.getResultList());
		syntaxAnalyzer.parse();
	}
}
