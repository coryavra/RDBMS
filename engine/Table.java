import java.util.*;
import java.io.*;

public class Table implements Serializable{
	
	ArrayList<Vector<String>> attribute_table = new ArrayList<Vector<String>>();
	String unique_id;
	Vector<Integer> p_key_indices = new Vector<Integer>();
	String[] attributes;
	String[] primary_keys;
	String table_name;

	Table(String t_name, String[] attribute_list, String[] p_keys){		// Take an array of attribute names and insert as first row in arraylist

		table_name = t_name;
		attributes = attribute_list.clone();
		primary_keys = p_keys.clone();

		// Record the indexes of the primary keys in the attribute list
		for (int i = 0; i < attribute_list.length; i++){
			for (int j = 0; j < p_keys.length; j++){
				if (attribute_list[i] == p_keys[j]) {
					p_key_indices.add(i);
				}
			}
		}

		// leave the first index as table name
		Vector<String> keys_vector = new Vector<String>();
		keys_vector.add(t_name);

		// Copy all the attributes to the vector
		for(int i = 0; i < attribute_list.length; i++) {
			keys_vector.add(attribute_list[i]);
		}

		// Set the first row 
		attribute_table.add(keys_vector);
	}

	public void deleteTable(){
		attribute_table = null;
	}

	public String getPKey(String[] values){

		// Set the initial string as empty
		String p_key = "";

		for (Integer i : p_key_indices) {

			// Iterative and concatenate it with all elements of the array
			p_key += values[i];
		}
		return p_key;
	}

	public void addRow(Vector<String> new_row){
		attribute_table.add(new_row);
	}

	public void deleteRow(String row_id){
		
		// Get the row, if it exists
		Vector<String> row = getRow(row_id);
		if (row.size() == 0){
			attribute_table.remove(row);
		}
		else{
			System.out.println("Error: Row doesn't exist. Failed to delete.");
		}
	}

	// Searches the table for a row, returns empty array if not found
	public Vector<String> getRow(String row_id){ //row_id is just the first element of the specified row
		
		// Search each row in the table
		for (Vector<String> row : attribute_table) {

			// Compare the first element of the row (its id) with the given id
			if (row.get(0) == row_id){
				return row;
			}
		}

		// If the row was not found, return an empty array
		Vector<String> empty_row = new Vector<String>(0);
		return empty_row;
	}

	public void show(){
		// Iterate through each row
		for (Vector<String> row : attribute_table) {
			System.out.println(row);
		}
	}

	public void selection(String attribute, String operator, String qualificator, String table_name, String new_table_name){
		int i = (Arrays.asList(attributes).indexOf(attribute));
		// if i == -1, exit

		for (Vector<String> row : attribute_table) {

			// Compare the first element of the row (its id) with the given id
			if (getOp(operator, row.get(i), qualificator)){
				addRow(row);
			}
		}
	}

	// This function creates a new table composed of a subset of attributes of a given table
	public Table projection(String new_table_name, String[] new_attr){
		Table new_table = new Table(new_table_name, new_attr, this.primary_keys);
		Vector<Integer> indicies = this.getIndicies(new_attr);

		for(int i = 1; i < attribute_table.size(); i++){
			Vector<String> temp_vector = attribute_table.get(i);
			Vector<String> temp_row = new Vector<String>();
			temp_row.add(temp_vector.get(0));

			for(int temp_index : indicies){
				temp_row.add(temp_vector.get(temp_index));
			}

		new_table.addRow(temp_row);
		}


		return new_table;
	}

	// This is a helper function that returns a vector of indicies (for a given subset
	// of attributes) to allow for easy location of data. 
	public Vector<Integer> getIndicies(String[] new_attr){
		Vector<Integer> indicies_list = new Vector<Integer>();

		for (int i = 1; i < attributes.length; i++){
			for (int j = 1; j < new_attr.length; j++){
				if (attributes[i] == new_attr[j]) {
					indicies_list.add(i);
				}
			}
		}
		return indicies_list;
	}

	public Boolean getOp(String operator, String a, String b){
		if ((a.matches("[0-9]+") && (b.matches("[0-9]+")))){

			switch(operator){
				case ">": 
					return (Integer.parseInt(a) > Integer.parseInt(b));
					//break;
				case "<": 
					return (Integer.parseInt(a) < Integer.parseInt(b));
					//break;
				case ">=": 
					return (Integer.parseInt(a) >= Integer.parseInt(b));
					//break;
				case "<=": 
					return (Integer.parseInt(a) >= Integer.parseInt(b));
					//break;
				case "==": 
					return a == b;
					//break;
				case "!=": 
					return a != b;
					//break;
			}
		}	
		return true;
	}

	public void writeTable(){
		try {
			FileOutputStream file_out = new FileOutputStream("table_data/" + table_name + ".ser");
			ObjectOutputStream out = new ObjectOutputStream(file_out);

			out.writeObject(attribute_table);
			out.close();
			file_out.close();
			System.out.printf("Serialized data is saved in table_data/" + table_name + ".ser");
     	}
     	catch(IOException i) {
      		i.printStackTrace();
     	}
  	}

  	public void readTable(){
		try {
			FileInputStream file_in = new FileInputStream("table_data/" + table_name + ".ser");
			ObjectInputStream in = new ObjectInputStream(file_in);

			ArrayList<Vector<String>> read_table = new ArrayList<Vector<String>>();
			read_table = (ArrayList<Vector<String>>) in.readObject(); // warning: [unchecked] unchecked cast
			in.close();
			file_in.close();
		}
		catch(IOException i) {
			i.printStackTrace();
			return;
		}
		catch(ClassNotFoundException c) {
			System.out.println("Table data not found");
			c.printStackTrace();
			return;
		}
  	}
}






