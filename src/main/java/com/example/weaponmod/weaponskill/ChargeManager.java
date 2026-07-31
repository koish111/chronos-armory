package com.example.weaponmod.weaponskill;

import com.example.weaponmod.WeaponMod;
import com.example.weaponmod.attachments.ModAttachments;
import com.example.weaponmod.items.custom.AntaresRapier;
import com.example.weaponmod.items.custom.AzureMountainsMasher;
import com.example.weaponmod.items.custom.CyanFrostVioletVolt;
import com.example.weaponmod.items.custom.GreatApple;
import com.example.weaponmod.items.custom.MoonMarrowScythe;
import com.example.weaponmod.items.custom.NullBlade;
import com.example.weaponmod.items.custom.PerpetualNightStar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;

public final class ChargeManager {

    public static int getMaxCharge() {
        return NullBlade.MAX_CHARGE;
    }

    public static int getMaxCharge(ResourceLocation weaponId) {
        if (!WeaponMod.MODID.equals(weaponId.getNamespace())) {
            return 0;
        }
        return switch (weaponId.getPath()) {
            case "null_blade" -> NullBlade.MAX_CHARGE;
            case "moon_marrow_scythe" -> MoonMarrowScythe.MAX_CHARGE;
            case "antares_rapier" -> AntaresRapier.MAX_CHARGE;
            case "azure_mountains_masher_sword" -> AzureMountainsMasher.MAX_CHARGE;
            case "great_apple_heavy_axe" -> GreatApple.MAX_CHARGE;
            case "perpetual_nightstar_trident" -> PerpetualNightStar.MAX_CHARGE;
            case "cyanfrost_violetvolt_katana" -> CyanFrostVioletVolt.MAX_CHARGE;
            default -> 0;
        };
    }

    public static int getCharge(Player player) {
        return getCharge(player, NullBlade.WEAPON_ID);
    }

    public static int getCharge(Player player, ResourceLocation weaponId) {
        AttachmentType<Integer> attachment = getAttachment(weaponId);
        int maxCharge = getMaxCharge(weaponId);
        return attachment == null || maxCharge <= 0
                ? 0
                : Math.min(player.getData(attachment), maxCharge);
    }

    public static int getCharge() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player == null ? 0 : ChargeManager.getCharge(player);
    }

    public static void addCharge(Player player, int amount) {
        addCharge(player, NullBlade.WEAPON_ID, amount);
    }

    public static void addCharge(Player player, ResourceLocation weaponId, int amount) {
        AttachmentType<Integer> attachment = getAttachment(weaponId);
        int maxCharge = getMaxCharge(weaponId);
        if (maxCharge <= 0 || amount == 0) {
            return;
        }

        int oldCharge = getCharge(player, weaponId);
        int newCharge = Math.clamp((long) oldCharge + amount, 0, maxCharge);
        if (newCharge == oldCharge) {
            return;
        }

        player.setData(attachment, newCharge);
        player.syncData(attachment);
    }

    /** 必须充满后才能消耗，成功时将充能归零。 */
    public static boolean tryConsumeFull(Player player) {
        return tryConsumeFull(player, NullBlade.WEAPON_ID);
    }

    /** 必须充满后才能消耗，成功时将指定武器的充能归零。 */
    public static boolean tryConsumeFull(Player player, ResourceLocation weaponId) {
        AttachmentType<Integer> attachment = getAttachment(weaponId);
        int maxCharge = getMaxCharge(weaponId);
        if (attachment == null || maxCharge <= 0 || getCharge(player, weaponId) < maxCharge) {
            return false;
        }

        player.setData(attachment, 0);
        player.syncData(attachment);
        return true;
    }

    /** 每 10 tick 增加 1 点充能。 */
    public static void tickPassive(ServerLevel level) {
        if (level.getGameTime() % 10 != 0) {
            return;
        }

        for (ServerPlayer player : level.players()) {
            addCharge(player, NullBlade.WEAPON_ID, 1);
            addCharge(player, MoonMarrowScythe.WEAPON_ID, 1);
            addCharge(player, AntaresRapier.WEAPON_ID, 1);
            addCharge(player, AzureMountainsMasher.WEAPON_ID, 1);
            addCharge(player, GreatApple.WEAPON_ID, 1);
            addCharge(player, PerpetualNightStar.WEAPON_ID, 1);
            addCharge(player, CyanFrostVioletVolt.WEAPON_ID, 1);
        }
    }

    private static AttachmentType<Integer> getAttachment(ResourceLocation weaponId) {
        if (!WeaponMod.MODID.equals(weaponId.getNamespace())) {
            return null;
        }
        return switch (weaponId.getPath()) {
            case "null_blade" -> ModAttachments.NULL_BLADE_CHARGE.get();
            case "moon_marrow_scythe" -> ModAttachments.MOON_MARROW_SCYTHE_CHARGE.get();
            case "antares_rapier" -> ModAttachments.ANTARES_RAPIER_CHARGE.get();
            case "azure_mountains_masher_sword" -> ModAttachments.AZURE_MOUNTAINS_MASHER_CHARGE.get();
            case "great_apple_heavy_axe" -> ModAttachments.GREAT_APPLE_CHARGE.get();
            case "perpetual_nightstar_trident" -> ModAttachments.PERPETUAL_NIGHT_STAR_CHARGE.get();
            case "cyanfrost_violetvolt_katana" -> ModAttachments.CYAN_FROST_VIOLET_VOLT_CHARGE.get();
            default -> null;
        };
    }
}
