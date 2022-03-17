package net.wechandoit.multichat.groups;

public class MessageChannel {
    private String name;
    private String chatFormat;
    private boolean isDefault = false;
    private boolean isGlobal;
    private double blockRange = 0; // 0 to ignore blockRange

    public MessageChannel(String name, String chatFormat) {
        this.name = name;
        this.chatFormat = chatFormat;
    }
    public MessageChannel(String name, String chatFormat, boolean isGlobal) {
        this.name = name;
        this.chatFormat = chatFormat;
        this.isGlobal = isGlobal;
    }

    public MessageChannel(String name, String chatFormat, boolean isGlobal, boolean isDefault) {
        this.name = name;
        this.chatFormat = chatFormat;
        this.isGlobal = isGlobal;
        this.isDefault = isDefault;
    }

    public MessageChannel(String name, String chatFormat, boolean isGlobal, boolean isDefault, double blockRange) {
        this.name = name;
        this.chatFormat = chatFormat;
        this.isGlobal = isGlobal;
        this.isDefault = isDefault;
        this.blockRange = blockRange;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChatFormat() {
        return chatFormat;
    }

    public void setChatFormat(String chatFormat) {
        this.chatFormat = chatFormat;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public double getBlockRange() {
        return blockRange;
    }

    public void setBlockRange(double blockRange) {
        this.blockRange = blockRange;
    }

    public boolean hasBlockRange() {
        return blockRange > 0;
    }


}
