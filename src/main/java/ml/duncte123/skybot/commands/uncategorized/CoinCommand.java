package ml.duncte123.skybot.commands.uncategorized;

import ml.duncte123.skybot.objects.command.Command;
import ml.duncte123.skybot.utils.AirUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.utils.PermissionUtil;

public class CoinCommand extends Command {

    public final static String help = "flips a coin.";
    /**
     * this is where the coins are stored
     */
    private final String coinUrl = "https://dshelmondgames.ml/img/coin/";
    /**
     * this are our images
     */
    private final String[] imagesArr = { "heads.png", "tails.png" };

    /**
     * This is the executeCommand of the command, the thing you want the command to to needs to be in here
     * @param args The command agruments
     * @param event a instance of {@link MessageReceivedEvent MessageReceivedEvent}
     */
    @Override
    public void executeCommand(String[] args, GuildMessageReceivedEvent event) {

        event.getChannel().sendTyping().queue();
        event.getChannel().sendMessage("*Flips a coin*").queue();
        MessageEmbed eb = AirUtils.embedImage(coinUrl+imagesArr[AirUtils.rand.nextInt(2)]);
        if(!PermissionUtil.checkPermission(event.getGuild().getSelfMember(), Permission.MESSAGE_EMBED_LINKS)) {
            event.getChannel().sendMessage(AirUtils.embedToMessage(eb)).queue();
            return;
        }
        event.getChannel().sendMessage(eb).queue();
    }

    /**
     * The usage instructions of the command
     * @return a String
     */
    @Override
    public String help() {
        // TODO Auto-generated method stub
        return help;
    }

    @Override
    public String getName() {
        return "coin";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"flip"};
    }
}