package com.jrnspark.whatsappcustom.message;

public class MessageObject {
    public String senderId,receiverId,message,messageId;

    public MessageObject(String senderId, String receiverId, String message,String messageId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }
    public String getMessageId(){
        return messageId;
    }

    public String getMessage() {
        return message;
    }
}
