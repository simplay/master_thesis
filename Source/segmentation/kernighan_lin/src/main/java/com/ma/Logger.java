package com.ma;

import com.sun.jmx.snmp.Timestamp;

import java.util.LinkedList;

/**
 * A logger is responsible for printing the runtime state of a given execution.
 * It prints its received messages into the console (if allowed) and also into
 * a given status report file (to have some reconstructive information).
 */
public class Logger {

    private static Logger instance = null;

    // Unique identifier used for the status filename.
    private String timestamp;

    // Logger messages that should be written to a status file.
    private final LinkedList<String> buffer = new LinkedList<>();

    // Should the logger print the messages to the console.
    private boolean isMuted = false;

    public static Logger getInstance() {
        return getInstance(false);
    }

    public static Logger getInstance(boolean isMuted) {
        if (instance == null) {
            return new Logger(isMuted);
        }
        return instance;
    }

    /**
     * Saves a given string in the internal logger buffer
     * and if not muted it prints it out into the console.
     *
     * @param msg given message string.
     */
    public static void print(String msg) {
        if (!getInstance().mayPrint()) System.out.print(msg);
        getInstance().writeBuffer(msg);
    }

    /**
     * Saves a given string appended by a new line character
     * in the internal logger buffer
     * and if not muted it prints it out into the console.
     *
     * @param msg given message string.
     */
    public static void println(String msg) {
        print(msg + "\n");
    }

    /**
     * Creates a new logger instance
     *
     * @param isMuted should status be printed into console
     */
    public Logger(boolean isMuted) {
        this.isMuted = isMuted;
        timestamp = new Timestamp(System.currentTimeMillis()).toString();
    }

    /**
     * Is the logger allowed to print received messages into the console?
     *
     * @return true if it may print messages, otherwise false.
     */
    public boolean mayPrint() {
        return !isMuted;
    }

    /**
     * Appends a string to the internal buffer.
     * Every message contained in the buffer will written into the output file.
     *
     * @param msg a message that should be added to the buffer.
     */
    public void writeBuffer(String msg) {
        buffer.add(msg);
    }

}
