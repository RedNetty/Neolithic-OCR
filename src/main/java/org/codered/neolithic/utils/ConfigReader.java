package org.codered.neolithic.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigReader {

    private static final Logger LOGGER = Logger.getLogger(ConfigReader.class.getName());
    private static final String CONFIG_FILE_PATH = "src/main/resources/config.json";

    public String getOpenAiToken() {
        // Check environment variable first (preferred for production)
        String envToken = System.getenv("OPENAI_API_KEY");
        if (envToken != null && !envToken.isBlank()) {
            return envToken;
        }

        // Fall back to config.json for local development
        try (FileReader reader = new FileReader(CONFIG_FILE_PATH)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

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
            LOGGER.log(Level.SEVERE, "Failed to read config.json", e);
        }

        return null;
    }

}
