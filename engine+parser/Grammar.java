import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;
import java.io.*;

public class Grammar {
   
	private Vector<String> token_vector = new Vector<String>();

	Grammar(String line){

		// Tokenize the input and store in a vector
		String delimiters = "(){}; \t\n\r\f";
		StringTokenizer st = new StringTokenizer(line, delimiters, true);
		while(st.hasMoreTokens()){
			String token = st.nextToken();
			if (!token.trim().isEmpty() && !token.equals(",")) {
				token = token.replaceAll(",", "");
				token_vector.add(token);
			 }
		}

		System.out.println("\n" + token_vector);

		for (String token : token_vector){
			switch(token) {
				case "CREATE":
					System.out.println("CREATE TABLE invoked");
					createCommand(token_vector);
					break;
				case "DROP":
					System.out.println("DROP TABLE invoked");
					dropCommand(token_vector);
					break;
				case "OPEN":
					System.out.println("OPEN invoked");
					openCommand(token_vector);
					break;
				case "ClOSE":
					System.out.println("CLOSE invoked");
					closeCommand(token_vector);
					break;
				case "WRITE":
					System.out.println("WRITE invoked");
					writeCommand(token_vector);
					break;
				case "SHOW":
					System.out.println("SHOW invoked");
					showCommand(token_vector);
					break;
				case "EXIT":
					System.out.println("EXIT invoked");
					exitCommand();
					break;
				case "UPDATE":
					System.out.println("UPDATE invoked");
					updateCommand(token_vector);
					break;
				case "DELETE":
					break;
				case "INSERT":
					System.out.println("INSERT invoked");
					insertCommand(token_vector);
					break;
				case "SELECT":
					break;
				case "PROJECT":
					break;
				case "RENAME":
			}
		}
	}

	public static void evaluateExpression(Vector<String> token_vector){
		for (String token : token_vector){
			switch(token) {
				case "CREATE":
					System.out.println("CREATE TABLE invoked");
					createCommand(token_vector);
					break;
				case "INSERT":
				case "SELECT":
				case "PROJECT":
			}
		}
	}

	public static String getRelationName(Vector<String> token_vector){
		String relation_name = "";

		if (token_vector.contains("CREATE") ||
			token_vector.contains("SELECT") ||
			token_vector.contains("PROJECT") ||
			token_vector.contains("+") ||
			token_vector.contains("-") ||
			token_vector.contains("*") ||
			token_vector.contains("JOIN")){
				System.out.println("Nested table detected in getRelationName");
		}
		else {
			for (String token : token_vector){
				if (!token.equals("OPEN") &&
					!token.equals("CLOSE") &&
					!token.equals("WRITE") &&
					!token.equals("SHOW") &&
					!token.equals("DROP") &&
					!token.equals("TABLE") &&
					!token.equals(";")){
					relation_name += token;
				}
			}
		}
		return relation_name;
	}

	public static Table createCommand(Vector<String> token_vector){	

		String relation_name = "";
		Vector<String> attribtues_vector = new Vector<String>();
		Vector<String> keys_vector = new Vector<String>();

		// Start the token index at the begining of the relation name
		Integer token_index = 2;

		// Get the relation name
		for (int i = token_index; i < token_vector.size(); i++){
			if (token_vector.get(i).equals("(")){
				token_index = i;
				break;
			}
			else {
				relation_name += token_vector.get(i);
			}
		}

		// Get the attribute list
		for (int i = token_index; i < token_vector.size(); i++){
			if (token_vector.get(i).equals("PRIMARY")) {
				token_index = i;
				break;
			}
			else if (token_vector.get(i).equals("VARCHAR")) {
				i = i + 2;
				continue;
			}
			else if (token_vector.get(i).equals("INTEGER")) {
				continue;
			}
			else if (!token_vector.get(i).equals("(") && !token_vector.get(i).equals(")")) {
				attribtues_vector.add(token_vector.get(i));
			}
			else {
				//System.out.println("Skipping token... " + token_vector.get(i));
			}
		}

		// Get the primary keys list
		for (int i = token_index; i < token_vector.size(); i++){
			if (token_vector.get(i).equals(";")) {
				token_index = i;
				break;
			}
			else if (token_vector.get(i).equals("PRIMARY") || token_vector.get(i).equals("KEY")) {
				continue;
			}
			else if (!token_vector.get(i).equals("(") && !token_vector.get(i).equals(")")) {
				keys_vector.add(token_vector.get(i));
			}			
			else {
				//System.out.println("Skipping token... " + token_vector.get(i));				
			}
		}

		System.out.println("Table name:" + relation_name);
		System.out.println("Attribute List:" + attribtues_vector);
		System.out.println("Primary keys List:" + keys_vector);

		// Convert the arrays to vectors
		String[] attributes_array = attribtues_vector.toArray(new String[attribtues_vector.size()]);
		String[] keys_array = keys_vector.toArray(new String[keys_vector.size()]);

		Table new_table = Engine.createTable(relation_name.trim(), attributes_array, keys_array);
		return new_table;
	}

