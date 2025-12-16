package org.codered.neolithic.openai;

/**
 * Represents a request to the OpenAI API for language processing.
 * This class encapsulates instructions and the converted text result.
 */
public class AIRequest {

    // Instructions to be processed by the OpenAI API
    private String instructions;

    // The converted text result from the OpenAI API
    private String convertedText;

    /**
     * Constructs an AIRequest with the specified instructions and converted text.
     *
     * @param instructions The instructions to be processed by the OpenAI API.
     * @param convertedText The converted text result from the OpenAI API.
     */
    public AIRequest(String instructions, String convertedText) {
        this.instructions = instructions;
        this.convertedText = convertedText;
    }

    /**
     * Sends the request to the OpenAI API for language processing using an OpenAIHandler.
     * It initializes a new OpenAIHandler with this AIRequest instance and triggers the processing.
     */
    public void sendRequest() {
        OpenAIHandler openAIHandler = new OpenAIHandler(this);
        openAIHandler.startChat();
    }

    /**
     * Gets the converted text result from the OpenAI API.
     *
     * @return The converted text result.
     */
    public String getConvertedText() {
        return convertedText;
    }

    /**
     * Sets the converted text result from the OpenAI API.
     *
     * @param convertedText The converted text result to be set.
     */
    public void setConvertedText(String convertedText) {
        this.convertedText = convertedText;
    }

    /**
     * Gets the instructions to be processed by the OpenAI API.
     *
     * @return The instructions.
     */
    public String getInstructions() {
        return instructions;
    }

    /**
     * Sets the instructions to be processed by the OpenAI API.
     *
     * @param instructions The instructions to be set.
     */
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
