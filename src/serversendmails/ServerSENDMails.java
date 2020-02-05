package serversendmails;

import Mail.SendMail;
import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ssh.connect_server;

public class ServerSENDMails {

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(1888), 0);
            HttpContext cc = server.createContext("/SendALLEmails", new SendALLEmails());
            server.createContext("/resetConnect", new resetCONNECT());
;            cc.setAuthenticator(new BasicAuthenticator("test") {
                @Override
                public boolean checkCredentials(String user, String pwd) {
                    System.out.println("pwd = " + pwd);
                    return user.equals("0631234579") && pwd.equals("kfvth20cdn");
                }
            });
            server.setExecutor(null); // creates a default executor
            server.start();
        } catch (IOException ex) {
            Logger.getLogger(ServerSENDMails.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static class SendALLEmails implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            System.out.println(httpExchange.getRequestMethod());
            System.out.println("модификация действий ");
            Object object = null;
            JSONParser jsonParser = new JSONParser();
            httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            if (httpExchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                httpExchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                httpExchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                httpExchange.sendResponseHeaders(204, -1);
                return;
            }
            if (httpExchange.getRequestMethod().equals("GET")) {
                //Метод в ГЕТЕ не работает
                JSONObject temerror = new JSONObject();
                temerror.clear();
                temerror.put("status", "403");
                temerror.put("comment", "Доступ запрещен");
                temerror.put("detail", "Этот метод доступен в POST запросе");
                writeResponse(httpExchange, temerror.toString());
            } else {
                //Читаем body что присылают
                InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody());
                BufferedReader br = new BufferedReader(isr);
                int b;
                StringBuilder buf = new StringBuilder(512);
                while ((b = br.read()) != -1) {
                    buf.append((char) b);
                }
                br.close();
                isr.close();
                System.out.println("buffer reader = " + buf);
                //Перводим буфер ридер в стринг
                String string_to_json = buf.toString();
                System.out.println("string to json = " + string_to_json);
                try {
                    object = jsonParser.parse(string_to_json);
                } catch (ParseException ex) {
                    Logger.getLogger(ServerSENDMails.class.getName()).log(Level.SEVERE, null, ex);
                }

                //Сохранение в базу новых
                JSONObject idforupdate = (JSONObject) object;
                System.out.println("id for update = " + idforupdate);
                JSONArray from = (JSONArray) idforupdate.get("from");
                System.out.println("from = " + from);
                JSONArray to = (JSONArray) idforupdate.get("to");
                System.out.println("to = " + to);
                String pattern = String.valueOf(idforupdate.get("pattern"));
                System.out.println("pattern = " + pattern);

                //цикл от кого
                JSONObject myres = null;
                for (int a = 0; a < from.size(); a++) {
                    JSONObject from_one = (JSONObject) from.get(a);
                    System.out.println("from one = " + from_one);
                    for (int c = 0; c < to.size(); c++) {
                        JSONObject to_one = (JSONObject) to.get(c);
                        System.out.println("to one = " + to_one);
                        String login = String.valueOf(from_one.get("login"));
                        String password = String.valueOf(from_one.get("password"));
                        System.out.println("login = " + login);
                        System.out.println("password = " + password);
                        String mail_to = String.valueOf(to_one.get("recipient"));
                        System.out.println("mail to = " + mail_to);
                        SendMail sm = new SendMail(login, password, pattern, pattern, mail_to);
                        try {
                            sm.run();
                            connect_server cs = new connect_server();
                            myres = cs.GetResult();
                            System.out.println("myres = " + myres);
                        } catch (Exception ex) {
                            System.out.println("ex = " + ex);
                        }
                    }

                }
                writeResponse(httpExchange, myres.toString());
            }
        }
    }

    static class resetCONNECT implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            System.out.println(httpExchange.getRequestMethod());
            System.out.println("модификация действий ");
            Object object = null;
            JSONParser jsonParser = new JSONParser();
            httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            if (httpExchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                httpExchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                httpExchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                httpExchange.sendResponseHeaders(204, -1);
                return;
            }
            if (httpExchange.getRequestMethod().equals("GET")) {
                //Метод в ГЕТЕ не работает
                JSONObject temerror = new JSONObject();
                temerror.clear();
                temerror.put("status", "403");
                temerror.put("comment", "Доступ запрещен");
                temerror.put("detail", "Этот метод доступен в POST запросе");
                writeResponse(httpExchange, temerror.toString());
            } else {
                //цикл от кого
                connect_server cs = new connect_server();
                JSONObject myres = cs.GetResult();
                writeResponse(httpExchange, myres.toString());
            }
        }
    }

    public static void writeResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, 0);
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

}
