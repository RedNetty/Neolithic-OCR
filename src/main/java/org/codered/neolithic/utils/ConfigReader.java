package org.codered.neolithic.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigReader {

    private static final Logger LOGGER = Logger.getLogger(ConfigReader.class.getName());

    public String getOpenAiToken() {
        // Check environment variable first (preferred for production)
        String envToken = System.getenv("OPENAI_API_KEY");
        if (envToken != null && !envToken.isBlank()) {
            return envToken;
        }

        // Fall back to config.json loaded from classpath (works in both IDE and JAR)
        try (InputStream is = ConfigReader.class.getResourceAsStream("/config.json")) {
            if (is == null) {
                LOGGER.warning("config.json not found on classpath. Set the OPENAI_API_KEY environment variable or provide config.json in resources.");
                return null;
            }
            JsonObject jsonObject = JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();

            if (jsonObject.has("api")) {
                JsonObject apiObject = jsonObject.getAsJsonObject("api");

                if (apiObject.has("openai")) {
                    JsonObject openaiObject = apiObject.getAsJsonObject("openai");

                    if (openaiObject.has("token")) {
                        String token = openaiObject.get("token").getAsString();
                        if (token.isBlank() || token.equals("openAI key here")) {
                            LOGGER.warning("OpenAI token is not configured. Set the OPENAI_API_KEY environment variable or update config.json.");
                            return null;
                        }
                        return token;
                    } else {
                        LOGGER.warning("'token' not found in the 'openai' config object.");
                    }
                } else {
                    LOGGER.warning("'openai' object not found in the 'api' config object.");
                }
            } else {
                LOGGER.warning("'api' object not found in config.json.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to read config.json from classpath", e);
        }

        return null;
    }

}
