package Bot;

import org.alicebot.ab.*;
import org.alicebot.ab.Chat;

import java.io.*;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatBot {
    //static ServerSocket variable
    private static ServerSocket server;
    //socket server port on which it will listen
    private static int port = 9876;
    private static String botname;

    public static void main(String args[]) throws IOException, ClassNotFoundException{
        //create the socket server object
        server = new ServerSocket(port);
        botname = "empty";

        // Resumo
        File resumo = new File("Resumo.txt");
        if(resumo.exists()){
            resumo.delete();
        }
        BufferedWriter out = new BufferedWriter(new FileWriter(resumo, true));
        String entrada = "";

        // bot session
        String path = "src/Bot";
        Bot bot = new Bot(botname, path);
        Chat chatSession = new Chat(bot);

        //keep listens indefinitely until receives 'exit' call or program terminates
        while(true){
            System.out.println("Waiting for the user message");
            //creating socket and waiting for client connection
            Socket socket = server.accept();
            //read from socket to ObjectInputStream object
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            //convert ObjectInputStream object to UserInfo
            UserInfo userInfo = (UserInfo) ois.readObject();
            //split message and emotion part:
            String message = userInfo.getChatString();
            String emotion = userInfo.getEmotion();
            // Set emotion
            chatSession.multisentenceRespond("EMOTION "+emotion);
            //gets answer to message:
            String answer = chatSession.multisentenceRespond(message);
            System.out.println("Message Received: " + message);
            //create ObjectOutputStream object
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            //write object to Socket
            oos.writeObject("Answer: "+answer);
            //write entrada do Resumo
            entrada = "User ["+emotion+"]: "+message+";"+'\n'+"ChatBot: "+answer+";"+"\n ----------------------------";
            out.write(entrada);
            out.newLine();
            //close resources
            ois.close();
            oos.close();
            socket.close();
            //terminate the server if user says "adeus"
            if(message.equalsIgnoreCase("adeus")) break;
        }
        out.close();
        System.out.println("Shutting down Socket Bot.ChatBot!");
        //close the ServerSocket object
        server.close();
    }
}
