/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package chatserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class broadCast extends Thread {

    ArrayList<Conversation> clients = new ArrayList<Conversation>();
    int nbClient = 0;

    public static void main(String[] args) throws IOException {
        //Démarrage du thread
        new broadCast().start();
    }

    @Override
    public void run() {
        int cpt = 5;

        System.out.println("Démarrage du serveur...");
        try {
            ServerSocket ss = new ServerSocket(1234);
            while (true) {
                //Accept connections
                Socket socket = ss.accept();
                nbClient++;
                Conversation conversation = new Conversation(socket, nbClient);
                clients.add(conversation);
                conversation.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        while (cpt > 0) {
            System.out.println("Bonjour le monde");
            cpt--;
        }

    }

    //Class Conversation inner ChatServer
    private class Conversation extends Thread {

        private int numClient;
        private Socket socket;

        public Conversation(Socket socket, int numClient) {
            super();
            this.socket = socket;
            this.numClient = numClient;
        }

        private void broaCastMessage(String message, Socket socket, int numClient) throws IOException {
            for (Conversation client : clients) {
                if(client.socket != socket){
                    if(client.numClient == numClient || numClient==2){
                        OutputStream os = client.socket.getOutputStream();
                        PrintWriter pw = new PrintWriter(os, true);
                        pw.println(message);
                    }
                }
            }
        }

        @Override
        public void run() {
            try {
                //Récupération d'un octet
                InputStream is;
                is = socket.getInputStream();
                //Récupération d'un caractère
                InputStreamReader isr = new InputStreamReader(is);
                //Formation d'une chaine de caractères
                BufferedReader br = new BufferedReader(isr);

                //Flux en sortie
                OutputStream os = socket.getOutputStream();
                //PrintWriter permet de récupérer la boite de dialogue du client pour lui envoyer un message
                PrintWriter pw = new PrintWriter(os, true);//true permet l'affichage d'une donnée dès qu'on l'a
                System.out.println("Connection du client " + numClient);

                //Récupération de l'adresse ip
                String IP = socket.getRemoteSocketAddress().toString();
                pw.println("Connection du client numéro " + numClient + " et d'adresse ip " + IP);

                //Récupération d'une chaine de caractère
                while (true) {
                    String req = br.readLine();
                    if (req != null) {
                        pw.println("Le client " + numClient + " a envoyé " + req);
                        int longReq = req.length();
                        pw.println("Longueur : " + longReq);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
