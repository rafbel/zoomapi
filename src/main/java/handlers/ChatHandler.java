package handlers;

import exceptions.InvalidArgumentException;
import exceptions.InvalidComponentException;
import interfaces.MemberCallbackInterface;
import interfaces.MessageCallbackInterface;
import interfaces.MessageInterface;
import models.Message;
import services.ChatService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatHandler {

    private ChatService chatService;
    private static final int CALL_RATE = 30;

    public ChatHandler() {
        this.chatService = new ChatService();
    }

    public void onNewMessage(String channelName, MessageCallbackInterface callback) {

    }

    public void onMessageUpdate(String channelName, MessageCallbackInterface callback) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        CopyOnWriteArrayList<Message> messages = new CopyOnWriteArrayList<>();
        executorService.scheduleAtFixedRate(() -> {
            LocalDate toDate = LocalDate.now();
            LocalDate fromDate = toDate.minusDays(5);
            try {
                List<Message> retrievedMessages = this.chatService.history(channelName, fromDate, toDate);

                for (Message retrievedMessage : retrievedMessages) {
                    int matchedMessageId = findMessageById(messages, retrievedMessage.getId());
                    if (matchedMessageId == -1) { // if message doesnt exist, adds it to the list
                        messages.add(retrievedMessage);
                    } else if (!messages.get(matchedMessageId).getMessage().equals(retrievedMessage.getMessage())) { // if content of the message was changed
                        callback.call(retrievedMessage);
                        messages.set(matchedMessageId, retrievedMessage);
                    }
                }

            } catch (InvalidComponentException | InvalidArgumentException exception) {
                exception.printStackTrace();
            }
        }, 0, CALL_RATE, TimeUnit.SECONDS);
    }

    public void onNewMember(String channelName, MemberCallbackInterface callback) {

    }

    private int findMessageById(CopyOnWriteArrayList<Message> messages, String messageId) {
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getId().equals(messageId)) {
                return i;
            }
        }

        return -1;
    }

}