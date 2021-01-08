import Bot.UserInfo;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User implements NativeKeyListener{

    static String log = "";
    static int backspace = 0;
    /* Key Pressed */
    public void nativeKeyPressed(NativeKeyEvent e) {
        //System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
        /* Terminate program when one press ESCAPE */
        log += NativeKeyEvent.getKeyText(e.getKeyCode());
        if (e.getKeyCode() == NativeKeyEvent.VC_BACKSPACE) {
            backspace++;
        }
    }

    /* Key Released */
    public void nativeKeyReleased(NativeKeyEvent e) {
        // System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
    }

    /* can't find any output from this call */
    public void nativeKeyTyped(NativeKeyEvent e) {
        System.out.println("Key Typed: " + e.getKeyChar());
    }


    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException{

        //get the localhost IP address, if server is running on some other IP, you need to use that
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        String message = "";
        String sugestao = "";
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        long timeElapsed = 0;
        long start;
        long finish;
        String emotion = "";
        int edition = 0;

        // Resumo
        File resumo = new File("Resumo.txt");
        if(resumo.exists()){
            resumo.delete();
        }
        BufferedWriter out = new BufferedWriter(new FileWriter(resumo, true));
        String entrada = "";

        /* Cenas para iniciar o logger: */
        try {
            /* Register jNativeHook */
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            /* Its error */
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        // https://github.com/kwhat/jnativehook/blob/2.2/doc/ConsoleOutput.md
        // Get the logger for "com.github.kwhat.jnativehook" and set the level to off.
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        // Don't forget to disable the parent handlers.
        logger.setUseParentHandlers(false);
        GlobalScreen.addNativeKeyListener(new User());

        Scanner scanner = new Scanner(System. in);

        System.out.print("Chat With Bot.ChatBot! \n>");
        while(!(message.equalsIgnoreCase("adeus"))){
            start = System.currentTimeMillis();
            message = scanner. nextLine();
            finish = System.currentTimeMillis();
            timeElapsed = finish - start;

            //establish socket connection to server
            socket = new Socket(host.getHostName(), 9876);
            //write to socket using ObjectOutputStream
            oos = new ObjectOutputStream(socket.getOutputStream());


            emotion = decideEmotion(log, backspace, timeElapsed);
            UserInfo userInfo = new UserInfo(message, emotion);
            System.out.println("emotion: " + userInfo.getEmotion());
            oos.writeObject(userInfo);
            backspace = 0;
            log = "";

            //read the server response message
            ois = new ObjectInputStream(socket.getInputStream());
            String response = (String) ois.readObject();

            System.out.println("Premir y/n para aceitar: [sugestão] "+response);

            char c = scanner.next().charAt(0);
            if(c=='n'){
                scanner.nextLine();
                response = scanner.nextLine();
                System.out.println("Bot.ChatBot: " + response);
            }
            else{ if(c=='y') {
                scanner.nextLine();
                System.out.println("Bot.ChatBot: " + response);
                }
            }
            //write entrada do Resumo
            entrada = "User ["+emotion+"]: "+message+";"+'\n'+"ChatBot: "+response+";"+"\n ----------------------------";
            out.write(entrada);
            out.newLine();
        }
        //close resources
        ois.close();
        oos.close();
        socket.close();
        scanner.close();
        out.close();

    }

    public static String decideEmotion(String log, int backspaces, long timeelapsed){
        // se deu mais de 5 backspaces está distraído:
        if (backspaces > 5) return "stressado";
        if (timeelapsed > 12000) return "distraido";
        return "neutral";

    }
}
