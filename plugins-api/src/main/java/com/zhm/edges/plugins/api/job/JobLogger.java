package com.zhm.edges.plugins.api.job;

import org.slf4j.Marker;

public interface JobLogger {

    // ----------Logger stuff BEGIN --------------------------

    /**
     * Log a message at the INFO level according to the specified format and arguments. This form
     * avoids superfluous string concatenation when the logger is disabled for the INFO level.
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    void info(String format, Object... arguments);

    /**
     * Log an exception (throwable) at the INFO level with an accompanying message.
     *
     * @param message   the message accompanying the exception
     * @param exception the exception (throwable) to log
     */
    void info(String message, Throwable exception);

    /**
     * Log a message with the specific Marker at the INFO level.
     *
     * @param marker the marker specific to this log statement
     * @param msg    the message string to be logged
     */
    void info(Marker marker, String msg);

    /**
     * Log a message at the INFO level according to the specified format and arguments. This form
     * avoids superfluous object creation when the logger is disabled for the INFO level.
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    void info(String format, Object arg1, Object arg2);

    /**
     * Log a message at the INFO level according to the specified format and argument. This form
     * avoids superfluous object creation when the logger is disabled for the INFO level.
     *
     * @param format the format string
     * @param arg    the argument
     */
    void info(String format, Object arg);

    /**
     * Log a message at the INFO level.
     *
     * @param msg the message string to be logged
     */
    void info(String msg);

    /**
     * Log an exception (throwable) at the INFO level with an accompanying message and marker.
     *
     * @param marker the marker data for this log statement
     * @param msg    the message accompanying the exception
     * @param t      the exception (throwable) to log
     */
    void info(Marker marker, String msg, Throwable t);

    /**
     * Log a message with the specific Marker at the INFO level according to the specified format and
     * arguments.
     *
     * @param marker    the marker data specific to this log statement
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    void info(Marker marker, String format, Object... arguments);

    /**
     * Log a message with the specific Marker at the INFO level according to the specified format and
     * arguments.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    void info(Marker marker, String format, Object arg1, Object arg2);

    /**
     * Log a message with the specific Marker at the INFO level according to the specified format and
     * argument.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg    the argument
     */
    void info(Marker marker, String format, Object arg);

    void info(LoggerType loggerType);

    void info(LoggerType loggerType, Object... arguments);


    /**
     * Log a message at the DEBUG level according to the specified format and arguments. This form
     * avoids superfluous string concatenation when the logger is disabled for the DEBUG level.
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    void debug(String format, Object... arguments);

    /**
     * Log an exception (throwable) at the DEBUG level with an accompanying message.
     *
     * @param message   the message accompanying the exception
     * @param exception the exception (throwable) to log
     */
    void debug(String message, Throwable exception);

    /**
     * Log a message with the specific Marker at the DEBUG level.
     *
     * @param marker the marker specific to this log statement
     * @param msg    the message string to be logged
     */
    void debug(Marker marker, String msg);

    /**
     * Log a message at the DEBUG level according to the specified format and arguments. This form
     * avoids superfluous object creation when the logger is disabled for the DEBUG level.
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    void debug(String format, Object arg1, Object arg2);

    /**
     * Log a message at the DEBUG level according to the specified format and argument. This form
     * avoids superfluous object creation when the logger is disabled for the DEBUG level.
     *
     * @param format the format string
     * @param arg    the argument
     */
    void debug(String format, Object arg);

    /**
     * Log a message at the DEBUG level.
     *
     * @param msg the message string to be logged
     */
    void debug(String msg);

    /**
     * Log an exception (throwable) at the DEBUG level with an accompanying message and marker.
     *
     * @param marker the marker data for this log statement
     * @param msg    the message accompanying the exception
     * @param t      the exception (throwable) to log
     */
    void debug(Marker marker, String msg, Throwable t);

    /**
     * Log a message with the specific Marker at the DEBUG level according to the specified format and
     * arguments.
     *
     * @param marker    the marker data specific to this log statement
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    void debug(Marker marker, String format, Object... arguments);

    /**
     * Log a message with the specific Marker at the DEBUG level according to the specified format and
     * arguments.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    void debug(Marker marker, String format, Object arg1, Object arg2);

    /**
     * Log a message with the specific Marker at the DEBUG level according to the specified format and
     * argument.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg    the argument
     */
    void debug(Marker marker, String format, Object arg);

