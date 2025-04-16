// http://localhost:8080/

package Web;

import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;


public class Servlet {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new IndexHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Сервер запущен на порту 8080");
    }
}
