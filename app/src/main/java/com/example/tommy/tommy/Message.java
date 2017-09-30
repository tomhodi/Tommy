package com.example.tommy.tommy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class providing message structure for sending and receiving requests from the server.
 * A message consists of a type and optionally a value.
 */

public class Message {

    private static final Pattern msgPattern = Pattern.compile("<\\s*(.*?)\\s*:\\s*(.*?)\\s*>");
    private static final String msgFormat = "<%s:%s>";

    private MessageType type;
    private String value;

    /**
     * Parses a message given in {@code '<KEY:VALUE>'} format.
     */
    public Message(String msg) {
        Matcher m = msgPattern.matcher(msg);
        if (m.matches()) {
            try {
                this.type = MessageType.parse(m.group(1));
            } catch (IllegalArgumentException e) {
                this.type = MessageType.UNKNOWN;
            }
            this.value = m.group(2);
        } else {
            this.type = MessageType.QUERY;
            this.value = msg;
        }
    }

    public Message(String type, String value) {
        try {
            this.type = MessageType.parse(type);
        } catch (IllegalArgumentException e) {
            this.type = MessageType.UNKNOWN;
        }
        this.value = value;
    }

    public Message(MessageType type, String value) {
        this.type = type;
        this.value = value;
    }

    public Message(MessageType type) {
        this(type, "");
    }

    public MessageType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    /**
     * Returns the message in {@code '<KEY:VALUE>'} format.
     */
    public String build() {
        return String.format(msgFormat, type, value);
    }

    public enum MessageType {
        UNKNOWN,
        QUERY,
        USERNAME,
        UPDATE_USER_INFO,
        OPEN_MY_PROFILE,
        OPEN_SETTINGS,
        LOG_OUT;

        private static MessageType parse(String str) throws IllegalArgumentException {
            return MessageType.valueOf(str.toUpperCase());
        }
    }
}
