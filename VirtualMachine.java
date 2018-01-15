/* Description: Virtual Machine for the SubLC3 instruction set.
 * This program reads and executes commands from a text document named "mySubLC3.txt"
 * Class: CSCI4200-DB
 * Instructor: Dr. Abi Salimi
 * Assignment: SubLC3 Virtual Machine
 * Author: Caleb Arcega
 * Date: 11/20/2017
 * Version: 1.01
 * */

package vmPackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

public class VirtualMachine {
	static DecimalFormat fmt = new DecimalFormat("0.#########"); //Formats output to 9 decimal places only if required
	static String nextLine;                                      //Temporary String holder for file input
	static final int MAX_MEMORY_SIZE = 500;                      //Maximum memory size
	static String[] commands = new String[MAX_MEMORY_SIZE];      //Program memory
	static int programLength = 0;                                //How many lines of memory does the program take
	static int line = 0;                                         //Program counter
	static HashMap<String, Double> variables = new HashMap<>();  //Stores integer variables and labels with line number
	static String[] tokens = {"NULL"};                           //Holds tokens for each line
	static double operand1 = 0;                                  //Temporary operand storage
	static double operand2 = 0;                                  //Temporary operand storage
	static double result = 0;                                    //Temporary storage for operation results
	static 	Scanner tempScanner = new Scanner(System.in);
	/* Open the input data file and process its contents */
	public static void main(String[] args) throws FileNotFoundException {
		String filename = "mySubLC3.txt";
		/* Name and Class information */
		System.out.println("Name: Caleb Arcega\nClass: CSCI4200\nSection: DB\nInstructor: Dr. Abi Salimi\n");
		System.out.println("**********************************************************************\n");
		System.out.println("File name: "+filename);
		
		
		/* Uncomment if you desire to print file contents as well */
		//System.out.println("----------------------------------------------------------------------\n");
	
		/* Attempt file access */
		try {
			Scanner scan = new Scanner(new File("src\\vmPackage\\"+filename));
			/* read through file */
			int i = 0;
			
			/* Create default increment of 2 */
			variables.put("increment", 2.0);
			
			/* Load Phase */
			while (scan.hasNextLine()) {
				nextLine = scan.nextLine();
				
				/* Optionally print file contents */
				//System.out.println(nextLine);

				/* ignore comment lines */
				if (nextLine.length() != 0 && nextLine.charAt(0) != ';') {
					commands[i] = nextLine;
					i++;
					programLength++;
					
					/* labels must be pre-loaded into variable holder in case of jump to unvisited line */
					tokens = nextLine.split(" "); 
					if (tokens.length == 1) {               //if there is only one token, it is a label
						tokens = new String[3];
						tokens[0] = "Nothing";
						tokens[1] = nextLine;
						line = i;                           //store label as variable with corresponding line number
						tokens[2] = Integer.toString(line);
						sto();
					}

				}

			} // end while
			scan.close();
			System.out.println("\n**********************************************************************\n");
	/* Begin Fetch-Execute Cycle */
			execute();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		tempScanner.close();
		
		//END PROGRAM
	}

	public static void execute() {
	/* Initialize the program counter to 0, and begin fetch-execute */
		exe: for (line = 0; line < programLength; line++) {
			
            /* split line into tokens */
			tokens = commands[line].split(" ");
			
			/* Identify first token and execute corresponding method */
			switch (tokens[0]) {
			case "ADD":
				add();
				break;
			case "SUB":
				sub();
				break;
			case "MUL":
				mul();
				break;
			case "DIV":
				div();
				break;
			case "IN":
				in(tokens[1]);
				break;
			case "PUT": 
				/*Put is not specified in the assignment requirements,
				but is included so as to make provided example program
				function.*/
			case "OUT":
				out(commands[line]);
				break;
			case "STO":
				sto();
				break;
			case "BRn":
				brn();
				break;
			case "BRz":
				brz();
				break;
			case "BRp":
				brp();
				break;
			case "BRzp":
				brzp();
				break;
			case "BRzn":
				brzn();
				break;
			case "JMP":
				jmp();
				break;
			case "HALT":
				/*On HALT, break execution*/
				break exe;
			}
		}
	}

	/************************************************************
	 * Jump Method:                                             *
	 * Description: jumps to label in second token by accessing *
	 * the corresponding line number. Because the line number   *
	 * is incremented at the end of every fetch-execute cycle,  *
	 * this method jumps to the line before (then incremented   *
	 * immediately).                                            *
	 ************************************************************/
	private static void jmp() {
		line = (int) (variables.get(tokens[1])-1);
	}

	/************************************************************
	 * BRzn Method:                                             *
	 * Description: checks if variable is less than or equal    *
	 * to 0. If so, jump to label.                              *
	 ************************************************************/
	private static void brzn() {
		if (variables.get(tokens[1]) <= 0) {
			tokens[1] = tokens[2];
			jmp();
		}
	}
	
	/************************************************************
	 * BRzp Method:                                             *
	 * Description: checks if variable is greater than or equal *
	 * to 0. If so, jump to label.                              *
	 ************************************************************/
	private static void brzp() {
		if (variables.get(tokens[1]) >= 0) {
			tokens[1] = tokens[2];
			jmp();
		}
	}

	/************************************************************
	 * BRp Method:                                              *
	 * Description: checks if variable is greater than 0. If    *
	 * so, jump to label.                                       *
	 ************************************************************/
	private static void brp() {
		if (variables.get(tokens[1]) > 0) {
			tokens[1] = tokens[2];
			jmp();
		}
	}

	/************************************************************
	 * BRz Method:                                              *
	 * Description: checks if variable is equal to 0. If so,    *
	 * jump to label.                                           *
	 ************************************************************/
	private static void brz() {
		if (variables.get(tokens[1]) == 0) {
			tokens[1] = tokens[2];
			jmp();
		}
	}

	/************************************************************
	 * BRn Method:                                              *
	 * Description: checks if variable is less than 0. If so,   *
	 * jump to label.                                           *
	 ************************************************************/
	private static void brn() {
		if (variables.get(tokens[1]) < 0) {
			tokens[1] = tokens[2];
			jmp();
		}
	}

	/*************************************************************
	 * Store Method:                                             *
	 * Description: This method checks the tokens and determines *
	 * whether or not it is creating a variable, or copying an   *
	 * existing one. New variables are initialized and stored in *
	 * the 'variables' hashmap.                                  *
	 ************************************************************/
	private static void sto() {
		/* if not storing from variable, parse the spot at tokens[2] to an
		 integer and store as new variable */

		if (tokens[2].charAt(0) == '-' || Character.isDigit(tokens[2].charAt(0)))
			variables.put(tokens[1], Double.parseDouble(tokens[2]));
		else
			variables.put(tokens[1], variables.get(tokens[2])); // else copy variable
	}

	/************************************************************
	 * Input Method:                                            *
	 * Description: This method takes in user integer input and *
	 * stores it to a new variable in the variable hashmap.     *
	 ************************************************************/
	// Inputs an integer value and stores it in Variable.
	private static void in(String varName) {
		try{
	
		variables.put(varName, (double)tempScanner.nextInt());
		
		}catch(InputMismatchException e){
			e.printStackTrace();
		}
	}

	
	/**********************************************
	 * Assign Method:                             *
	 * Description: This method first checks for  *
	 * variable or integer token, then assigns    *
	 * values to operand1 and operand2 for        *
	 * operation                                  *
	 * ********************************************/
	public static void assign(){
		//NOTE: checking for '-' pertains to negative numbers. Identifiers cannot begin with '-'
		operand1 = tokens[2].charAt(0) == '-' || Character.isDigit(tokens[2].charAt(0)) ? Double.parseDouble(tokens[2]) : variables.get(tokens[2]);
		operand2 = tokens[3].charAt(0) == '-' || Character.isDigit(tokens[3].charAt(0)) ? Double.parseDouble(tokens[3]) : variables.get(tokens[3]);				
	}
	
	/**********************************************************
	 * Division Method:                                       *
	 * Description: This method first calls for assignment of *
	 * operand 1 & 2, then performs integer division between  *
	 * the two operands. The result is then stored to the     *
	 * designated variable.                                   *
	 **********************************************************/
	private static void div() {
		// assign operands
		assign();
		
		// perform division
		result = operand1 / operand2; // NOTE: this has been modified to perform floating point division

		// send to sto
		tokens[2] = Double.toString(result);
		sto();

	}

	/**********************************************************
	 * Multiplication Method:                                 *
	 * Description: This method first calls for assignment    *
	 * of operand 1 & 2, then performs multiplication between *
	 * the two operands. The result is then stored to the     *
	 * designated variable.                                   *
	 **********************************************************/
	private static void mul() {
		// assign operands
		assign();

		// perform multiplication
		result = operand1 * operand2;

		// send to sto
		tokens[2] = Double.toString(result);
		sto();

	}
	
	/**********************************************************
	 * Subtraction Method:                                    *
	 * Description: This method first calls for assignment of *
	 * operand 1 & 2, then performs subtraction between      *
	 * the two operands. The result is then stored to the     *
	 * designated variable.                                   *
	 **********************************************************/
	private static void sub() {
		// assign operands
		assign();
		
		// perform subtraction
		result = operand1 - operand2;
		
		// send to sto
		tokens[2] = Double.toString(result);
		sto();
		
	}

	/**********************************************************
	 * Addition Method:                                       *
	 * Description: This method first calls for assignment of *
	 * operand 1 & 2, then performs addition between          *
	 * the two operands. The result is then stored to the     *
	 * designated variable.                                   *
	 **********************************************************/
	private static void add() {
		// assign operands
		assign();
		
		// perform addition
		result = operand1 + operand2;

		// send to sto
		tokens[2] = Double.toString(result);
		sto();
	}

	/**********************************************************
	 * Output Method:                                         *
	 * Description: This method first checks for variable or  *
	 * String literal tokens, then outputs the corresponding  *
	 * values.                                                *
	 **********************************************************/
	public static void out(String output) {
		if (tokens[1].contains("\""))
			System.out.println(output.substring(5, output.length() - 1));
		else
			System.out.println(fmt.format(variables.get(tokens[1])));
	}
}


/*********************
 * Testing programs: *
 * *******************/


/*
; Display a sequence of numbers in reverse order
; Initialize the values
OUT "Enter the initial number:"
IN number
STO increment 2
; Display the values
OUT "The values are:"
SUB tempNum number 1
loopStart
BRn tempNum loopEnd
OUT number
SUB number number increment
SUB tempNum number 1
JMP loopStart
loopEnd
PUT "H A V E A N I C E D A Y !"
HALT
*/

/*
; Secondary test program
OUT "Please enter an integer"
IN user_num
MUL result_num user_num 2
OUT "Your number times 2 is: "
OUT result_num
HALT
*/

/*
OUT "LOOP TEST"
OUT "How many times should I loop?"
IN count
OUT "What should do integer division by?"
IN divisor
OUT "What Should I divide?"
IN numerator
startLoop
DIV numerator numerator divisor
SUB count count 1
BRzn count endLoop
JMP startLoop
endLoop
OUT "Your number is: "
OUT numerator
OUT "Goodbye!"
HALT
*/

