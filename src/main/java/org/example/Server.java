package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.System.out;

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

    private void proceedConnection(Socket socket) throws RuntimeException {
        try (final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final var out = new PrintWriter(socket.getOutputStream())) {
            final var requestLine = in.readLine();
            final var parts = requestLine.split(" ");
            final var path = parts[1];
            while (!in.ready()) ;
            System.out.println();
            while (in.ready()) {
                final var filePath = Path.of(".", "public", path);
                final var mimeType = Files.probeContentType(filePath);

                // special case for classic
                if (path.equals("/classic.html")) {
                    final var template = Files.readString(filePath);
                    final var content = template.replace(
                            "{time}",
                            LocalDateTime.now().toString()
                    ).getBytes();
                    out.write(Arrays.toString((
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: " + mimeType + "\r\n" +
                                    "Content-Length: " + content.length + "\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes()));
                    out.write(Arrays.toString(content));
                    out.flush();
                    continue;
                }

                System.out.println(in.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.println("HTTP/1.1 404 Not Found");
        out.println("Content-Type: text/html; charset=utf-8");
        out.println();
        out.flush();

    }
}