import java.io.*;
import java.util.*;
import java.net.*;
import java.util.Scanner;

public class Server{
    ServerSocket serverSocket;
    Socket connection = null;
    ObjectOutputStream out;
    ObjectInputStream in;
    String message;
    String choice;
    Scanner scanner = new Scanner(System.in);
    Boolean commanding = false;

    public static void main(String args[]) {
        Server server = new Server();
        while(true){
          server.run();
        }
    }

    Server(){}

    void run() {
        try {
            // Creating a server socket
            serverSocket = new ServerSocket(52312, 10);

            // Wait for connection
            System.out.println("Waiting for connection");
            connection = serverSocket.accept();
            System.out.println("Connection received from " + connection.getInetAddress().getHostName());

            // Get Input and Output streams
            out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(connection.getInputStream());
            sendMessage("Connection successful");

            // The two parts communicate via the input and output streams
            do {
        try {
          if (!commanding) {
            message = (String)in.readObject();
            System.out.println("Client> " + message);

            switch(message.toUpperCase()) {
                case "HELP":
                    sendMessage(listCommands());
                    break;
                case "NEW":
                    commanding = true;
                    newCommand();
                    //commanding = false;
                    break;
                case "ADD":
                                commanding = true;
                                addCommand();
                case "TRADE":
                case "CHANGE":
                case "RENAME":
                case "REMOVE":
                case "PRINT":
                                commanding = true;
                                printCommand();
                case "SAVE":
                case "DELETE":
                case "QUERY":
                case "EXIT":
                  exitApplication();
                default: 
                    sendMessage("Invalid Command. Input 'HELP' for list of commands.");
                    break;
            }
          }
        } 
        catch(ClassNotFoundException classnot) {
          System.err.println("Data received in unknown format");
        }          
      } while (!message.equals("QUIT;"));
        }
        catch(IOException ioException) {
            ioException.printStackTrace();
        }
        finally {
          exitApplication();
        }
    }

    String listenToSocket() {
        try {
      // The two parts communicate via the input and output streams
            do {
        try {
          message = (String)in.readObject();
          System.out.println("Client> " + message);
          return message;
        }
        catch(ClassNotFoundException classnot) {
          System.err.println("Data received in unknown format");
        }            
      } while (!message.equals("QUIT;")); 
        }
        catch(IOException ioException) {
            ioException.printStackTrace();
        }
        return null;
    }

    void sendMessage(String msg) {
        try {
            out.writeObject(msg);
            out.flush();
            System.out.println("Server> " + msg);
        }
        catch(IOException ioException) {
            ioException.printStackTrace();
        }
    }

    String listCommands(){
      String listedCommandsString = 
      "HERE IS A LIST OF COMMANDS YOU MAY USE:" +
        "\nNEW - Allows the user to create a new table for: PLAYER, TEAM, or GAME" +
        "\nADD - Allows the user to insert a new entry for: PLAYER, TEAM, or GAME" +
        "\nREMOVE - Allows the user to delete entries for: PLAYER, TEAM, or a set of the both" +
        "\nTRADE - Allows the user to move players to different teams" +
        "\nCHANGE - Allows user to make changes to: PLAYER or TEAM" +
        "\nPRINT - Allows the user to see all information for: RELATION, PLAYER, TEAM, or SPORT" +
        "\nSAVE - Allows the user to save all data." +
        "\nDELETE - Allows the user to permanently remove data." +
        "\nQUERY - Allows the user to get certain data from the database.";

        return listedCommandsString;
    }

    void newCommand() {
       sendMessage("Would you like to create a Sport, Team, or Player?");
       String choice = listenToSocket();
       Vector<String> listOfAttr = new Vector<String>();

       int attrNumber = 0;
       do{
       sendMessage("Enter Number of Attributes for Table: ");
       String numOfAttr = listenToSocket();
       attrNumber = Integer.parseInt(numOfAttr);
       }while(attrNumber <= 0);

       for(int i = 0; i < attrNumber; i++)
       {
        String typeCheck = "";
        do{
        sendMessage("Specify Attribute Type for Attribute " + (i+1) + " (VARCHAR(x) or INTEGER): ");
        String attrType = listenToSocket();
        typeCheck = attrType.substring(0,7);
        sendMessage(typeCheck);
        sendMessage("Enter Name for Attribute " + (i+1) + ":");
        String attr = listenToSocket();
        listOfAttr.add(attr + " " + attrType);
        }while(!typeCheck.equalsIgnoreCase("VARCHAR") && !typeCheck.equalsIgnoreCase("INTEGER"));
       }

        String stringAttr = listOfAttr.toString().replace("[","").replace("]","");

        sendMessage("Pick 2 Attributes to be the Primary Key.\n" + "Attribute " + 1 + ":" );
        String pk1 = listenToSocket();
        sendMessage("Attribute " + 2 + ":" );
        String pk2 = listenToSocket();

        String create = "CREATE TABLE " + choice + " (" + stringAttr + ")" + " PRIMARY KEY " + "(" + pk1 + ", " + pk2 + ");" ;
        sendMessage(create);
        sendMessage("Data Received");

        commanding = false;
    }

    void addCommand(){
        sendMessage("Enter name of table for Insert entry: ");
        String choice = listenToSocket();
        

        commanding = false;


    }

    void printCommand(){
        sendMessage("Enter name of table for Print: ");
        String choice = listenToSocket();
        String show = "SHOW " + choice;

        commanding = false;

    }
    void tradeCommand(){

    }
    void changeCommand(){

    }

    void exitApplication() {
      // Close the connection
        try {
            System.out.println("Server connection closed");
            in.close();
            out.close();
            serverSocket.close();
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }
}