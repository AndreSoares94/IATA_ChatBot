package Bot;

import java.io.*;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.DoubleStream;

public class ChatBot {

    //static ServerSocket variable
    private static ServerSocket server;

    //socket server port on which it will listen
    private static int port = 9876;

    // Get total number of lines in responses.
    private static int getLines(String filename) {
        int lines = 0;

        try(BufferedReader br = new BufferedReader(
                new FileReader(filename))) {
            while(br.readLine() != null) lines++;
        } catch(IOException exc) {
            System.out.println("I/O Exception: " + exc);
        }

        return lines;
    }

    // Get all responses.
    private static String[] getResponsesArray(String filename, int lines) {
        int lineCount = 0;

        String line;
        String[] responsesArray = new String[lines];

        try(BufferedReader br = new BufferedReader(
                new FileReader(filename))) {

            do {
                line = br.readLine();

                if(line != null) {
                    responsesArray[lineCount] = line;
                    lineCount++;
                }
            } while(line != null);
        } catch(FileNotFoundException exc) {
            System.out.println("FileNotFoundException: " + exc);
        } catch(IOException exc) {
            System.out.println("I/O Exception: " + exc);
        }

        return responsesArray;
    }

    // Get ChatBot response to userInput
    private static String getResponse(String[] responses, String userInput) {
        String tag, response;
        String[] array;

        for(String responseLine: responses) {
            if(responseLine != null) {
                array = responseLine.split(" - ");
                tag = array[0];
                response = array[1];

                if(tag.compareToIgnoreCase(userInput) == 0) {
                    return response;
                }
            }
        }
        return "Sorry, i did not understand, please try again";
    }

    public static void main(String args[]) throws IOException, ClassNotFoundException{
        //create the socket server object
        String filename = "src/Bot/responses.txt";
        server = new ServerSocket(port);

        int lines = getLines(filename);
        String resposta;
        String[] responsesArray = getResponsesArray(filename, lines);

        //SOCKET SERVER
        while(true){
            System.out.println("Waiting for the user message");
            //creating socket and waiting for client connection
            Socket socket = server.accept();
            //read from socket to ObjectInputStream object
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            //convert ObjectInputStream object to String
            String userInput = (String) ois.readObject();
            System.out.println("Message Received: " + userInput);
            //create ObjectOutputStream object
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            resposta = getResponse(responsesArray, userInput);
            //write object to Socket
            oos.writeObject(resposta);
            //close resources
            ois.close();
            oos.close();
            socket.close();
            //terminate the server if client sends exit request
            if(userInput.equalsIgnoreCase("goodbye")) break;
        }
        System.out.println("Shutting down ChatBot!");
        //close the ServerSocket object
        server.close();
    }
}
