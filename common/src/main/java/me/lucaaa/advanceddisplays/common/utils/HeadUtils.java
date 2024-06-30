package me.lucaaa.advanceddisplays.common.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.lucaaa.advanceddisplays.api.displays.enums.DisplayHeadType;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class HeadUtils {
    public static ItemStack getHead(String base64, String title, List<String> lore) {
        ItemStack head = getHead(DisplayHeadType.BASE64, base64, null);
        ItemMeta meta = head.getItemMeta();
        assert meta != null;

        meta.setDisplayName(Utils.getColoredText(title));
        meta.setLore(lore.stream().map(Utils::getColoredText).toList());

        head.setItemMeta(meta);
        return head;
    }

    public static ItemStack getHead(DisplayHeadType displayHeadType, String displayHeadValue, Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        assert skullMeta != null;

        String value = displayHeadValue;
        if (displayHeadValue.equalsIgnoreCase("%player%")) {
            value = player.getName();
        }

        String base64;
        if (displayHeadType == DisplayHeadType.PLAYER) {
            try {
                String UUIDJson = IOUtils.toString(new URL("https://api.mojang.com/users/profiles/minecraft/" + value), StandardCharsets.UTF_8);
                JsonObject uuidObject = JsonParser.parseString(UUIDJson).getAsJsonObject();
                String dashlessUuid = uuidObject.get("id").getAsString();

                String profileJson = IOUtils.toString(new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + dashlessUuid), StandardCharsets.UTF_8);
                JsonObject profileObject = JsonParser.parseString(profileJson).getAsJsonObject();
                base64 = profileObject.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();

            } catch (IOException e) {
                Logger.log(java.util.logging.Level.WARNING, "The player name " + value + " does not exist!");
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
            Logger.logError(java.util.logging.Level.WARNING, "An error occurred while parsing a head! Head value: " + value, e);
            return item;
        }


        item.setItemMeta(skullMeta);
        return item;
    }
}
