package com.heroku.java;

import java.io.*;
import java.net.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;

@SpringBootApplication
@Controller
public class App {
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) throws IOException {
        
        URL proxyUrl = new URL(System.getenv("QUOTAGUARDSTATIC_URL"));
        logger.debug("###" + proxyUrl + "###");
        String userInfo = proxyUrl.getUserInfo();
        String user = userInfo.substring(0, userInfo.indexOf(':'));
        String password = userInfo.substring(userInfo.indexOf(':') + 1);

        URLConnection conn = null;
        System.setProperty("http.proxyHost", proxyUrl.getHost());
        System.setProperty("http.proxyPort", Integer.toString(proxyUrl.getPort()));

        Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, password.toCharArray());
                }
            });

        URL url = new URL("http://ip.quotaguard.com");
        conn = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null)
            System.out.println(inputLine);
        in.close();
        

        /*
        String proxyUrl = System.getenv("QUOTAGUARDSTATIC_URL");
        try {
            URI uri = new URI(proxyUrl);
            String proxyHost = uri.getHost();
            int proxyPort = uri.getPort();
            String userInfo = uri.getUserInfo();

            if (userInfo != null) {
                String[] credentials = userInfo.split(":");
                String username = credentials[0];
                String password = credentials[1];

                // Set proxy authentication
                Authenticator.setDefault(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password.toCharArray());
                    }
                });
            }

            // Set system properties for HTTP and HTTPS proxies
            System.setProperty("http.proxyHost", proxyHost);
            System.setProperty("http.proxyPort", String.valueOf(proxyPort));
            System.setProperty("https.proxyHost", proxyHost);
            System.setProperty("https.proxyPort", String.valueOf(proxyPort));

            logger.info("Proxy configured successfully: " + proxyHost + ":" + proxyPort);
        } catch (URISyntaxException e) {
            logger.error("Invalid proxy URL: " + proxyUrl, e);
        }
        */

        SpringApplication.run(App.class, args);
    }
}
