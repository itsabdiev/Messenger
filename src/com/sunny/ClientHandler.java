package com.sunny;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler  implements Runnable{
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    public int online = clientHandlers.size() + 1;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    public ClientHandler (Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage(ANSI_BLACK + username + " has entered the chat" + ANSI_RESET);
        }catch (IOException e ) {
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }
    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            }catch (IOException e ) {
                closeEverything(socket,bufferedReader,bufferedWriter);
                break;
            }
        }
    }
    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.username.equals(username)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();

                }

            }catch (IOException e) {
                closeEverything(socket,bufferedReader,bufferedWriter);
            }
        }
    }
    public void removeClientHandler () {
        clientHandlers.remove(this);
        broadcastMessage( ANSI_BLACK + username + " has left the chat" + ANSI_RESET);
    }

    public void closeEverything (Socket socket,BufferedReader bufferedReader,BufferedWriter bufferedWriter) {
        removeClientHandler();
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
}
