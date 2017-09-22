package com.example.tommy.tommy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static android.R.attr.type;

/**
 * Created by tom on 9/20/2017.
 */

public class ClientThread extends Thread {
    private static final String host = "18.220.131.76";
    private static final int port = 51600;
    private static final String msgFormat = "<%s:%s>";

    private HomeActivity homeActivity;
    private String username;
    private Socket clientSocket;
    private BlockingQueue<String> requestQueue;
    private boolean kill;

    public ClientThread(HomeActivity homeActivity, String username) {
        this.homeActivity = homeActivity;
        this.username = username;
        this.requestQueue = new LinkedBlockingQueue<>();
        kill = false;
    }

    public void kill() {
        kill = true;
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String buildMessage(String type, String value) {
        return String.format(msgFormat, type, value);
    }

    public void sendRequest(String request) {
        if (!request.isEmpty()) {
            requestQueue.add(request);
        }
    }

    public void sendMessage(String type, String value) {
        if (!type.isEmpty()) {
            sendRequest(buildMessage(type, value));
        }
    }

    public void sendMessage(String msg) {
        if (!msg.isEmpty()) {
            sendMessage(msg, "");
        }
    }

    @Override
    public void run() {
        try {
            clientSocket = new Socket(host, port);
            // Producer
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        out.println(buildMessage("username", username));
                        while (!kill) {
                            try {
                                String request = requestQueue.take();
                                out.println(request);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
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
                            homeActivity.respond(response);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
