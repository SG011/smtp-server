package com.smtp.server;
import com.smtp.queue.MailQueue;
import io.netty.channel.*;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.*;
import java.nio.charset.StandardCharsets;

@ChannelHandler.Sharable
public class SmtpChannelInitializer extends ChannelInitializer<Channel> {
    private final MailQueue queue;
    public SmtpChannelInitializer(MailQueue queue) { this.queue = queue; }

    @Override
    protected void initChannel(Channel ch) {
        ch.pipeline()
            .addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
            .addLast(new StringDecoder(StandardCharsets.UTF_8))
            .addLast(new StringEncoder(StandardCharsets.UTF_8))
            .addLast(new SmtpServerHandler(queue));
    }
}
