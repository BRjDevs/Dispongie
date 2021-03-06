package me.d4rk.dispongie.listeners;

import me.d4rk.dispongie.Dispongie;
import net.dv8tion.jda.core.JDA;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.message.MessageChannelEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ChatListener {

    JDA jda;
    String channel_id;
    String webhook;

    public ChatListener(String weeb, JDA gee, String chid) {
        webhook = weeb;
        jda = gee;
        channel_id = chid;
    }

    @Listener
    public void onChat(MessageChannelEvent.Chat event) {
        if(event.getCause().first(Player.class).isPresent()) {
            if (webhook != null) {
                new Thread(() -> {
                    CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                    HttpPost post = new HttpPost(webhook);
                    try {
                        post.setHeader("Charset", "UTF-8");
                        List<NameValuePair> nameValuePairs = new ArrayList<>();
                        nameValuePairs.add(new BasicNameValuePair("content", Dispongie.convertMentionsFromNames(event.getRawMessage().toPlain())));
                        nameValuePairs.add(new BasicNameValuePair("username", event.getCause().first(Player.class).isPresent() ? event.getCause().first(Player.class).get().getName() : "Server"));
                        nameValuePairs.add(new BasicNameValuePair("avatar_url", event.getCause().first(Player.class).isPresent() ? "https://crafatar.com/avatars/" + event.getCause().first(Player.class).get().getIdentifier() + "?overlay=true" : "https://cdn.discordapp.com/avatars/310513420367822860/ad73f066cd5df5bcc8489cb3cf6ba9d6.png?size=256"));
                        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
                        post.setEntity(urlEncodedFormEntity);
                        HttpResponse response = httpClient.execute(post);
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                }).start();
            } else {
                jda.getTextChannelById(channel_id).sendMessage("**" + event.getCause().first(Player.class).get().getName() + ": **" + event.getRawMessage().toPlain()).queue();
            }
        }
    }

}
