package com.smtp.server;

import com.smtp.queue.MailQueue;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class SmtpServerHandler extends SimpleChannelInboundHandler<String> {

    private final MailQueue queue;
    private SmtpSession session;
    private boolean inData = false;

    public SmtpServerHandler(MailQueue queue) {
        this.queue = queue;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        session = new SmtpSession();
        ctx.writeAndFlush("220 smtp-server ESMTP ready\r\n");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String rawLine) {
        // Strip trailing CRLF — present when called from EmbeddedChannel directly;
        // absent when DelimiterBasedFrameDecoder strips it in the real pipeline.
        String line = rawLine.replaceAll("[\r\n]+$", "");

        if (inData) {
            if (line.equals(".")) {
                inData = false;
                session.setBodyComplete(true);
                queue.enqueue(session);
                ctx.writeAndFlush("250 Message accepted\r\n");
                session.reset();
            } else {
                // dot-unstuffing: leading ".." → "."
                session.appendBody(line.startsWith("..") ? line.substring(1) : line);
            }
            return;
        }

        String upper = line.toUpperCase();

        if (upper.startsWith("EHLO") || upper.startsWith("HELO")) {
            String[] parts = line.split(" ", 2);
            session.setClientDomain(parts.length > 1 ? parts[1].trim() : "");
            ctx.writeAndFlush("250-smtp-server\r\n250 OK\r\n");
        } else if (upper.startsWith("MAIL FROM:")) {
            String sender = line.replaceAll("(?i)MAIL FROM:<(.+)>", "$1").trim();
            session.setSender(sender);
            ctx.writeAndFlush("250 OK\r\n");
        } else if (upper.startsWith("RCPT TO:")) {
            String recipient = line.replaceAll("(?i)RCPT TO:<(.+)>", "$1").trim();
            session.addRecipient(recipient);
            ctx.writeAndFlush("250 OK\r\n");
        } else if (upper.equals("DATA")) {
            inData = true;
            ctx.writeAndFlush("354 Start mail input; end with <CRLF>.<CRLF>\r\n");
        } else if (upper.equals("QUIT")) {
            ctx.writeAndFlush("221 Bye\r\n");
            ctx.close();
        } else {
            ctx.writeAndFlush("500 Command not recognized\r\n");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.writeAndFlush("421 Service not available\r\n");
        ctx.close();
    }
}
