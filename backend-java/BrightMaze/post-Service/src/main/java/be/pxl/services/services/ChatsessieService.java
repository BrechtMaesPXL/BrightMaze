package be.pxl.services.services;

import be.pxl.services.client.PythonChatbotClient;
import be.pxl.services.domain.Chatsessie;
import be.pxl.services.domain.History;
import be.pxl.services.domain.HistoryItem;
import be.pxl.services.domain.dto.ChatsessieRequest;
import be.pxl.services.domain.dto.ChatsessieResponse;
import be.pxl.services.enums.Role;
import be.pxl.services.exception.BotResponseProcessingException;
import be.pxl.services.exception.ChatSessionNotFoundException;
import be.pxl.services.repository.ChatSessieRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ChatsessieService implements IChatsessieService {

    private final ChatSessieRepository chatsessieRepository;
    private final PythonChatbotClient pythonChatbotClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${python.service.url}")
    private String pythonServiceUrl;

    @Override
    public String startSession() {
        Chatsessie newSession = Chatsessie.builder()
                .history(new History("", List.of()))
                .build();
        Chatsessie savedSession = chatsessieRepository.save(newSession);
        return savedSession.getId();
    }

    private static final Pattern SCRIPT_PATTERN = Pattern.compile(
            "(?i)<script[^>]*+>(?:.|\\R)*?</script>",
            Pattern.DOTALL
    );
    @SneakyThrows
    @Override
    public ChatsessieResponse sendMessage(ChatsessieRequest chatsessieRequest) {
        Optional<Chatsessie> chatsessie = chatsessieRepository.findById(chatsessieRequest.getId());

        if (chatsessie.isPresent()) {
            // VALIDATIE + SANITIZATION
            String originalMessage = chatsessieRequest.getMessage();
            if (originalMessage == null || originalMessage.trim().isEmpty()) {
                throw new IllegalArgumentException("Bericht mag niet leeg zijn.");
            }

            // Strip HTML-tags
            String sanitizedMessage = originalMessage.replaceAll("<[^>]*>", "").trim();

            // Remove <script> blocks safely:
            Matcher m = SCRIPT_PATTERN.matcher(sanitizedMessage);
            sanitizedMessage = m.replaceAll("");

            // Remove dangerous characters
            sanitizedMessage = sanitizedMessage.replaceAll("[\"'`;]", "");

            // Limiteer lengte
            if (sanitizedMessage.length() > 1000) {
                sanitizedMessage = sanitizedMessage.substring(0, 1000);
            }

            // basis XSS-preventie
            sanitizedMessage = sanitizedMessage.replaceAll("(?i)<script.*?>.*?</script>", "");
            sanitizedMessage = sanitizedMessage.replaceAll("[\"'`;]", "");

            // VERWERKING
            History history = chatsessie.get().getHistory();
            List<HistoryItem> historyItems = history.getHistoryItems();

            HistoryItem userHistoryItem = new HistoryItem(Role.USER, sanitizedMessage);
            history.addItem(userHistoryItem);

            Map<String, Object> requestPayload = new HashMap<>();
            requestPayload.put("message", sanitizedMessage);
            requestPayload.put("history", historyItems);

            String botResponseJson = pythonChatbotClient.sendMessageToChatbot(requestPayload);
            HistoryItem assistantResponse = processBotResponse(botResponseJson);
            history.addItem(assistantResponse);

            chatsessieRepository.save(chatsessie.get());

            return new ChatsessieResponse(chatsessieRequest.getId(), history);
        } else {
            throw new ChatSessionNotFoundException(chatsessieRequest.getId());
        }
    }


    private HistoryItem processBotResponse(String botResponseJson) {
        try {
            JsonNode jsonResponse = objectMapper.readTree(botResponseJson);
            String type = cleanJsonValue(jsonResponse.get("type"));
            String botReply = cleanJsonValue(jsonResponse.get("response"));

            ObjectNode contentNode = objectMapper.createObjectNode();
            contentNode.put("response", botReply);

            switch (type) {
                case "route" -> processRouteContent(jsonResponse, contentNode);
                case "events_time_specific" -> processEventsContent(jsonResponse, contentNode);
                default -> {
                }
            }

            return new HistoryItem(Role.ASSISTANT, contentNode.toString(), type);
        } catch (Exception e) {
            throw new BotResponseProcessingException("Error processing bot response", e);
        }
    }

    private void processRouteContent(JsonNode jsonResponse, ObjectNode contentNode) {
        String start = cleanJsonValue(jsonResponse.get("start"));
        String end = cleanJsonValue(jsonResponse.get("end"));
        contentNode.put("start", start);
        contentNode.put("end", end);
    }

    private void processEventsContent(JsonNode jsonResponse, ObjectNode contentNode) {
        String events = cleanJsonValue(jsonResponse.get("events"));
        contentNode.put("events", events);
    }

    private String cleanJsonValue(JsonNode node) {
        if (node == null || node.isNull()) {
            return "";
        }
        String value = node.toString();
        return value.length() > 2 ? value.substring(1, value.length() - 1) : value;
    }

    @Override
    public void endSession() {
        // als we willen verwijderen kunnne we hier zetten
    }
}
