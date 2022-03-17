package net.wechandoit.multichat.commands;

import com.google.common.base.Joiner;
import net.wechandoit.multichat.Multichat;
import net.wechandoit.multichat.config.ConfigSettings;
import net.wechandoit.multichat.groups.MessageChannel;
import net.wechandoit.multichat.profiles.Profile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class ChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            Profile profile = Multichat.getProfileHandler().getProfile(player);

            if (args.length == 0) {
                profile.sendMessage("<rainbow><bold>Multichat Help");
                profile.sendMessage("<red> Usage: /multichat chatGroups");
                profile.sendMessage("<red> Usage: /multichat switch [chatGroup]");
                profile.sendMessage("<red> Usage: /multichat setProfanityFilter [true/false]");
                return true;
            }

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("chatGroups")) {
                    if (player.isOp() || player.hasPermission("multichat.admin")) {
                        profile.sendMessage("<red>Valid Channels: " + Multichat.getMessageGroupHandler().getAllChannelNames());
                    } else {
                        profile.sendMessage("<red>Valid Channels: " + Multichat.getMessageGroupHandler().getValidChannelNamesForProfile(profile));
                    }
                } else if (args[0].equalsIgnoreCase("help")) {
                    profile.sendMessage("<rainbow><bold>Multichat Help");
                    profile.sendMessage("<red> Usage: /multichat chatGroups");
                    profile.sendMessage("<red> Usage: /multichat switch [chatGroup]");
                    profile.sendMessage("<red> Usage: /multichat setProfanityFilter [true/false]");
                }
                return true;
            }

            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("setProfanityFilter")) {
                    if (args[1].contains("true") && args[1].equalsIgnoreCase("true")) {
                        profile.setProfanityFilter(true);
                        profile.sendMessage("<red>You have enabled the profanity filter.");
                    } else if (args[1].contains("false") && args[1].equalsIgnoreCase("false")) {
                        profile.setProfanityFilter(false);
                        profile.sendMessage("<red>You have disabled the profanity filter.");
                    }
                    Multichat.getProfileHandler().updateInDb(profile);
                } else if (args[0].equalsIgnoreCase("switch")) {
                    if (Multichat.getMessageGroupHandler().isValidChannel(args[1])) {
                        if (Multichat.getMessageGroupHandler().getValidChannelNamesForProfile(profile).contains(args[1])) {
                            profile.setChatGroup(args[1]);
                            profile.sendMessage("<red>You have switched to the " + args[1] + " channel");
                            Multichat.getProfileHandler().updateInDb(profile);
                        } else {
                            profile.sendMessage("<red>You do not have permission to switch to this channel.");
                        }
                    } else {
                        profile.sendMessage("<red>" + args[1] + " is not a valid channel.");
                        profile.sendMessage("<red>Valid Channels: " + Multichat.getMessageGroupHandler().getAllChannelNames());
                    }
                } else if ((player.isOp() || player.hasPermission("multichat.admin")) && args[0].equalsIgnoreCase("delete")) {
                    String name = args[1];
                    if (Multichat.getMessageGroupHandler().get(name) != null) {
                        if (Multichat.getMessageGroupHandler().isChannelDeleteable(name)) {
                            Multichat.getMessageGroupHandler().getMessageGroupList().remove(name);
                            for (Profile p : Multichat.getProfileHandler().getProfileList()) {
                                if (p.getChatGroup().equals(name)) {
                                    p.setChatGroup(ConfigSettings.getDefaultChatGroup);
                                    if (p.getPlayer() != null) {
                                        p.sendMessage("<red>You were sent back to the " + ConfigSettings.getDefaultChatGroup + " channel!");
                                    }
                                }
                            }
                            profile.sendMessage("<red>You have deleted the " + name + " channel!");
                        } else {
                            profile.sendMessage("<red>You cannot delete this channel!");
                        }
                    } else {
                        profile.sendMessage("<red>Channel " + name + " does not exist!");
                    }
                }
                return true;
            }

            if (args.length == 3) {
                if ((player.isOp() || player.hasPermission("multichat.admin")) && args[0].equalsIgnoreCase("setBlockRange")) {
                    try {
                        String name = args[1];
                        if (Multichat.getMessageGroupHandler().get(name) != null) {
                            double blockRange = Double.parseDouble(args[2]);
                            double oldBlockRange = Multichat.getMessageGroupHandler().get(name).getBlockRange();
                            Multichat.getMessageGroupHandler().get(name).setBlockRange(blockRange);
                            profile.sendMessage("<red>" + name + "'s block range changed from " + oldBlockRange + " to " + blockRange);
                        } else {
                            profile.sendMessage("<red>Channel " + name + " is an invalid channel name!");
                        }
                    } catch (NullPointerException | NumberFormatException e) {
                        profile.sendMessage("<red>Usage: /multichat setBlockRange [name] [blockRange]");
                    }
                } else if ((player.isOp() || player.hasPermission("multichat.admin")) && args[0].equalsIgnoreCase("setGlobal")) {
                    try {
                        String name = args[1];
                        if (Multichat.getMessageGroupHandler().get(name) != null) {
                            boolean isGlobal = false;
                            if (args[2].equalsIgnoreCase("true") || args[2].contains("true")) {
                                isGlobal = true;
                            }
                            Multichat.getMessageGroupHandler().get(name).setGlobal(isGlobal);
                            if (isGlobal)
                                profile.sendMessage("<red>" + name + " is now global");
                            else
                                profile.sendMessage("<red>" + name + " is now not global");
                        } else {
                            profile.sendMessage("<red>Channel " + name + " is an invalid channel name!");
                        }
                    } catch (NullPointerException | NumberFormatException e) {
                        profile.sendMessage("<red>Usage: /multichat setBlockRange [name] [blockRange]");
                    }
                }
                return true;
            }

            if (args.length == 4) {
                if (player.isOp() && args[0].equalsIgnoreCase("create")) {
                    try {
                        String name = args[1];
                        if (Multichat.getMessageGroupHandler().get(name) == null) {
                            if (!Multichat.getMessageGroupHandler().isChannelNameBlacklisted(name)) {
                                boolean isGlobal = false;
                                if (args[2].equalsIgnoreCase("true") || args[2].contains("true")) {
                                    isGlobal = true;
                                }
                                double blockRange = Double.parseDouble(args[3]);
                                MessageChannel channel = new MessageChannel(name, ConfigSettings.defaultChatFormat, isGlobal, false, blockRange);
                                Multichat.getMessageGroupHandler().getMessageGroupList().add(channel);
                                Multichat.getMessageGroupHandler().addChannelSettingsToConfig(channel);
                                profile.sendMessage("<green>Added channel " + name);
                            } else {
                                profile.sendMessage("<red>Channel " + name + " is an invalid channel name!");
                            }
                        } else {
                            profile.sendMessage("<red>Channel " + name + " already exists!");
                        }
                    } catch (NullPointerException | NumberFormatException e) {
                        profile.sendMessage("<red>Usage: /multichat create [name] [isGlobal] [blockRange]");
                    }
                }
                return true;

            }
            if ((player.isOp() || player.hasPermission("multichat.admin")) && args[0].equalsIgnoreCase("changeFormat")) {
                if (Multichat.getMessageGroupHandler().isValidChannel(args[1])) {
                    String message = Joiner.on(" ").join(Arrays.copyOfRange(args, 2, args.length));
                    Multichat.getMessageGroupHandler().get(args[1]).setChatFormat(message);
                    profile.sendMessage("<red>Format of " + args[1] + " changed to: " + message);
                }
            }
            return true;
        }

        return false;
    }
}
