package org.codered.neolithic.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;

public class ConfigReader {

    private static final String CONFIG_FILE_PATH = "src/main/resources/config.json";

    public String getOpenAiToken() {
        try (FileReader reader = new FileReader(CONFIG_FILE_PATH)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            // Assuming the structure is like {"api": {"openai": {"token": "your_openai_token_here"}}}
            if (jsonObject.has("api")) {
                JsonObject apiObject = jsonObject.getAsJsonObject("api");

                if (apiObject.has("openai")) {
                    JsonObject openaiObject = apiObject.getAsJsonObject("openai");

                    if (openaiObject.has("token")) {
                        return openaiObject.get("token").getAsString();
                    } else {
                        // Handle the case where "token" is not found
                        System.err.println("Error: 'token' not found in the 'openai' object.");
                    }
                } else {
                    // Handle the case where "openai" is not found
                    System.err.println("Error: 'openai' object not found in the 'api' object.");
                }
            } else {
                // Handle the case where "api" is not found
                System.err.println("Error: 'api' object not found in the JSON file.");
            }
        } catch (Exception e) {
            // Handle any exceptions that might occur during file reading or JSON parsing
            e.printStackTrace();
        }

        return null; // Return null in case of failure
    }

}
