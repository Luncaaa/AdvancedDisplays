package me.lucaaa.advanceddisplays.data;

import me.clip.placeholderapi.PlaceholderAPI;
import me.lucaaa.advanceddisplays.AdvancedDisplays;
import me.lucaaa.advanceddisplays.api.util.ComponentSerializer;
import me.lucaaa.advanceddisplays.inventory.inventories.DisplayEditorGUI;
import me.lucaaa.advanceddisplays.nms_common.Version;
import me.lucaaa.advanceddisplays.v1_21_R5.VersionUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Utils {
    public static String getColoredText(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String getTextString(String message, Player clickedPlayer, Player globalPlayer, boolean useGlobalPlaceholders) {
        return ComponentSerializer.getLegacyString(getText(message, clickedPlayer, globalPlayer, useGlobalPlaceholders));
    }

    public static Component getText(String message, Player clickedPlayer, Player globalPlayer, boolean useGlobalPlaceholders) {
        message = message.replace("%player%", clickedPlayer.getName());
        if (globalPlayer != null) message = message.replace("%global_player%", globalPlayer.getName());

        Player placeholderPlayer = (useGlobalPlaceholders) ? globalPlayer : clickedPlayer;
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            message = PlaceholderAPI.setPlaceholders(placeholderPlayer, message);
        }

        return ComponentSerializer.deserialize(message);
    }

    public static Component combine(Component component1, Component component2) {
        return component1.appendNewline().append(component2);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void saveItemData(ItemStack item, ConfigurationSection settings, Version version) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        if (version.isEqualOrNewerThan(Version.v1_21_R3)) {
            VersionUtils.saveMetaCustomModelData(meta, settings);
        } else {
            int customModelData = (meta.hasCustomModelData()) ? meta.getCustomModelData() : 0;
            settings.set("customModelData", customModelData);
        }

        if (meta instanceof PotionMeta potion && potion.getColor() != null) {
            settings.set("color",
                    potion.getColor().getRed() + ";" +
                            potion.getColor().getGreen() + ";" +
                            potion.getColor().getBlue());

        } else if (meta instanceof ArmorMeta armor) {
            if (armor.getTrim() == null) {
                settings.set("trim", null); // Deletes the setting if present

            } else {
                settings.set("trim", armor.getTrim().getPattern().getKey().getKey() + ":" + armor.getTrim().getMaterial().getKey().getKey());
            }

            if (meta instanceof LeatherArmorMeta leatherArmor) {
                settings.set("color",
                        leatherArmor.getColor().getRed() + ";" +
                                leatherArmor.getColor().getGreen() + ";" +
                                leatherArmor.getColor().getBlue());
            }

        } else if (meta instanceof BannerMeta banner) {
            List<String> patterns = new ArrayList<>();

            for (Pattern pattern : banner.getPatterns()) {
                patterns.add(pattern.getPattern().name() + ":" + pattern.getColor().name());
            }

            settings.set("patterns", patterns);

        } else if (meta instanceof CompassMeta compass) {
            if (compass.getLodestone() == null) {
                settings.set("lodestone", null); // Deletes the setting if present

            } else {
                Location lodestone = compass.getLodestone();
                settings.set("lodestone", lodestone.getX() + ";" + lodestone.getY() + ";" + lodestone.getZ());
            }

        } else if (meta instanceof BundleMeta bundle) {
            settings.set("hasItems", bundle.hasItems());

        } else if (meta instanceof AxolotlBucketMeta bucketMeta && bucketMeta.hasVariant()) {
            settings.set("axolotl-bucket", bucketMeta.getVariant().name());

        } else if (meta instanceof CrossbowMeta crossbowMeta) {
            List<ItemStack> chargedProjectiles = crossbowMeta.getChargedProjectiles();
            if (!chargedProjectiles.isEmpty()) {
                ItemStack firstProjectile = chargedProjectiles.get(0);
                if (firstProjectile.getType() == Material.FIREWORK_ROCKET) {
                    settings.set("crossbow-ammo", DisplayEditorGUI.CrossbowAmmo.ROCKET.name());
                } else {
                    settings.set("crossbow-ammo", DisplayEditorGUI.CrossbowAmmo.ARROW.name());
                }
            } else {
                settings.set("crossbow-ammo", DisplayEditorGUI.CrossbowAmmo.NONE.name());
            }
        }

        if (!item.getEnchantments().isEmpty()) {
            settings.set("enchanted", !item.getEnchantments().isEmpty());
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public static ItemStack loadItemData(ItemStack item, ConfigurationSection settings, World compassWorld, AdvancedDisplays plugin) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        if (settings.isString("oraxen") && plugin.isIntegrationLoaded(Compatibility.ORAXEN)) {
            return plugin.getIntegration(Compatibility.ORAXEN).getItemStack(settings.getString("oraxen"));
        } else if (settings.isString("itemsAdder") && plugin.isIntegrationLoaded(Compatibility.ORAXEN)) {
            return plugin.getIntegration(Compatibility.ITEMS_ADDER).getItemStack(settings.getString("itemsAdder"));
        }

        if (plugin.getNmsVersion().isEqualOrNewerThan(Version.v1_21_R3)) {
            VersionUtils.loadMetaCustomModelData(meta, settings);

        } else {
            int customModelData = settings.getInt("customModelData");
            if (customModelData != 0) {
                meta.setCustomModelData(customModelData);
            }
        }

        if (meta instanceof PotionMeta potion && settings.isString("color")) {
            String[] saved = settings.getString("color", "255;255;255").split(";");
            Color color = Color.fromRGB(Integer.parseInt(saved[0]), Integer.parseInt(saved[1]), Integer.parseInt(saved[2]));
            potion.setColor(color);

        } else if (meta instanceof ArmorMeta armor) {
            if (settings.isString("trim")) {
                String[] trim = settings.getString("trim", "sentry:netherite").toLowerCase().split(":");
                TrimMaterial material = Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(trim[1]));
                TrimPattern pattern = Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(trim[0]));

                if (material == null || pattern == null) {
                    plugin.log(Level.WARNING, "Invalid armor trim material and/or pattern: Material: " + trim[1] + " - Pattern: " + trim[0]);
                } else {
                    armor.setTrim(new ArmorTrim(material, pattern));
                }
            }

            if (meta instanceof LeatherArmorMeta leatherArmor && settings.isString("color")) {
                String[] saved = settings.getString("color", "255;255;255").split(";");
                Color color = Color.fromRGB(Integer.parseInt(saved[0]), Integer.parseInt(saved[1]), Integer.parseInt(saved[2]));
                leatherArmor.setColor(color);
            }

        } else if (meta instanceof BannerMeta banner && settings.isList("patterns")) {
            List<String> patterns = settings.getStringList("patterns");

            for (String configPattern : patterns) {
                if (configPattern.isBlank()) continue;
                String[] parts = configPattern.split(":");

                try {
                    PatternType pattern = PatternType.valueOf(parts[0]);
                    DyeColor color = DyeColor.valueOf(parts[1]);
                    banner.addPattern(new Pattern(color, pattern));
                } catch (IllegalArgumentException e) {
                    plugin.log(Level.WARNING, "Invalid banner pattern type and/or color: " + configPattern);
                }
            }

        } else if (meta instanceof CompassMeta compass && settings.isString("lodestone")) {
            String[] location = settings.getString("lodestone", "0.0;0.0;0.0").split(";");
            Location lodestone = new Location(compassWorld,
                    Double.parseDouble(location[0]),
                    Double.parseDouble(location[1]),
                    Double.parseDouble(location[2]));
            compass.setLodestone(lodestone);

        } else if (meta instanceof BundleMeta bundle) {
            if (settings.getBoolean("hasItems")) {
                bundle.addItem(new ItemStack(Material.DIAMOND, 64));
            }

        } else if (meta instanceof AxolotlBucketMeta bucketMeta) {
            if (settings.isString("axolotl-bucket")) {
                String variant = settings.getString("axolotl-bucket", "").toUpperCase();
                try {
                    bucketMeta.setVariant(Axolotl.Variant.valueOf(variant));
                } catch (IllegalArgumentException e) {
                    plugin.log(Level.WARNING, "Invalid axolotl variant: " + variant);
                }
            }

        } else if (meta instanceof CrossbowMeta crossbowMeta) {
            if (settings.isString("crossbow-ammo")) {
                String ammo = settings.getString("crossbow-ammo");
                try {
                    DisplayEditorGUI.CrossbowAmmo crossbowAmmo = DisplayEditorGUI.CrossbowAmmo.valueOf(ammo);
                    crossbowMeta.setChargedProjectiles(crossbowAmmo.getItems());
                } catch (IllegalArgumentException e) {
                    plugin.log(Level.WARNING, "Invalid crossbow ammo: " + ammo);
                }
            }
        }

        // Item displays (ignoreEnchant = true) have their own way to enchant items.
        if (settings.getBoolean("enchanted")) {
            meta.addEnchant(Enchantment.MENDING, 1, true);
        }

        hideFlags(meta);
        item.setItemMeta(meta);
        return item;
    }

    public static void hideFlags(ItemMeta meta) {
        for (ItemFlag flag : ItemFlag.values()) {
            meta.addItemFlags(flag);
        }
    }

    public static String locToString(Location loc) {
        return round(loc.getX()) + ";" + round(loc.getY()) + ";" + round(loc.getZ());
    }

    public static double round(double toRound) {
        return BigDecimal.valueOf(toRound).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static List<String> isStringValidLoc(String loc) {
        List<String> errors = new ArrayList<>();
        if (!loc.matches("^[0-9~*;]+$")) {
            errors.add("The location can only have numbers, \";\", \"~\" or \"*\"! For decimals use dots (\".\"), NOT commas (\",\").");

        } else if (loc.split(";").length != 3) {
            errors.add("The location has an invalid format! It must be x;y;z");
        }
        return errors;
    }

    public static Double parsePosition(String toParse, CoordComponent component, Location relative, CommandSender sender) throws NumberFormatException {
        double parsedArg = 0;

        if (toParse.contains("~") && sender instanceof Player player) {
            toParse = toParse.replace("~", "");
            parsedArg = switch (component) {
                case X -> player.getLocation().getX();
                case Y -> player.getLocation().getY() ;
                case Z -> player.getLocation().getZ();
            };

        } else if (toParse.contains("*")) {
            toParse = toParse.replace("*", "");
            parsedArg = switch (component) {
                case X -> relative.getX();
                case Y -> relative.getY();
                case Z -> relative.getZ();
            };
        }

        double parsed = toParse.isBlank() ? 0 : Double.parseDouble(toParse);
        return Utils.round(parsedArg + parsed);
    }

    public enum CoordComponent {
        X,
        Y,
        Z
    }
}