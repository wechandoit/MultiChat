package net.wechandoit.multichat.groups.groups;

import net.wechandoit.multichat.groups.MessageChannel;

public class WhisperChannel extends MessageChannel {

    public WhisperChannel(String name, String chatFormat, boolean isDefault, double blockRange) {
        super(name, chatFormat, false, isDefault, blockRange);
    }
}
