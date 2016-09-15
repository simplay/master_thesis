package com.ma;

/**
 * Defines which initial (balanced) set partition should be used,
 * given a certain user input.
 */
public enum InitialPartitionMode {
    MOD_N("mn"),
    MOD_2("m2"),
    EMPTY_FULL("ef"),
    EMPTY_BUT_ONE("ebo"),
    SPLIT_LEFT_RIGHT("slr");

    private String name;

    private InitialPartitionMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static InitialPartitionMode getModeByName(String name) {
        for (InitialPartitionMode mode : values()) {
            if (name.equals(mode.getName())) {
                return mode;
            }
        }
        return null;
    }



}
