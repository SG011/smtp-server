package com.smtp.server;
import java.util.ArrayList;
import java.util.List;

public class SmtpSession {
    private String clientDomain;
    private String sender;
    private final List<String> recipients = new ArrayList<>();
    private final StringBuilder body = new StringBuilder();
    private boolean bodyComplete = false;

    public void setClientDomain(String d) { this.clientDomain = d; }
    public String getClientDomain() { return clientDomain; }
    public void setSender(String s) { this.sender = s; }
    public String getSender() { return sender; }
    public void addRecipient(String r) { recipients.add(r); }
    public List<String> getRecipients() { return List.copyOf(recipients); }
    public void appendBody(String line) { body.append(line).append("\r\n"); }
    public String getBody() { return body.toString(); }
    public void setBodyComplete(boolean v) { this.bodyComplete = v; }

    public boolean isComplete() {
        return sender != null && !recipients.isEmpty() && bodyComplete;
    }

    public void reset() {
        clientDomain = null;
        sender = null;
        recipients.clear();
        body.setLength(0);
        bodyComplete = false;
    }
}
