package me.lucaaa.advanceddisplays.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.lucaaa.advanceddisplays.nms_common.Logger;
import me.lucaaa.advanceddisplays.nms_common.PacketException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

public class HeadUtils {
    public static ItemStack getPlayerHead(String player, Logger logger) {
        try {
            String UUIDJson = getJSONRequest("https://api.mojang.com/users/profiles/minecraft/" + player);
            JsonObject uuidObject = JsonParser.parseString(UUIDJson).getAsJsonObject();
            String dashlessUuid = uuidObject.get("id").getAsString();

            String profileJson = getJSONRequest("https://sessionserver.mojang.com/session/minecraft/profile/" + dashlessUuid);
            JsonObject profileObject = JsonParser.parseString(profileJson).getAsJsonObject();
            String base64 = profileObject.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();

            return getBase64Head(base64);

        } catch (Exception e) {
            logger.logError(java.util.logging.Level.WARNING, "An error occurred while parsing a player head! Head value: " + player, e);
            return new ItemStack(Material.PLAYER_HEAD);
        }
    }

    public static ItemStack getBase64Head(String base64, Logger logger) {
        try {
            return getBase64Head(base64);

        } catch (MalformedURLException e) {
            logger.logError(java.util.logging.Level.WARNING, "An error occurred while parsing a base64 head! Head value: " + base64, e);
            return new ItemStack(Material.PLAYER_HEAD);
        }
    }

    /**
     * This method, opposite to {@link #getBase64Head(String, Logger)}, throws an exception when an error occurs.
     * The other methods hangle it "differently" (the printed error changes sightly).
     * @param base64 The head's base64 texture.
     * @return The head with the base64 texture.
     * @throws MalformedURLException Exception occurred while parsing URLs.
     */
    private static ItemStack getBase64Head(String base64) throws MalformedURLException {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) Objects.requireNonNull(item.getItemMeta());

        String skinJson = new String(Base64.getDecoder().decode(base64));
        JsonObject skinObject = JsonParser.parseString(skinJson).getAsJsonObject();
        String url = skinObject.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();

        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();

        URL urlObject = new URL(url);
        textures.setSkin(urlObject);
        profile.setTextures(textures);
        skullMeta.setOwnerProfile(profile);

        item.setItemMeta(skullMeta);
        return item;
    }

    private static String getJSONRequest(String url) throws IOException, InterruptedException, URISyntaxException {
        @SuppressWarnings("resource") // Doesn't implement AutoCloseable in Java 17 (minimum version for plugin to work)
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 200 = full success
        if (response.statusCode() == 200) {
            return response.body();

        } else {
            throw new PacketException("A " + response.statusCode() + " error occurred while handling an HTTP request: " + response.body());
        }
    }
}