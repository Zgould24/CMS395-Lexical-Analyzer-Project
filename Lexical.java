import java.util.*;
import java.io.*;

public class Lexical {
	
	private static final int SIZE = 500;
	
	private static File t  = new File("src/Tokens");									//Reads from a file for token tables
	private static File p = new File("src/PL_0 Example");
	
	private static Hashtable<Integer, String> symbols = new Hashtable<Integer, String>(SIZE);	//Creates a hashtable for symbols
	private static Dictionary<String, Integer> tokens = new Hashtable<String, Integer>();		//Creates a dictionary for tokens
	
	
	public static void main(String[] args) {										//Main function
		
		populateTokens(tokens);														//Reads tokens from file f and places them inside tokens table
		
		analyze();
		
//		String example = "example";													//Creates an example symbol
//		int key = hashFunction(example, size);										//Generates key from example symbol
//		
//		symbols.put(key, example);													//Uses the two metrics to place symbol inside symbol table
		
//		System.out.println(symbols.get(hashFunction(example, size)));				//Test that ensures the hashfunction generates the same key for the same symbol and returns the symbol name
//		System.out.println(tokens.get("<="));										//Tests that the token has been placed in the correct place in the tokens dictionary
		
		
		
//		Object test = 3;															//Compares strings and integer types
//		Object userInput = test.getClass();
//		
//		if(test instanceof Integer) {
//			System.out.println(true);
//		}  else {
//			System.out.println(false);
//		}
	}
	
	public static void analyze() {
		
		try {
			
			Scanner pl_0 = new Scanner(p);
			
			
			//Section of code checks for any consts or vars before block begins
			while(pl_0.hasNextLine()) {
				
				String[] parsed = pl_0.nextLine().split(" ");
				
				switch(parsed[0]) {
				
					case "const":
						constCheck(parsed);
						break;
				
					case "var":
						varCheck();
						break;
					
					case "procedure":
						procCheck();
						break;
					
					
				
				}
				
			}
			
			
			
		}	catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void constCheck(String[] p) {
		
		int i = 1;
		
		while(true) {
			
			int identKey = hashFunction(p[i]);
			symbols.put(identKey, p[i]);
			
			if(!p[i].matches("[a-zA-Z$]*")) {
				System.out.println("ERROR: Identifier contains illegal characters.");
			}
			
			if(!p[i+1].equals("=")) {
				System.out.println("ERROR: Assignment symbol not found for identifier.");
			}
			
			try {
				Integer.parseInt(p[i+2]);
			}	catch(Exception e) {
				System.out.println("ERROR: Invalid data type for identifier");
			}
			
			if(p[i+3].equals(";")) {
				break;
			}  else if(!p[i+3].equals(",")) {
				System.out.println("ERROR: Comma delimiter not present.");
			}
			
			i += 4;
			
		}
		
		printTokenOutput(p);
		
	}
	
	public static void varCheck() {
		
	}

	public static void procCheck() {
		
	}
	
	public static void printTokenOutput(String[] s) {
		
		StringBuilder sb = new StringBuilder();
		int i = 0;
		
		while(i < s.length) {
			
			if(tokens.get(s[i]) != null) {
				sb.append(tokens.get(s[i]));
			}  else if(symbols.get(hashFunction(s[i])) == s[i]) {
				sb.append("0 " + hashFunction(s[i]));
			}  else if(s[i].matches("[0-9]*")) {
				sb.append("1 " + s[i]);
			}
			
			sb.append(" * ");
			
			i++;
		}
		
		System.out.println(sb);
		
	}
	
	public static void populateTokens(Dictionary d) {								//Function that will place tokens inside the passed dictionary
		
		try {																		//Try-catch ensures that the file is exists and is readable
			
			Scanner sc = new Scanner(t);											
			String[] tokens;
			
			while(sc.hasNextLine()) {												//Splits each line into substrings
				tokens = sc.nextLine().split(" ");
				
				d.put(tokens[0], tokens[1]);										//Places the token name and key into dictionary
			}
			
		}	catch(Exception e) {													//Catch handles an unreachable file
			e.printStackTrace();
		}
		
	}
	
	public static int hashFunction(String input) {						//Hashfuntion that will generate a key for the input symbol
		
		char[] inputParse = input.toCharArray();									//Parses input symbol into char array
		int x = 0;
		
		for(int i = 0; i < inputParse.length; i++) {								//adds each char ASCII value into int x
			x += inputParse[i];
		}
		
		return x % SIZE;															//Generates a key modulating x with size of symbol table
	}
	
}
