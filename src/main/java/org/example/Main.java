package org.example;


import java.io.IOException;

public class Main {
    private static final int SERVER_PORT = 9999;
    private static final int THREAD_POOL_SIZE = 64;

    public static void main(String[] args) {
        Server server = new Server(SERVER_PORT, THREAD_POOL_SIZE);

        server.addHandler(HttpMethod.GET, "/messages", ((request, responseStream) -> {

            try {
                String path = request.getPath();
                System.out.println("in handler: " + path);

                if (path.equals("/messages")) {
                    responseStream.write((
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: " + "text/html" + "\r\n" +
                                    "Content-Length: " + request.getFullPath().length() + "\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    responseStream.write(path.getBytes());
                    responseStream.flush();
                    return;
                }
                responseStream.write((
                        "HTTP/1.1 404 OK\r\n" +
                                "Content-Type: " + "text/html" + "\r\n" +
                                "Content-Length: " + 0 + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                responseStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        server.addHandler(HttpMethod.POST, "/", (_, responseStream) ->

        {
            String response = "Hello from Post";
            var responseBytes = response.getBytes();
            responseStream.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: plain/text\r\n" +
                            "Content-Length: " + responseBytes.length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            responseStream.write(responseBytes);
            responseStream.flush();
        });

        server.addHandler(HttpMethod.DELETE, "/", (_, responseStream) ->

        {
            String response = "Hello from DELETE";
            var responseBytes = response.getBytes();
            responseStream.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: plain/text\r\n" +
                            "Content-Length: " + responseBytes.length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            responseStream.write(responseBytes);
            responseStream.flush();
        });


        server.start();
    }


}
