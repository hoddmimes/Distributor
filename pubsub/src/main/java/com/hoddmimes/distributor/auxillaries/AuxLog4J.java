package com.hoddmimes.distributor.auxillaries;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

public class AuxLog4J {
    public static void initialize(String pLogfile, boolean pConsole) {
        ConfigurationBuilder<BuiltConfiguration> tBuilder = ConfigurationBuilderFactory.newConfigurationBuilder();

        tBuilder.setStatusLevel(Level.INFO);
        tBuilder.setConfigurationName("AuxLog4J");
        // Add File appender
        if (pLogfile != null) {
            AppenderComponentBuilder tLogfile = tBuilder.newAppender("logfile", "File");
            tLogfile.addAttribute("fileName", pLogfile);

            // Add layout for Logfile
            LayoutComponentBuilder tFileLayout = tBuilder.newLayout("PatternLayout");
            tFileLayout.addAttribute("pattern", "%d{HH:mm:ss.SSS} %C{1} %-5level  - %msg%n");
            tLogfile.add(tFileLayout);
            tBuilder.add(tLogfile);
        }

        // Add stdout appender
        if (pConsole) {
            AppenderComponentBuilder tConsole = tBuilder.newAppender("stdout", "Console");

            // Add layout for Console
            LayoutComponentBuilder tConsoleLayout = tBuilder.newLayout("PatternLayout");
            tConsoleLayout.addAttribute("pattern", "%d{HH:mm:ss.SSS} %C{1} %-5level  - %msg%n");
            tConsole.add(tConsoleLayout);
            tBuilder.add(tConsole);
        }

        RootLoggerComponentBuilder tRootLogger = tBuilder.newRootLogger(Level.TRACE);
        if (pConsole) {
            tRootLogger.add(tBuilder.newAppenderRef("stdout"));
        }

        if (pLogfile != null) {
            tRootLogger.add(tBuilder.newAppenderRef("logfile"));
        }

        if ((pLogfile != null) || (pConsole)) {
            tBuilder.add(tRootLogger);
            Configurator.initialize(tBuilder.build());
        }
    }
}
