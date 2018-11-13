package lab1;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LexicalAnalyzer {
	private String inputFilePath = "src/lab1Input.txt";
	private String outputFilePath = "src/lab1output.txt";
	private StringBuilder buffer = new StringBuilder();
	private StringBuilder tokenBuilder = new StringBuilder();
	private List<Token> tokens = new LinkedList<>();
	private final String[] keywords = {"boolean", "byte", "char", "double", "false", "float", "int", "long", "new", "null", "short", "true", "void", "instanceof", "break", "case", "catch", "continue", "default", "do", "else", "for", "if", "return", "switch", "try", "while", "finally", "throw", "this", "super", "abstract", "final", "private", "protected", "public", "static", "synchronized", "transient", "volatile", "class", "extends", "implements", "interface", "package", "import", "throws", "enum", "native", "strictfp", "goto", "const", "assert"};
	private final char[] operatorChars = {'+', '-', '*', '/', '=', '>', '<', '&', '|', '^', '!', '~', '%'};
	private final String[] operatorStrings = {"++", "--", "&&", " ", "", " ", "==", ">=", "<=", "!=", "+=", "-=", "*=", "/=", "%="};
	private final char[] separators = {'.', ':', ',', ';', '{', '}', '(', ')', ']', '['};

	public LexicalAnalyzer() {
	}

	public LexicalAnalyzer(String inPath, String outPath) {
		this.inputFilePath = inPath;
		this.outputFilePath = outPath;
	}

	private boolean isKeyword(String str) {
		for (String s : keywords) {
			if (s.equals(str))
				return true;
		}
		return false;
	}

	private boolean isOperator(char ch) {
		for (char c : operatorChars) {
			if (c == ch)
				return true;
		}
		return false;
	}

	private boolean isOperator(String string) {
		for (String str : operatorStrings) {
			if (str.equals(string))
				return true;
		}
		return false;
	}

	private boolean isSeparator(char ch) {
		for (char c : separators) {
			if (c == ch)
				return true;
		}
		return false;
	}

	private boolean isValidIntStr(String intStr) {
		return !(intStr.length() >= 2 && intStr.startsWith("0"));
	}

	private boolean isWordChar(char ch) {
		return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_';
	}

	private boolean isWordStarter(char ch) {
		return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_';
	}

	private boolean isDigit(char ch) {
		return (ch >= '0' && ch <= '9');
	}

	private boolean isBlank(char ch) {
		return Character.isWhitespace(ch);
	}

	private void readFile() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
			String temp = reader.readLine();
			boolean isComment = false;
			while (temp != null) {
				if (temp.replace("\t", "").trim().startsWith("/*"))
					isComment = true;
				if (!isComment) {
					if (!temp.trim().startsWith("//"))
						buffer.append(temp).append('\n');
				} else {
					if (temp.replace("\t", "").trim().endsWith("*/"))
						isComment = false;
				}
				temp = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addEnd(String cate) {
		tokens.add(new Token(cate, tokenBuilder.toString()));
	}

	private void analyse() {
		if (buffer.length() == 0)
			System.out.println("empty input");
		else {
			int index = 0;
			char currentChar = buffer.charAt(0);
			int max = buffer.length();
			loop:
			do {
				if (isBlank(currentChar)) {
					while (isBlank(currentChar)) {
						index++;
						if (index >= max)
							break loop;
						currentChar = buffer.charAt(index);
					}
				}
				if (isWordStarter(currentChar)) {
					tokenBuilder.append(currentChar);
					index++;
					if (index >= max) {
						addEnd("identifier");
						break;
					}
					currentChar = buffer.charAt(index);
					while (isWordChar(currentChar)) {
						tokenBuilder.append(currentChar);
						index++;
						if (index >= max) {
							if (isKeyword(tokenBuilder.toString()))
								addEnd("keyword");
							else
								addEnd("identifier");
							break loop;
						}
						currentChar = buffer.charAt(index);
					}
					String temp = tokenBuilder.toString();
					if (isKeyword(temp)) {
						tokens.add(new Token("keyword", temp));
					} else {
						tokens.add(new Token("identifier", temp));
					}
					tokenBuilder.setLength(0);
				} else if (isDigit(currentChar)) {
					tokenBuilder.append(currentChar);
					index++;
					if (index >= max) {
						addEnd("integer");
						break;
					}
					currentChar = buffer.charAt(index);
					while (isDigit(currentChar)) {
						tokenBuilder.append(currentChar);
						index++;
						if (index >= max) {
							if (!isValidIntStr(tokenBuilder.toString()))
								addEnd("wrong token");
							break loop;
						}
						currentChar = buffer.charAt(index);
					}
					if (isValidIntStr(tokenBuilder.toString())) {
						if (currentChar == '.') {
							tokenBuilder.append(currentChar);
							index++;
							if (index >= max) {
								addEnd("wrong token");
								break;
							}
							currentChar = buffer.charAt(index);
							if (isDigit(currentChar)) {
								tokenBuilder.append(currentChar);
								index++;
								if (index >= max) {
									addEnd("float");
									break;
								}
								currentChar = buffer.charAt(index);
								while (isDigit(currentChar)) {
									tokenBuilder.append(currentChar);
									index++;
									if (index >= max) {
										addEnd("float");
										break loop;
									}
									currentChar = buffer.charAt(index);
								}
								tokens.add(new Token("float", tokenBuilder.toString()));
							} else {
								tokens.add(new Token("wrong token", tokenBuilder.toString()));
							}
						} else {
							tokens.add(new Token("integer", tokenBuilder.toString()));
						}
					} else {
						tokens.add(new Token("wrong token", tokenBuilder.toString()));
					}
					tokenBuilder.setLength(0);
				} else if (isOperator(currentChar)) {
					tokenBuilder.append(currentChar);
					index++;
					if (index >= max) {
						addEnd("operator");
						break;
					}
					currentChar = buffer.charAt(index);
					if (isOperator(currentChar)) {
						tokenBuilder.append(currentChar);
						if (isOperator(tokenBuilder.toString()))
							tokens.add(new Token("operator", tokenBuilder.toString()));
						else
							tokens.add(new Token("wrong token", tokenBuilder.toString()));
						tokenBuilder.setLength(0);
						index++;
						if (index >= max) {
							break;
						}
						currentChar = buffer.charAt(index);
					} else {
						tokens.add(new Token("operator", tokenBuilder.toString()));
						tokenBuilder.setLength(0);
					}
				} else if (isSeparator(currentChar)) {
					tokens.add(new Token("separator", String.valueOf(currentChar)));
					index++;
					if (index >= max) {
						break;
					}
					currentChar = buffer.charAt(index);
				} else if (currentChar == '\'') {
					tokenBuilder.append(currentChar);
					index++;
					if (index >= max) {
						addEnd("wrong token");
						break;
					}
					currentChar = buffer.charAt(index);
					tokenBuilder.append(currentChar);
					if (currentChar != '\'') {
						index++;
						if (index >= max) {
							addEnd("wrong token");
							break;
						}
						currentChar = buffer.charAt(index);
						if (currentChar == '\'') {
							tokenBuilder.append(currentChar);
							tokens.add(new Token("character", tokenBuilder.toString()));
							tokenBuilder.setLength(0);
							index++;
							if (index >= max) {
								break;
							}
							currentChar = buffer.charAt(index);
						} else {
							tokens.add(new Token("wrong token", tokenBuilder.toString()));
							tokenBuilder.setLength(0);
						}
					} else {
						tokens.add(new Token("wrong token", tokenBuilder.toString()));
						tokenBuilder.setLength(0);
						index++;
						if (index >= max) {
							break;
						}
						currentChar = buffer.charAt(index);
					}
				} else if (currentChar == '"') {
					tokenBuilder.append(currentChar);
					index++;
					if (index >= max) {
						addEnd("wrong token");
						break;
					}
					currentChar = buffer.charAt(index);
					while (currentChar != '"') {
						tokenBuilder.append(currentChar);
						index++;
						if (index >= max) {
							addEnd("wrong token");
							break loop;
						}
						currentChar = buffer.charAt(index);
					}
					tokenBuilder.append(currentChar);
					tokens.add(new Token("string", tokenBuilder.toString()));
					tokenBuilder.setLength(0);
					index++;
					if (index >= max) {
						break;
					}
					currentChar = buffer.charAt(index);
				}else{
					tokens.add(new Token("wrong token", String.valueOf(currentChar)));
					break ;
				}
			} while (index < buffer.length());
		}
	}

	private void writeResult() {
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath));
			for (Token t : tokens) {
				writer.println(t);
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void parse() {
		readFile();
		System.out.println("file loaded");
		analyse();
		writeResult();
		System.out.println("parsing completed");
		printErrors();
	}

	public List<Token> getResultList() {
		parse();
		return this.tokens;
	}

	public void printErrors() {
		List<String> wrongArr = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(outputFilePath));
			String temp = reader.readLine();
			while (temp != null) {
				if (temp.startsWith("wrong token")) {
					wrongArr.add(temp);
				}
				temp = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (wrongArr.size() > 0) {
			if (wrongArr.size() > 1)
				System.out.println(wrongArr.size() + " errors are found:");
			else
				System.out.println(1 + " error is found:");
			for (String str : wrongArr) {
				System.out.println(str);
			}
		}
	}

	public static void main(String[] args) {
		LexicalAnalyzer analyzer = new LexicalAnalyzer();
		analyzer.parse();
	}
}
