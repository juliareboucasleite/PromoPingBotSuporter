package com.promoping.bot.comandos.admin;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.promoping.bot.comandos.core.BaseCommand;
import com.promoping.bot.dao.TwitchChannelDAO;
import com.promoping.bot.security.AccessControl;
import com.promoping.bot.utils.BotConfig;
import com.promoping.bot.utils.EmbedBuilder;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gerencia notificações de live da Twitch no canal social-feed.
 */
public class SocialFeedCommand extends BaseCommand {
    
    private static final String SOCIAL_FEED_CHANNEL_ID = "1442931610927366284";
    private static final Pattern TWITCH_URL_PATTERN = Pattern.compile("(?:twitch\\.tv/|^)([^/\\s?]+)", Pattern.CASE_INSENSITIVE);
    
    private final TwitchChannelDAO twitchChannelDAO;
    private final AccessControl accessControl;
    
    public SocialFeedCommand(TwitchChannelDAO twitchChannelDAO, AccessControl accessControl) {
        this.twitchChannelDAO = twitchChannelDAO;
        this.accessControl = accessControl;
    }
    
    @Override
    public String getName() {
        return "social-feed";
    }
    
    @Override
    protected List<String> getAliasesList() {
        return Arrays.asList("twitch", "live");
    }
    
    @Override
    public String getDescription() {
        return "Gerencia notificações de live da Twitch no canal social-feed.";
    }
    
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (!accessControl.isAdmin(event.getMember())) {
            event.getChannel().sendMessage("Você precisa de permissões de administrador para usar este comando.")
                    .queue();
            return;
        }
        
        String action = args.length > 0 ? args[0].toLowerCase() : "listar";
        MessageChannel channel = event.getChannel();
        String prefix = BotConfig.getPrefix();
        
        TextChannel socialFeedChannel = event.getJDA().getTextChannelById(SOCIAL_FEED_CHANNEL_ID);
        if (socialFeedChannel == null) {
            channel.sendMessage("Canal social-feed não encontrado!").queue();
            return;
        }
        
        try {
            if (action.equals("listar") || action.equals("list") || action.isEmpty()) {
                List<TwitchChannelDAO.TwitchChannel> channels = twitchChannelDAO.getAllChannels();
                
                if (channels.isEmpty()) {
                    channel.sendMessage("Nenhum canal da Twitch está sendo monitorado no momento.").queue();
                    return;
                }
                
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Canais Twitch Monitorados")
                        .setDescription("Total: **" + channels.size() + "** canal(is)")
                        .setColor(0x9146ff)
                        .setTimestamp();
                
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.forLanguageTag("pt-PT"));
                StringBuilder channelList = new StringBuilder();
                
                for (TwitchChannelDAO.TwitchChannel ch : channels) {
                    String status = ch.isLive() ? "**AO VIVO**" : "Offline";
                    String lastCheck = ch.getLastLiveCheck() != null 
                            ? dateFormat.format(ch.getLastLiveCheck())
                            : "Nunca";
                    channelList.append("**").append(ch.getChannelName()).append("** - ").append(status)
                            .append("\n└ Última verificação: ").append(lastCheck).append("\n\n");
                }
                
                embed.addField("Canais", channelList.toString(), false);
                channel.sendMessageEmbeds(embed.build()).queue();
                
            } else if (action.equals("adicionar") || action.equals("add")) {
                if (args.length < 2) {
                    channel.sendMessage("Por favor, forneça o nome do canal da Twitch ou a URL.\n**Uso:** `" + prefix + "social-feed adicionar <nome-do-canal>` ou `" + prefix + "social-feed adicionar https://twitch.tv/nome-do-canal`")
                            .queue();
                    return;
                }
                
                String channelInput = args[1];
                String channelName = extractChannelName(channelInput);
                
                if (channelName == null) {
                    channel.sendMessage("Nome de canal inválido. Use apenas o nome do canal ou a URL completa.").queue();
                    return;
                }
                
                if (twitchChannelDAO.channelExists(channelName)) {
                    channel.sendMessage("O canal **" + channelName + "** já está sendo monitorado.").queue();
                    return;
                }
                
                String channelToSave = channelInput.toLowerCase().contains("twitch.tv") 
                        ? channelInput.toLowerCase() 
                        : channelName;
                
                twitchChannelDAO.addChannel(channelToSave);
                
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Canal Adicionado")
                        .setDescription("O canal **" + channelName + "** foi adicionado ao monitoramento.")
                        .addField("Informações", 
                                "• Canal: **" + channelName + "**\n" +
                                "• Notificações serão enviadas em: <#" + SOCIAL_FEED_CHANNEL_ID + ">\n" +
                                "• Verificação automática a cada 5 minutos", false)
                        .setColor(0x00ff00)
                        .setTimestamp();
                
                channel.sendMessageEmbeds(embed.build()).queue();
                
            } else if (action.equals("remover") || action.equals("remove") || action.equals("rem")) {
                if (args.length < 2) {
                    channel.sendMessage("Por favor, forneça o nome do canal da Twitch ou a URL.\n**Uso:** `" + prefix + "social-feed remover <nome-do-canal>`")
                            .queue();
                    return;
                }
                
                String channelInput = args[1];
                String channelName = extractChannelName(channelInput);
                
                twitchChannelDAO.removeChannel(channelName != null ? channelName : channelInput);
                
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("Canal Removido")
                        .setDescription("O canal **" + (channelName != null ? channelName : channelInput) + "** foi removido do monitoramento.")
                        .setColor(0xff6b6b)
                        .setTimestamp();
                
                channel.sendMessageEmbeds(embed.build()).queue();
                
            } else if (action.equals("testar") || action.equals("test")) {
                String channelName = args.length > 1 ? args[1] : "leeksxy";
                
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("TESTE - Live na Twitch")
                        .setDescription("**" + channelName + "** está ao vivo!")
                        .addField("Canal", "[" + channelName + "](https://twitch.tv/" + channelName + ")", true)
                        .addField("Status", "AO VIVO", true)
                        .addField("Título", "Teste de Notificação", false)
                        .setColor(0x9146ff)
                        .setThumbnail("https://static-cdn.jtvnw.net/jtv_user_pictures/asmongold-profile_image-f7ddcbd0332f5aa2-300x300.png")
                        .setTimestamp()
                        .setFooter("PromoPing - Social Feed");
                
                socialFeedChannel.sendMessageEmbeds(embed.build()).queue();
                channel.sendMessage("Notificação de teste enviada no canal social-feed!").queue();
                
            } else {
                channel.sendMessage(
                        "Ação inválida!\n\n" +
                        "**Ações disponíveis:**\n" +
                        "• `listar` - Lista canais monitorados\n" +
                        "• `adicionar <canal>` - Adiciona um canal\n" +
                        "• `remover <canal>` - Remove um canal\n" +
                        "• `verificar` - Força verificação imediata\n" +
                        "• `testar` - Envia uma notificação de teste\n\n" +
                        "**Exemplo:** `" + prefix + "social-feed verificar`"
                ).queue();
            }
            
        } catch (Exception e) {
            channel.sendMessage("Ocorreu um erro ao processar o comando. Tente novamente.").queue();
        }
    }
    
    private String extractChannelName(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        
        Matcher matcher = TWITCH_URL_PATTERN.matcher(input);
        if (matcher.find()) {
            return matcher.group(1).toLowerCase();
        }
        
        return input.toLowerCase().trim();
    }
}
