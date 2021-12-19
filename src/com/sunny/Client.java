package com.sunny;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";


    public Client(Socket socket,String username) {
        try {
        this.socket = socket;
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.username = username;
        }catch (IOException e ) {
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    public void sendMessage () {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username + " : " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch (IOException e) {
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    public void listenForMessage () {
        new Thread(() -> {
            String messageFromGroupChat;

            while(socket.isConnected()) {
                try {
                    messageFromGroupChat = bufferedReader.readLine();
                    System.out.println(messageFromGroupChat);
                }catch (IOException e) {
                    closeEverything(socket,bufferedReader,bufferedWriter);
                }
            }
        }).start();
    }
    public void closeEverything (Socket socket,BufferedReader bufferedReader,BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }

        }catch (IOException e ) {
            e.printStackTrace();
        }
    }
    public static void main (String [] args ) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println(ANSI_BLACK + "Enter your username" + ANSI_RESET);
        String username = scanner.nextLine();
        Socket socket = new Socket("192.168.0.104",1234);
        while (username.length() < 3) {
            System.out.println(ANSI_BLACK + "Username must contain at least 3 chars" + ANSI_RESET);
            username = scanner.nextLine();
        }
        Client client = new Client(socket,username);
        client.listenForMessage();
        client.sendMessage();




    }



}
