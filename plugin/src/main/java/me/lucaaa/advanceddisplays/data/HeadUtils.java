package me.lucaaa.advanceddisplays.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.lucaaa.advanceddisplays.nms_common.Logger;
import me.lucaaa.advanceddisplays.nms_common.PacketException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
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
    public static ItemStack getHead(DisplayHeadType displayHeadType, String displayHeadValue, boolean enchanted, Player player, Logger logger) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta skullMeta = (SkullMeta) Objects.requireNonNull(item.getItemMeta());

        String value = displayHeadValue;
        if (displayHeadValue.equalsIgnoreCase("%player%") && player != null) {
            value = player.getName();
        }

        String base64;
        if (displayHeadType == DisplayHeadType.PLAYER) {
            try {
                String UUIDJson = getJSONRequest("https://api.mojang.com/users/profiles/minecraft/" + value);
                JsonObject uuidObject = JsonParser.parseString(UUIDJson).getAsJsonObject();
                String dashlessUuid = uuidObject.get("id").getAsString();

                String profileJson = getJSONRequest("https://sessionserver.mojang.com/session/minecraft/profile/" + dashlessUuid);
                JsonObject profileObject = JsonParser.parseString(profileJson).getAsJsonObject();
                base64 = profileObject.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();

            } catch (Exception e) {
                logger.logError(java.util.logging.Level.WARNING, "An error occurred while parsing a head! Head value: ", e);
                return item;
            }

        } else {
            base64 = displayHeadValue;
        }

        try {
            String skinJson = new String(Base64.getDecoder().decode(base64));
            JsonObject skinObject = JsonParser.parseString(skinJson).getAsJsonObject();
            String url = skinObject.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();

            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
            PlayerTextures textures = profile.getTextures();

            URL urlObject = new URL(url);
            textures.setSkin(urlObject);
            profile.setTextures(textures);
            skullMeta.setOwnerProfile(profile);

        } catch (IllegalArgumentException | MalformedURLException e) {
            logger.logError(java.util.logging.Level.WARNING, "An error occurred while parsing a head! Head value: " + value, e);
            return item;
        }

        if (enchanted) {
            skullMeta.addEnchant(Enchantment.MENDING, 1, true);
        }

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