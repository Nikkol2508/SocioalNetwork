package application.service;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rewrite.RewritePolicy;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.StringFormattedMessage;

@Plugin(name = "LogRewritePolicy", category = "Core", elementType = "rewritePolicy", printObject = true)
public final class LogRewritePolicy implements RewritePolicy {

    @Override
    public LogEvent rewrite(final LogEvent event) {
        try {
            String formattedMessage = event.getMessage().getFormattedMessage();

            Log4jLogEvent.Builder builder = new Log4jLogEvent.Builder(event);
            formattedMessage = formattedMessage.replaceAll("phone=[\\d]{8}", "phone=********");
            formattedMessage = formattedMessage.replaceAll("email=[A-Za-z0-9._%+-]+@", "email=****@");
            formattedMessage = formattedMessage.replaceAll("password=[\\S]+,", "password=*******,");
            formattedMessage = formattedMessage.replaceAll("lastName=[A-Za-zА-Яа-яё]+,", "lastName=******");
            formattedMessage = formattedMessage.replaceAll("firstName=[A-Za-zА-Яа-яё]+,", "firstName=******");

            builder.setMessage(new StringFormattedMessage(formattedMessage));

            return builder.build();
        } catch (Throwable t) {
            return event;
        }
    }

    @PluginFactory
    public static LogRewritePolicy createPolicy() {
        return new LogRewritePolicy();
    }
}
