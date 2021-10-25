import java.util.*;
import java.io.*;

//TO-DOs:

//Need to make sure that when you put an ident in the symbol table that you check if it already exists
// or not.

//Make sure that EOL and EOF are included in the print out of tokens

public class Lexical {
	
	private static final int SIZE = 500;
	
	private static File t  = new File("src/Keywords");											//Reads from a file for token tables
	private static File p = new File("src/PL_0 Example");
	
	private static Hashtable<Integer, String> symbols = new Hashtable<Integer, String>(SIZE);	//Creates a hashtable for symbols
	private static Dictionary<String, Integer> keywords = new Hashtable<String, Integer>();		//Creates a dictionary for keywords of the PL/0 language
	
	
	public static void main(String[] args) {													//Main function
		
		populateKeywords(keywords);
		
		analyze();																				
		
		printSymbolsTable();
		
	}
	
	public static void analyze() {																//Function that takes in lines of PL/0 code and translates it into tokens
		
		try {
		
			StringBuilder sb;																	//Building the string for the token output i.e. "0 108 * 31 * 54" etc.
			Scanner pl_0 = new Scanner(p);														//Reads from PL/0 example code file
			String line;
			String[] parsed;
			int lineNum = 1, searchCount, searchEnd;
			
			System.out.println("OUTPUT:");
		
			while(pl_0.hasNextLine()) {															//Goes through line by line of the PL/0 example code file
				
				sb = new StringBuilder();
				line = pl_0.nextLine();															//Stores the current line of the file into a data member
				parsed = line.split(" ");														//Parses current line into individual strings
				
				
				for(int i = 0; i < parsed.length; i++) {										//Goes through each string of the parsed line and determines the output of the token output
					
					if(keywords.get(parsed[i]) != null) {																				//Condition checks if currently looked at string is
					sb.append(keywords.get(parsed[i]));																					// a token and will print "0 * [Token#]]"
					}  else if(parsed[i].matches("[0-9]*")) {
						sb.append("1 " + parsed[i]);
					}  else if(parsed[i].length() > 30) {																							//If not a token, check if "identifier" meets size requirements
						System.out.println("ERROR: Identifier does not meet character/size requirements or does not contain a constant");			// returns -1 if not
						sb.append("-1");
					}  else if(!parsed[i].matches("[a-zA-Z0-9$]*")) {
						
						String originalIdent = parsed[i];
						
						parsed[i] = parsed[i].replaceAll("[^a-zA-Z0-9$]", "");
						
						System.out.println("ERROR: Invalid char(s) found in identifier. " + originalIdent + " will now be named " + parsed[i] + ".");
						
						if(symbols.get(hashFunction(parsed[i])) == null) {																	//Checks if space in symbol table is empty and
							symbols.put(hashFunction(parsed[i]), parsed[i]);																// "identifier" meets char criteria
							sb.append("0 " + hashFunction(parsed[i]));
						}  else if(symbols.get(hashFunction(parsed[i])) != null) {															//Checks if spot is taken at index of symbol table and current string does not equal
							if(symbols.get(hashFunction(parsed[i])).equals(parsed[i])) {													// the string/symbol that is currently in the symbol table at that index
								if(parsed[0].equals("const") || parsed[0].equals("var")) {													//Checks if currently line is for assignments of "identifiers"								
									System.out.println("ERROR: An identifier by the name of " + parsed[i] + " already exists.");
									sb.append("0 -1");																						//Will print -1 if "identifier" being assigned already exists
								}  else {																									//Prints out token if it is not an assignment line
									sb.append("0 " + hashFunction(parsed[i]));
								}
							}  else if(!symbols.get(hashFunction(parsed[i])).equals(parsed[i])) {											//Checks if assignment of "identifier" that does not equal the current one at that
								if(parsed[0].equals("const") || parsed[0].equals("var")) {													// symbol table index will collide
									System.out.println("ERROR: Symbol " + symbols.get(hashFunction(parsed[i])) 
									+ " already exists at index " 
									+ hashFunction(parsed[i]) 
									+ ". Processing collision check now.\n");
									
									String collisionToken = collisionCheck(parsed[i], hashFunction(parsed[i]));								//Checks for collision and linearly assigns symbol to the next available index in the
									sb.append("0 " + collisionToken);																		// symbol table. Will put -1 in token output if collision avoidance was unsuccessful
								}  else {																									//If it is not an assignment line, condition will linearly search for symbol since it
									searchCount = hashFunction(parsed[i]) + 1;																// will not be in the right place as it is hashed
									searchEnd = searchCount + 6;
									
									while(searchCount != searchEnd) {																		//Goes through the next 5 symbol table indexes to find the right symbol if symbol had
										if(symbols.get(searchCount) != null && symbols.get(searchCount).equals(parsed[i])) {				// to be assigned linearly to avoid collision
											sb.append("0 " + searchCount);
										}
										
										searchCount++;
									}
								}
							}
						}
					
					}  else if(symbols.get(hashFunction(parsed[i])) == null) {															//Checks if space in symbol table is empty and
						symbols.put(hashFunction(parsed[i]), parsed[i]);																// "identifier" meets char criteria
						sb.append("0 " + hashFunction(parsed[i]));
					}  else if(symbols.get(hashFunction(parsed[i])) != null) {															//Checks if spot is taken at index of symbol table and current string does not equal
						if(symbols.get(hashFunction(parsed[i])).equals(parsed[i])) {													// the string/symbol that is currently in the symbol table at that index
							if(parsed[0].equals("const") || parsed[0].equals("var")) {													//Checks if currently line is for assignments of "identifiers"								
								System.out.println("ERROR: An identifier by the name of " + parsed[i] + " already exists.");
								sb.append("0 -1");																						//Will print -1 if "identifier" being assigned already exists
							}  else {																									//Prints out token if it is not an assignment line
								sb.append("0 " + hashFunction(parsed[i]));
							}
						}  else if(!symbols.get(hashFunction(parsed[i])).equals(parsed[i])) {											//Checks if assignment of "identifier" that does not equal the current one at that
							if(parsed[0].equals("const") || parsed[0].equals("var")) {													// symbol table index will collide
								System.out.println("ERROR: Symbol " + symbols.get(hashFunction(parsed[i])) 
								+ " already exists at index " 
								+ hashFunction(parsed[i]) 
								+ ". Processing collision check now.\n");
								
								String collisionToken = collisionCheck(parsed[i], hashFunction(parsed[i]));								//Checks for collision and linearly assigns symbol to the next available index in the
								sb.append("0 " + collisionToken);																		// symbol table. Will put -1 in token output if collision avoidance was unsuccessful
							}  else {																									//If it is not an assignment line, condition will linearly search for symbol since it
								searchCount = hashFunction(parsed[i]) + 1;																// will not be in the right place as it is hashed
								searchEnd = searchCount + 6;
								
								while(searchCount != searchEnd) {																		//Goes through the next 5 symbol table indexes to find the right symbol if symbol had
									if(symbols.get(searchCount) != null && symbols.get(searchCount).equals(parsed[i])) {				// to be assigned linearly to avoid collision
										sb.append("0 " + searchCount);
									}
									
									searchCount++;
								}
							}
						}
					}
					
					if(i == parsed.length - 1) {																						
						break;
					}  else {
						sb.append(" * ");
					}
					
				}
				
				System.out.println("Program line number: " + lineNum);																	//Prints out the same output as what is described in the project description
				System.out.println(line);
				System.out.println("The tokens are: " + sb + "\n");
				
				lineNum++;
			}
				
		}  catch(Exception e) {																											//Handles exception of PL/0 example file cannot be found
				e.printStackTrace();
		}
	}
	
