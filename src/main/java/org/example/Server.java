package org.example;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int SERVER_SOCKET;
    private final ExecutorService executorService;

    public final ConcurrentHashMap<String, ConcurrentHashMap<String, Handler>> handlers;

    public Server(int serverSocket, int poolSize) {
        SERVER_SOCKET = serverSocket;
        executorService = Executors.newFixedThreadPool(poolSize);
        handlers = new ConcurrentHashMap<>();
    }

    public void start() throws RuntimeException {
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

    public void addHandler(String requestMethod, String path, Handler handler) {
        if (!handlers.containsKey(requestMethod)) {
            handlers.put(requestMethod, new ConcurrentHashMap<>());
        }
        handlers.get(requestMethod).put(path, handler);
    }


    private void proceedConnection(Socket socket) {
        try (final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final var out = new BufferedOutputStream(socket.getOutputStream())) {


            // read only request line for simplicity
            // must be in form GET /path HTTP/1.1
            final var requestLine = in.readLine();
            System.out.println(requestLine);
            final var parts = requestLine.split(" ");

            if (parts.length != 3) {
                // just close socket
                socket.close();
                return;
            }

            final var method = parts[0];
            final var path = parts[1];

            Request request = new Request(method, path);
            // System.out.println("to string: " + request.getQueryParam());

            if (!handlers.containsKey(request.getMethod())) {
                return;
            }
            var methodHandlerMap = handlers.get(request.getMethod());
            if (!methodHandlerMap.containsKey(request.getPath())) {
                return;
            }
            var handler = methodHandlerMap.get(request.getPath());

            handler.handle(request, out);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }


}