    void debug(LoggerType loggerType);

    void debug(LoggerType loggerType, Object... arguments);

    /**
     * Log a message at the ERROR level according to the specified format and arguments. This form
     * avoids superfluous string concatenation when the logger is disabled for the ERROR level.
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    void error(String format, Object... arguments);

    /**
     * Log an exception (throwable) at the ERROR level with an accompanying message.
     *
     * @param message   the message accompanying the exception
     * @param exception the exception (throwable) to log
     */
    void error(String message, Throwable exception);

    /**
     * Log a message with the specific Marker at the ERROR level.
     *
     * @param marker the marker specific to this log statement
     * @param msg    the message string to be logged
     */
    void error(Marker marker, String msg);

    /**
     * Log a message at the ERROR level according to the specified format and arguments. This form
     * avoids superfluous object creation when the logger is disabled for the ERROR level.
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    void error(String format, Object arg1, Object arg2);

    /**
     * Log a message at the ERROR level according to the specified format and argument. This form
     * avoids superfluous object creation when the logger is disabled for the ERROR level.
     *
     * @param format the format string
     * @param arg    the argument
     */
    void error(String format, Object arg);

    /**
     * Log a message at the ERROR level.
     *
     * @param msg the message string to be logged
     */
    void error(String msg);

    /**
     * Log an exception (throwable) at the ERROR level with an accompanying message and marker.
     *
     * @param marker the marker data for this log statement
     * @param msg    the message accompanying the exception
     * @param t      the exception (throwable) to log
     */
    void error(Marker marker, String msg, Throwable t);

    /**
     * Log a message with the specific Marker at the ERROR level according to the specified format and
     * arguments.
     *
     * @param marker    the marker data specific to this log statement
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    void error(Marker marker, String format, Object... arguments);

    /**
     * Log a message with the specific Marker at the ERROR level according to the specified format and
     * arguments.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    void error(Marker marker, String format, Object arg1, Object arg2);

    /**
     * Log a message with the specific Marker at the ERROR level according to the specified format and
     * argument.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg    the argument
     */
    void error(Marker marker, String format, Object arg);

    void error(LoggerType loggerType);

    void error(LoggerType loggerType, Object... arguments);

    /**
     * Log a message at the WARN level according to the specified format and arguments. This form
     * avoids superfluous string concatenation when the logger is disabled for the WARN level.
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    void warn(String format, Object... arguments);

    /**
     * Log an exception (throwable) at the WARN level with an accompanying message.
     *
     * @param message   the message accompanying the exception
     * @param exception the exception (throwable) to log
     */
    void warn(String message, Throwable exception);

    /**
     * Log a message with the specific Marker at the WARN level.
     *
     * @param marker the marker specific to this log statement
     * @param msg    the message string to be logged
     */
    void warn(Marker marker, String msg);

    /**
     * Log a message at the WARN level according to the specified format and arguments. This form
     * avoids superfluous object creation when the logger is disabled for the WARN level.
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    void warn(String format, Object arg1, Object arg2);

    /**
     * Log a message at the WARN level according to the specified format and argument. This form
     * avoids superfluous object creation when the logger is disabled for the WARN level.
     *
     * @param format the format string
     * @param arg    the argument
     */
    void warn(String format, Object arg);

    /**
     * Log a message at the WARN level.
     *
     * @param msg the message string to be logged
     */
    void warn(String msg);

    /**
     * Log an exception (throwable) at the WARN level with an accompanying message and marker.
     *
     * @param marker the marker data for this log statement
     * @param msg    the message accompanying the exception
     * @param t      the exception (throwable) to log
     */
    void warn(Marker marker, String msg, Throwable t);

    /**
     * Log a message with the specific Marker at the WARN level according to the specified format and
     * arguments.
     *
     * @param marker    the marker data specific to this log statement
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    void warn(Marker marker, String format, Object... arguments);

    /**
     * Log a message with the specific Marker at the WARN level according to the specified format and
     * arguments.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    void warn(Marker marker, String format, Object arg1, Object arg2);

    /**
     * Log a message with the specific Marker at the WARN level according to the specified format and
     * argument.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg    the argument
     */
    void warn(Marker marker, String format, Object arg);

    void warn(LoggerType loggerType);

    void warn(LoggerType loggerType, Object... arguments);

    void done();

    // ----------Logger stuff END --------------------------

}
