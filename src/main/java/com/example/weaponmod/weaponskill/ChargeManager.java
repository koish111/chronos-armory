package com.example.weaponmod.weaponskill;

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
    private ChargeManager() {
    }

    public static int getMaxCharge() {
        return NullBlade.MAX_CHARGE;
    }

    public static int getMaxCharge(ResourceLocation weaponId) {
        if (weaponId.equals(NullBlade.WEAPON_ID)) {
            return NullBlade.MAX_CHARGE;
        }
        if (weaponId.equals(MoonMarrowScythe.WEAPON_ID)) {
            return MoonMarrowScythe.MAX_CHARGE;
        }
        if (weaponId.equals(AntaresRapier.WEAPON_ID)) {
            return AntaresRapier.MAX_CHARGE;
        }
        if (weaponId.equals(AzureMountainsMasher.WEAPON_ID)) {
            return AzureMountainsMasher.MAX_CHARGE;
        }
        if (weaponId.equals(GreatApple.WEAPON_ID)) {
            return GreatApple.MAX_CHARGE;
        }
        if (weaponId.equals(PerpetualNightStar.WEAPON_ID)) {
            return PerpetualNightStar.MAX_CHARGE;
        }
        if (weaponId.equals(CyanFrostVioletVolt.WEAPON_ID)) {
            return CyanFrostVioletVolt.MAX_CHARGE;
        }

        return 0;
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
        if (weaponId.equals(NullBlade.WEAPON_ID)) {
            return ModAttachments.NULL_BLADE_CHARGE.get();
        }
        if (weaponId.equals(MoonMarrowScythe.WEAPON_ID)) {
            return ModAttachments.MOON_MARROW_SCYTHE_CHARGE.get();
        }
        if (weaponId.equals(AntaresRapier.WEAPON_ID)) {
            return ModAttachments.ANTARES_RAPIER_CHARGE.get();
        }
        if (weaponId.equals(AzureMountainsMasher.WEAPON_ID)) {
            return ModAttachments.AZURE_MOUNTAINS_MASHER_CHARGE.get();
        }
        if (weaponId.equals(GreatApple.WEAPON_ID)) {
            return ModAttachments.GREAT_APPLE_CHARGE.get();
        }
        if (weaponId.equals(PerpetualNightStar.WEAPON_ID)) {
            return ModAttachments.PERPETUAL_NIGHT_STAR_CHARGE.get();
        }
        if (weaponId.equals(CyanFrostVioletVolt.WEAPON_ID)) {
            return ModAttachments.CYAN_FROST_VIOLET_VOLT_CHARGE.get();
        }

        return null;
    }
}
