package com.example.tommy.tommy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class responsible for communicating with the server.
 * Concurrently sends requests added to the queue and receives responses from the server.
 * The received responses are passed to HomeActivity and processed there.
 */

public class ClientThread extends Thread {
    private static final String host = "18.220.131.76";
    private static final int port = 51600;
    private static final long reconnectionDelayInMillis = 2000;

    private HomeActivity homeActivity;
    private String username;
    private Socket clientSocket;
    private BlockingQueue<String> requestQueue;
    private boolean kill;

    public ClientThread(HomeActivity homeActivity, String username) {
        this.homeActivity = homeActivity;
        this.username = username;
        this.requestQueue = new LinkedBlockingQueue<>();
        clientSocket = null;
        kill = false;
    }

    private void closeSocket() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Terminates the connection with the server.
     */
    public void kill() {
        kill = true;
        requestQueue.add("");
        closeSocket();
    }

    /**
     * Sends a new request to the server.
     */
    public void sendRequest(String request) {
        if (!request.isEmpty()) {
            requestQueue.add(request);
        }
    }

    /**
     * Sends a new message to the server without value.
     */
    public void sendMessage(String type, String value) {
        if (!type.isEmpty()) {
            sendRequest(new Message(type, value).build());
        }
    }

    /**
     * Sends a new message to the server with value.
     */
    public void sendMessage(String type) {
        if (!type.isEmpty()) {
            sendMessage(type, "");
        }
    }

    private void connectToServer() {
        boolean isConnected = false;
        while (!kill && !isConnected) {
            try {
                clientSocket = new Socket(host, port);
                isConnected = true;
            } catch (IOException e) {
                try {
                    Thread.sleep(reconnectionDelayInMillis);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
    }

    /**
     * Establishes a connection with the the server.
     * The 'producer' thread sends requests from the queue to the server.
     * The 'consumer' thread receives requests from the server and passes them to the HomeActivity.
     */
    @Override
    public void run() {
        connectToServer();
        // Producer
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println(new Message("username", username).build());
                    while (!kill) {
                        try {
                            String request = requestQueue.take();
                            if (!kill) {
                                out.println(request);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        // Consumer
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    while (!kill) {
                        String response = in.readLine();
                        if (!kill) {
                            if (response == null) {
                                homeActivity.reEstablishConnection();
                                kill();
                            } else {
                                homeActivity.processResponse(response);
                            }
                        }
                    }
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}