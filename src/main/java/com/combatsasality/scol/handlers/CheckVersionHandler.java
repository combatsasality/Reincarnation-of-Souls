package com.combatsasality.scol.handlers;

import com.combatsasality.scol.Main;
import com.google.gson.Gson;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckVersionHandler {
    public static final String BASE_URL = "https://combatsasality.com/api/version";
    public static TranslationTextComponent newVersionComponent;
    public static String newVersion;
    public static TranslationTextComponent urlForNewVersion;

    public static class Response {
        public boolean status;
        public String message;
        public String url;
        public Response(boolean status, String message, String url) {
            this.status = status;
            this.message = message;
            this.url = url;
        }
    }

    public static Response getNewestVersion() {
        try {
            URL url = new URL(BASE_URL + "?version="+Main.VERSION_MINECRAFT);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.setRequestProperty("Accept", "*/*");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                Gson g = new Gson();
                Response jsonObject = g.fromJson(response.toString(), Response.class);
                connection.disconnect();
                return jsonObject;
            } else {
                System.out.println("Failed to fetch data. Response code: " + responseCode);
                connection.disconnect();
                return new Response(false, null, null);
            }

        } catch (Exception e) {
            return new Response(false, null, null);
        }
    }

    public static void worldEventLoad(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer().getCommandSenderWorld().isClientSide || !ConfigHandler.UPDATE_CHECK.get()) return;
        if (newVersion == null) {
            Response response = getNewestVersion();
            if (response.status) {
                newVersion = response.message;
                newVersionComponent = new TranslationTextComponent("scol.update.0", response.message);
                newVersionComponent.withStyle(TextFormatting.ITALIC);
                newVersionComponent.withStyle(TextFormatting.DARK_PURPLE);
                urlForNewVersion = new TranslationTextComponent("scol.update.1");
                Style style = Style.EMPTY
                        .applyFormat(TextFormatting.DARK_PURPLE)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, response.url));
                urlForNewVersion.setStyle(style);
            }
        }
        if (newVersion != null && !newVersion.equalsIgnoreCase(Main.VERSION)) {
            event.getPlayer().displayClientMessage(newVersionComponent, false);
            event.getPlayer().displayClientMessage(urlForNewVersion, false);
        }
    }
}
