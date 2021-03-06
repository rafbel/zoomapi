package listeners;

import exceptions.InvalidArgumentException;
import exceptions.InvalidComponentException;
import models.ChannelMember;
import models.Message;
import services.ChatService;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ChatListener extends Listener {

    private ChatService chatService;

    public ChatListener(ChatService chatService) {
        super();
        this.chatService = chatService;
    }

    public void onNewMessage(String channelName, Consumer<Message> callback) {
        AtomicReference<Long> latestTimeStamp = new AtomicReference<>(0L);
        registerEvent(() -> {
            try {
                LocalDate date = LocalDate.now(ZoneOffset.UTC);
                List<Message> retrievedMessages = this.chatService.history(channelName, date, date);

                if (latestTimeStamp.get() != 0) {
                    for(Message retrievedMessage: retrievedMessages) {
                        Long messageTimeStamp = retrievedMessage.getTimestamp();

                        if(latestTimeStamp.get() < messageTimeStamp) {
                            callback.accept(retrievedMessage);
                        }
                    }
                }

                if (!retrievedMessages.isEmpty()) {
                    latestTimeStamp.set(retrievedMessages.get(retrievedMessages.size() - 1).getTimestamp());
                }

            } catch (InvalidComponentException | InvalidArgumentException exception) {
                exception.printStackTrace();
            }
        });
    }

    public void onMessageUpdate(String channelName, Consumer<Message> callback) {
        List<Message> messages = new ArrayList<>();
        registerEvent(() -> {
            LocalDate toDate = LocalDate.now(ZoneOffset.UTC);
            LocalDate fromDate = toDate.minusDays(5);
            try {
                List<Message> retrievedMessages = this.chatService.history(channelName, fromDate, toDate);

                for (Message retrievedMessage : retrievedMessages) {
                    int matchedMessageId = findMessageById(messages, retrievedMessage.getId());
                    if (matchedMessageId == -1) { // if message doesnt exist locally, adds it to the list
                        messages.add(retrievedMessage);
                    } else if (!messages.get(matchedMessageId).getMessage().equals(retrievedMessage.getMessage())) { // if content of the message was changed
                        callback.accept(retrievedMessage);
                        messages.set(matchedMessageId, retrievedMessage);
                    }
                }

            } catch (InvalidComponentException | InvalidArgumentException exception) {
                exception.printStackTrace();
            }
        });
    }

    public void onNewMember(String channelName, Consumer<ChannelMember> callback) {
        List<ChannelMember> members = new ArrayList<>();
        registerEvent(() -> {
            try {
                List<ChannelMember> retrievedMembers = chatService.members(channelName);

                if (members.isEmpty()) {
                    members.addAll(retrievedMembers);
                } else {
                    for (ChannelMember retrievedMember : retrievedMembers) {
                        if (!members.stream().filter(o -> o.getId().equals(retrievedMember.getId())).findFirst().isPresent()) {
                            callback.accept(retrievedMember);
                            members.add(retrievedMember);
                        }
                    }
                }

            } catch (InvalidComponentException | InvalidArgumentException e) {
                e.printStackTrace();
            }
        });
    }

    private int findMessageById(List<Message> messages, String messageId) {
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getId().equals(messageId)) {
                return i;
            }
        }

        return -1;
    }
}
