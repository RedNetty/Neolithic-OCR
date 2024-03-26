package org.codered.neolithic.openai;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import org.codered.neolithic.Neolithic;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class OpenAIHandler {

    private final OpenAiService service;
    private final AIRequest originalRequest;
    private JFrame chatFrame;
    private JTextPane chatTextPane;
    private JTextField userInputField;
    private JButton sendButton;
    private JLabel loadingLabel;

    public OpenAIHandler(AIRequest originalRequest) {
        String openAiToken = Neolithic.getConfigReader().getOpenAiToken();
        this.service = new OpenAiService(openAiToken, Duration.ofHours(2));
        this.originalRequest = originalRequest;
        initializeChatFrame();
    }

    public OpenAiService getService() {
        return service;
    }

    public void startChat() {
        chatFrame.setVisible(true);
    }

    private void initializeChatFrame() {
        chatFrame = new JFrame("Chat with AI");
        chatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatFrame.setSize(600, 400);
        chatFrame.setLayout(new BorderLayout());

        chatTextPane = new JTextPane();
        chatTextPane.setEditable(false);
        chatTextPane.setBackground(Color.DARK_GRAY);
        chatTextPane.setForeground(Color.WHITE);
        chatTextPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(chatTextPane);
        chatFrame.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        userInputField = new JTextField();
        sendButton = new JButton("Send");

        sendButton.addActionListener(e -> processUserInput());

        inputPanel.add(userInputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        loadingLabel = new JLabel("Loading AI response...");
        loadingLabel.setHorizontalAlignment(JLabel.CENTER);
        loadingLabel.setForeground(Color.BLUE);
        loadingLabel.setVisible(false);

        inputPanel.add(loadingLabel, BorderLayout.NORTH);

        chatFrame.add(inputPanel, BorderLayout.SOUTH);

        appendMessage("You", originalRequest.getInstructions() + " " + originalRequest.getConvertedText(), Color.LIGHT_GRAY);
        List<ChatMessage> initialMessages = new ArrayList<>();
        initialMessages.add(new ChatMessage(ChatMessageRole.USER.value(), originalRequest.getInstructions() + " " + originalRequest.getConvertedText()));
        performStreamedChat(initialMessages);
    }

    private void processUserInput() {
        String userMessage = userInputField.getText().trim();
        if (!userMessage.isEmpty()) {
            appendMessage("You", userMessage, Color.LIGHT_GRAY);
            userInputField.setText("");

            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "You are a chat assistant.");
            messages.add(systemMessage);
            messages.add(new ChatMessage(ChatMessageRole.USER.value(), userMessage));

            performStreamedChat(messages);
        }
    }

    private void performStreamedChat(List<ChatMessage> messages) {
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                try {
                    userInputField.setEnabled(false);
                    loadingLabel.setVisible(true);

                    messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), originalRequest.getInstructions() + ": " + originalRequest.getConvertedText()));

                    ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                            .model("gpt-4-turbo-preview")
                            .messages(messages)
                            .maxTokens(2000)
                            .temperature(0.5)
                            .build();

                    return service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage().getContent();
                } catch (Exception e) {
                    e.printStackTrace();
                    showErrorDialog("An error occurred while communicating with the OpenAI API.");
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    String aiResponse = get();

                    if (aiResponse != null) {
                        appendMessage("AI", aiResponse, Color.ORANGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    userInputField.setEnabled(true);
                    loadingLabel.setVisible(false);
                }
            }
        };

        worker.execute();
    }

    private void appendMessage(String sender, String message, Color color) {
        StyledDocument doc = chatTextPane.getStyledDocument();
        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setForeground(set, color);

        try {
            doc.insertString(doc.getLength(), "\n" + sender + ": " + message + "\n", set);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        chatTextPane.setCaretPosition(doc.getLength());
    }

    private void showErrorDialog(String errorMessage) {
        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
}