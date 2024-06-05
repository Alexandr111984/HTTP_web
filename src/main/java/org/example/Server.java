package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final int SERVER_SOCKET;
    private final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png",
            "/resources.html",
            "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    private final ExecutorService executorService;

    public Server(int serverSocket, int poolSize) {
        SERVER_SOCKET = serverSocket;
        executorService = Executors.newFixedThreadPool(poolSize);
    }

    void start() throws RuntimeException {
        try (final var serverSocket = new ServerSocket(SERVER_SOCKET)) {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                executorService.execute(() -> proceedConnection(socket));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
        }
    }

    private void proceedConnection(Socket socket) throws RuntimeException {
        try (final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final var out = new PrintWriter(socket.getOutputStream())) {
            while (!in.ready()) ;
            System.out.println();
            while (in.ready()) {
                System.out.println(in.readLine());
            }
            out.println("HTTP/1.1 200 Ok");
            out.println("Content-Type: text/html; charset=utf-8");
            out.println();
            out.println("<p> Все хорошо!</p>");
            out.flush();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}