/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package miniweb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maxime Blaise
 */
public class MiniWeb {

    /**
     * Port du serveur, par défaut 80.
     */
    private int port;

    /**
     * Construction du mini serveur sur le bon port.
     *
     * @param port
     */
    public MiniWeb(String port) {
        this.port = new Integer(port);

        this.ecoute();
    }

    /**
     * Une fois cette méthode appelé, le navigateur pourra communiquer avec
     * notre serveur.
     */
    private void ecoute() {
        String fichier = "";
        Socket s = null;
        try {
            //Initialisation des variables dont on aura besoin
            ServerSocket ss = new ServerSocket(this.port);
            System.out.println(getColor(92) + "Max mini Server Web lancé ! " + getColor(96) + "(port " + this.port + ")" + getColor(0));
            s = ss.accept();

            //Création des objets nécessaires à la lecture
            InputStream is = s.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            //On stock la lecture dans cette variable
            String retour = "";

            //Lecture
            //String lecture = "";
            while (br.ready()) {
                retour += br.readLine() + "<br/>";
            }

            String premiereLigne = retour.split("<br/>")[0];
            String[] splitPremiereLigne = premiereLigne.split(" ");

            if (splitPremiereLigne[0].equals("GET")) {
                //Méthode GET
                if (splitPremiereLigne.length >= 2) {
                    fichier = splitPremiereLigne[1];

                    if (fichier.equals("/")) {
                        //Redirection vers l'index
                        fichier += "index.html";
                    }

                    System.out.println("\nFichier demandé : " + getColor(92) + fichier + getColor(0) + "\n");
                }
            }
            //Affichage dans le terminal
            //System.out.println(retour);

        } catch (IOException ex) {
            int newPort = (int) ((Math.random() * 9000) + 1000);
            System.out.println(getColor(91) + "Erreur lors de la connexion sur le port " + port + "... \n tentative de connexion sur le port " + getColor(96) + newPort + getColor(91) + "(aléatoire)");
            this.setPort(newPort);
            this.ecoute();
        } finally {
            try {
                //String newFichier = fichier.substring(1, fichier.length());
                try (BufferedReader brFichier = new BufferedReader(new FileReader("."+fichier))) {

                    //Si c'est une image 
                    // /!\ NE FONCTIONNE MALHEUREUSEMENT PAS
                    if (fichier.endsWith(".png")) {
                        //Envoi du message, en html.
                        if (s != null) {

                            File fTest = new File(fichier);
                            String path = fTest.getAbsolutePath();

                            String retour = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head><body><p><img src=\"" + path + "\" alt=\"Une image\" /></p></body></html>";
                            try (OutputStream os = s.getOutputStream(); PrintWriter pw = new PrintWriter(os)) {
                                pw.println("HTTP/1.1 200 OK\nContent-Type: text/html\n\n");
                                pw.println(retour);
                                pw.println("");
                                pw.close();
                            }
                        }

                        /* String stringData = "", stringFinal = "";
                         //Lecture du fichier
                         while ((stringData = brFichier.readLine()) != null) {
                         stringFinal += stringData;
                         }
                         byte[] data = stringFinal.getBytes();
                         try (OutputStream os = s.getOutputStream(); PrintWriter pw = new PrintWriter(os)) {
                         pw.print("HTTP/1.1 200 OK\n");
                         os.write(data);
                            
                         os.close();
                         pw.close();
                         }*/
                    } else {

                        //Fichier HTML (normalement)
                        String lineLecture = "", retour = "";
                        //Parcourt du fichier
                        while ((lineLecture = brFichier.readLine()) != null) {
                            retour += lineLecture + "\n";
                        }

                        //Envoi du message, en html.
                        if (s != null) {
                            try (OutputStream os = s.getOutputStream(); PrintWriter pw = new PrintWriter(os)) {
                                pw.println("HTTP/1.1 200 OK\nContent-Type: text/html\n\n");
                                pw.println(retour);
                                pw.println("");
                            }
                        }
                    }

                }
            } catch (FileNotFoundException ex) {
                //Logger.getLogger(MiniWeb.class.getName()).log(Level.SEVERE, null, ex);
                //Envoi du message, en html.
                if (s != null) {
                    String retour = "<html><p style=\"color: red; font-size: 3em;\">File Not Found </p></html>";

                    try (OutputStream os = s.getOutputStream(); PrintWriter pw = new PrintWriter(os)) {
                        pw.println("HTTP/1.1 200 OK\nContent-Type: text/html\n\n");
                        pw.println(retour);
                        pw.println("");
                    } catch (IOException ex1) {
                        Logger.getLogger(MiniWeb.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(MiniWeb.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Permet de récupérer le port du mini serveur web.
     *
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     * Permet de changer le port du serveur.
     *
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        if (args.length == 1) {
            //Le port est renseigné
            MiniWeb mw = new MiniWeb(args[0]);
        } else if (args.length == 0) {
            //On prend le port par défaut, c'est-à-dire 80
            MiniWeb mw = new MiniWeb("80");
        }
    }

    /**
     * Permet de colorer le terminal Linux.
     *
     * @param i
     * @return
     */
    public static String getColor(int i) {
        return "\033[" + i + "m";
    }

}
