package ml.duncte123.skybot.commands.uncategorized;

import ml.duncte123.skybot.objects.command.Command;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class PingCommand extends Command {

    public final static String help = "PONG!";

    /**
     * This is the executeCommand of the command, the thing you want the command to to needs to be in here
     * @param args The command agruments
     * @param event a instance of {@link MessageReceivedEvent MessageReceivedEvent}
     */
    @Override
    public void executeCommand(String[] args, GuildMessageReceivedEvent event) {
        long time = System.currentTimeMillis();

        event.getChannel().sendMessage("PONG!").queue( (message) ->
            message.editMessageFormat("PONG!\nping is: %dms \nWebsocket ping: " + event.getJDA().getPing() + "ms", (System.currentTimeMillis() - time) ).queue());

    }

    /**
     * The usage instructions of the command
     * @return a String
     */
    @Override
    public String help() {
        return help;
    }

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"pong"};
    }
}