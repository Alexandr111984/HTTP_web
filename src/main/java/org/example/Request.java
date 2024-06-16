package org.example;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class Request {

    private final String method;
    private final String fullPath;
    private String path;

    private List<NameValuePair> pairs;

    public Request(String requestMethod, String requestPath) {
        this.method = requestMethod;
        this.fullPath = requestPath;
        if (!fullPath.equals("/")) {
            final var parts = fullPath.split("\\?", 2);
            path = parts[0];
            if (parts.length == 2)
                pairs = URLEncodedUtils.parse(parts[1], StandardCharsets.UTF_8);
        } else {
            this.path = fullPath;
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getFullPath() {
        return fullPath;
    }

    public String getQueryParam(String name) {
        for (int i = 0; i < pairs.size(); i++) {
            if (name.equals(pairs.get(i).getName()))
                return pairs.get(i).getValue();
        }
        return "";
    }

    public String getQueryParam() {
        return pairs.toString();
    }

}
