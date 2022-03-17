package net.wechandoit.multichat.groups.groups;

import net.wechandoit.multichat.groups.MessageChannel;
import org.bukkit.World;

public class ShoutChannel extends MessageChannel {
    private World world;

    public ShoutChannel(String name, String chatFormat, boolean isDefault, World world) {
        super(name, chatFormat, false, isDefault);
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }
}
