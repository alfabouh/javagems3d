package ru.alfabouh.engine.system.logger.appenders;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import ru.alfabouh.engine.system.logger.JGemsLogging;

import java.util.StringJoiner;

@Plugin(name = "JGemsConsoleAppender", category = "Core", elementType = "appender", printObject = true)
public class LogAppender extends AbstractAppender {
    protected StringJoiner stringJoiner;

    protected LogAppender(String name, Filter filter, Layout<? extends LogEvent> layout, boolean ignoreExceptions, Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);
    }

    @PluginFactory
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static LogAppender createAppender(@PluginAttribute("name") String name, @PluginAttribute("pattern") String pattern) {
        Layout patternLayout = PatternLayout.newBuilder().withPattern(pattern).build();
        LogAppender appender = new LogAppender(name, null, patternLayout, false, null);
        appender.stringJoiner = new StringJoiner("\n");
        return appender;
    }

    @Override
    public void append(LogEvent event) {
        CharSequence sequence = (CharSequence) getLayout().toSerializable(event);
        this.stringJoiner.add(sequence);
        JGemsLogging.consoleText.add(sequence);
    }
}