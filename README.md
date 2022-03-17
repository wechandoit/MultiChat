# MultiChat
 Paper 1.18.2 Chat plugin

# APIs
- Paper 1.18.2
- MiniMessage (https://docs.adventure.kyori.net/minimessage/api.html)

#Soft Dependencies
- LuckPerms
- PlaceholderAPI

#Commands
- /chat -> help command
- /chat chatGroups -> list chat groups
- /chat help -> help command
- /chat setProfanityFilter [true/false] -> enable/disable your profanity filter
- /chat switch [name] -> switch to channel
- /message|m|msg|whisper [player] [message] -> message a player in a private channel
- /shout [message] -> send a message in the world's shout channel (requires permission: multichat.shout or op)

#Admin Commands
- /chat delete [name] -> delete channel that a player created
- /chat create [name] [isGlobal(true/false)] [blockRange(a number)] -> create a chat channel
- /chat setBlockRange [name] [radius] -> set the block radius of a channel
- /chat setGlobal [name] [true/false] -> set a channel to be global/local
- /chat changeFormat [name] [format] -> change the chat format of a channel
