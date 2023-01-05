import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    static String host = "elasticsearch:";
//    static String host = "localhost:";


    public static void main(String[] args) {
        System.out.println("My app started");
        if (args.length == 0) {
            System.out.println("Command not found");
            return;
        }
        String text = "", command = "";
        Map<String, Commandable> commands = new HashMap<>() ;
        commands.put("add", Main::add);
        commands.put("search", Main::search);

//        Scanner in = new Scanner(System.in);
//        System.out.print("Input a number: ");
//        int num = in.nextInt();

        for (int i = 0; i < args.length - 1; i++) {
            switch (args[i]) {
                case "-s":
                    text = args[++i];
                    break;
                case "-e":
                    if (commands.containsKey(args[i + 1])) {
                        command = args[++i];
                        operation(commands.get(command), text);
                    }
                    else {
                        System.out.println("Command not found");
                        return;
                    }
                    break;
            }
        }
    }

    static void operation(Commandable command, String text) {
        HttpURLConnection connection = null;
        try {
            StringBuilder content = new StringBuilder();
            ConnectionsJson connectionsJson = command.execute(text);
            connection = connectionsJson.connection;
            String json = connectionsJson.json;

            byte[] jsonByte = json.getBytes("UTF-8");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", String.valueOf(jsonByte.length));
            connection.setDoOutput(true);

            try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
                out.write(jsonByte);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println(1);
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                String line;
                content = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println(2);
            }

            System.out.println(CorrectJson(content.toString()));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(3);
        } finally {
            connection.disconnect();
        }
    }

    static ConnectionsJson add(String text) {
        try {
            URL url = new URL("http://" + host + "9200/test/_doc/" + LocalDateTime.now());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            String json = "{\"text\": \"" + text + "\"}";
            return new ConnectionsJson(connection, json);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(4);
        }
        return null;
    }

    static ConnectionsJson search(String text) {
        try {
            URL url = new URL("http://" + host + "9200/test/_search");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            String json = "{\"query\": {" +
                            "\"match\": {" +
                            "\"text\": {" +
                            "\"query\": \"" + text + "\"," +
                            "\"operator\": \"and\"," +
                            "\"fuzziness\": 1}}}}";
            return new ConnectionsJson(connection, json);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.out.println(5);
        }
        return null;
    }

    static String CorrectJson(String json) {
        return json.replaceAll("\n", "")
                .replace(",", ",\n")
                .replace("}", "\n}")
                .replaceAll(":\\{", ":\n{");
    }
}