	public static void insertCommand(Vector<String> token_vector){
		String relation_name = "";
		Vector<String> data_vector = new Vector<String>();

		Integer token_index = 2;

		// Gets the relation name
		for (int i = token_index; i < token_vector.size(); i++){
			if (token_vector.get(i).equals("VALUES")){
				token_index = i + 2;
				break;
			}
			else {
			relation_name += token_vector.get(i);
			}
		}

		// Get the list of values to be inserted
		for (int i = token_index; i < token_vector.size(); i++){
			if (token_vector.get(i).equals(";")) {
				token_index = i;
				break;
			}
			else if (token_vector.get(i).equals("RELATION")){
				token_index = i;
				Vector<String> new_vec = new Vector<String>();
				for(int j = token_index; j < token_vector.size(); j++){
					new_vec.add(token_vector.get(j));
				}
				System.out.println(new_vec);
				// evaluateExpression(new_vec);
			}
			else if (!token_vector.get(i).equals("(") && !token_vector.get(i).equals(")")) {
				data_vector.add(token_vector.get(i));
			}
			else {
				//System.out.println("Skipping token... " + token_vector.get(i));
			}
		}

		String[] data_array = data_vector.toArray(new String[data_vector.size()]);
		Engine.insertRow(relation_name.trim(), data_array);
	}

	public static void updateCommand(Vector<String> token_vector){
		String relation_name = "";
		String row_id = "";
		Vector<String> values_vec = new Vector<String>();

		Integer token_index = 1;

		// Gets the relation name
		for (int i = token_index; i < token_vector.size(); i++){
			if (token_vector.get(i).equals("SET")){
				token_index = i + 1;
				break;
			}
			else {
				relation_name += token_vector.get(i);
			}
		}

		// gets the set of data
		for (int i = token_index; i < token_vector.size(); i++){
			if (token_vector.get(i).equals("WHERE")) {
				token_index = i;
				break;
			}
			else {
			}
		}

		/* We have to careful with this function because it depending on how the
		input comes in (whitespace or not) depends on how we go about implementing it.
		ex. UPDATE animals SET kind="dog",age="10" WHERE name="Leroy"
			-> [UPDATE, animals, SET, kind="dog". age="10", WHERE, name="Leroy"]
		vs
		UPDATE animals SET kind = "dog", age = "10" WHERE name = "Leroy"
			-> [UPDATE, animals, SET, kind, =, "dog", . . .]
		*/



	}


	public static void dropCommand(Vector<String> token_vector){	
		String relation_name = getRelationName(token_vector);
		System.out.println("Table Name: " + relation_name);
		Engine.dropTable(relation_name.trim());
	}

	public static void openCommand(Vector<String> token_vector){	
		String relation_name = getRelationName(token_vector);
		System.out.println("Table Name: " + relation_name);
		Engine.writeTable(relation_name.trim()); // Close table???
	}

	public static void closeCommand(Vector<String> token_vector){	
		String relation_name = getRelationName(token_vector);
		System.out.println("Table Name: " + relation_name);
		Engine.writeTable(relation_name.trim());
	}

	public static void writeCommand(Vector<String> token_vector) {
		String relation_name = getRelationName(token_vector);
		System.out.println("Table Name: " + relation_name);
		Engine.writeTable(relation_name.trim());
	}

	public static void showCommand(Vector<String> token_vector) {
		String relation_name = getRelationName(token_vector);
		System.out.println("Table Name: " + relation_name);
		Engine.show(relation_name.trim());
	}

	public static void exitCommand() {
		System.out.println("Program ending");
		// end program
	}

}











