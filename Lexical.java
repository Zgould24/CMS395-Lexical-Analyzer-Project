import java.util.*;
import java.io.*;

public class Lexical {
	
	public static File f  = new File("src/Tokens");									//Reads from a file for token tables

	public static void main(String[] args) {										//Main function
		
		int size = 500;																//Maximum size of the tokens symbol table
		
		Dictionary<String, Integer> tokens = new Hashtable<String, Integer>();		//Creates a dictionary for tokens
		Hashtable<Integer, String> symbols = new Hashtable<Integer, String>(size);	//Creates a hashtable for symbols
		
		populateTokens(tokens);														//Reads tokens from file f and places them inside tokens table
		
		String example = "example";													//Creates an example symbol
		int key = hashFunction(example, size);										//Generates key from example symbol
		
		symbols.put(key, example);													//Uses the two metrics to place symbol inside symbol table
		
		System.out.println(symbols.get(hashFunction(example, size)));				//Test that ensures the hashfunction generates the same key for the same symbol and returns the symbol name
		System.out.println(tokens.get("program"));									//Tests that the token has been placed in the correct place in the tokens dictionary
	}
	
	public static void populateTokens(Dictionary d) {								//Function that will place tokens inside the passed dictionary
		
		try {																		//Try-catch ensures that the file is exists and is readable
			
			Scanner sc = new Scanner(f);											
			String[] tokens;
			
			while(sc.hasNextLine()) {												//Splits each line into substrings
				tokens = sc.nextLine().split(" ");
				
				d.put(tokens[0], tokens[1]);										//Places the token name and number into dictionary
			}
			
		}	catch(Exception e) {													//Catch handles an unreachable file
			e.printStackTrace();
		}
		
	}
	
	public static int hashFunction(String input, int size) {						//Hashfuntion that will generate a key for the input symbol
		
		char[] inputParse = input.toCharArray();									//Parses input symbol into char array
		int x = 0;
		
		for(int i = 0; i < inputParse.length; i++) {								//adds each char ASCII value into int x
			x += inputParse[i];
		}
		
		return x % size;															//Generates a key modulating x with size of symbol table
	}
	
}
