package com.stucom.abou.game.model;

import java.io.Serializable;

@SuppressWarnings("unused")
public class Message implements Serializable {

    private int Id;
    private int FromId;
    private String Text;
    private String SentAt;
    private String ReceivedAt;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getFromId() {
        return FromId;
    }

    public void setFromId(int fromId) {
        FromId = fromId;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        this.Text = text;
    }

    public String getSentAt() {
        return SentAt != null ? SentAt.split("T")[0] : null;
    }

    public void setSentAt(String sentAt) {
        SentAt = sentAt;
    }

    public String getReceivedAt() {
        return ReceivedAt != null ? ReceivedAt.split("T")[0] :  null;
    }

    public void setReceivedAt(String receivedAt) {
        ReceivedAt = receivedAt;
    }
}