	public static String collisionCheck(String symbol, int tableIndex) {																//Function that will check for collision for assignment of a symbol
		
		tableIndex++;																													//Starts at the next slot in the symbol table after a collision was found
		int i = tableIndex + 5, attemptCounter = 1;																						//Sets up placement for looking at the next 5 spots in the symbol table
		
		
		while(tableIndex < i) {																											//Loop that goes through the next 5 spots in the symbol table
			
			System.out.print("Attempt #" + attemptCounter + " ");
			
			if(symbols.get(tableIndex) != null) {																						//Checks if the current spot you look at out of the 5 spots is occupied 
				System.out.println("ERROR: There is another symbol in that place.");
				attemptCounter++;
			}  else {																													//Checks if there is an empty spot for the symbol in the symbol table
				System.out.println("Collision avoidance was successful!\n");															// and breaks out of loop so it does not check anymore spots
				symbols.put(tableIndex, symbol);
				
				return Integer.toString(tableIndex);
			}
			tableIndex++;
		}
		
		System.out.println("Collision avoidance was unsuccessful.\n");																	//If loop never breaks and goes through all 5 spots, it will give up and
		return "-1";																													// return a -1 signaling that there was no collision avoidance
	}
	
	public static void populateKeywords(Dictionary d) {								//Function that will place tokens inside the passed dictionary
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
	
	public static int hashFunction(String input) {									//Hashfuntion that will generate a key for the input symbol
		char[] inputParse = input.toCharArray();									//Parses input symbol into char array
		int x = 0;
		
		for(int i = 0; i < inputParse.length; i++) {								//adds each char ASCII value into int x
			x += inputParse[i];
		}
		
		return x % SIZE;															//Generates a key modulating x with size of symbol table
	}
	
	public static void printSymbolsTable() {										//Helper function to create a physical representation of what's inside the symbol table
		int i = 0;
		
		while(i < 500) {
			System.out.println(i + ". " + symbols.get(i));
			
			i++;
		}
	}
	
}
