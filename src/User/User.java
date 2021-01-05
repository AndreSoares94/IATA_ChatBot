package User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

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

    /* I can't find any output from this call */
    public void nativeKeyTyped(NativeKeyEvent e) {
        System.out.println("Key Typed: " + e.getKeyChar());
    }


    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException{
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        String message = "";
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;

        String emotion = "";

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

        System.out.print("--------------- Chat With ChatBot! --------------- \n>");
        while(!message.equalsIgnoreCase("goodbye")){
            message = scanner. nextLine();

            //establish socket connection to server
            socket = new Socket(host.getHostName(), 9876);
            //write to socket using ObjectOutputStream
            oos = new ObjectOutputStream(socket.getOutputStream());

            //System.out.println("log: "+ log);
            emotion = decideEmotion(log, backspace);
            System.out.println("(" + emotion + ")");
            oos.writeObject(message);
            backspace = 0;
            log = "";

            //read the server response message
            ois = new ObjectInputStream(socket.getInputStream());
            String response = (String) ois.readObject();
            System.out.println("ChatBot: " + response);
        }
        System.out.println("Shutting Down ChatBot");
        //close resources
        ois.close();
        oos.close();
    }

    public static String decideEmotion(String log, int bs){
        // se deu mais de 5 backspaces está distraído:
        if (bs > 5) return "distracted";
        return "neutral";
    }
}
