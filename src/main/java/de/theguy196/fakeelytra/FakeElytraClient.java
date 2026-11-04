package de.theguy196.fakeelytra;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.Locale;

public final class FakeElytraClient implements ClientModInitializer {
    private static final long DEFAULT_REFERENCE_PRICE = 1_000_000L;
    private static long referencePrice = DEFAULT_REFERENCE_PRICE;

    @Override
    public void onInitializeClient() {
        loadPriceConfig();

        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            if (stack.isOf(Items.LEATHER_CHESTPLATE)) {
                String formattedPrice = NumberFormat.getIntegerInstance(Locale.GERMANY)
                        .format(referencePrice);
                lines.add(Text.literal("Elytra-Referenzpreis (nur lokal): $" + formattedPrice)
                        .formatted(Formatting.GOLD));
                lines.add(Text.literal("Kein echter Item- oder Verkaufspreis")
                        .formatted(Formatting.GRAY));
            }
        });
    }

    private static void loadPriceConfig() {
        Path configFile = FabricLoader.getInstance()
                .getConfigDir()
                .resolve("fakeelytra-price.txt");

        try {
            if (Files.exists(configFile)) {
                String value = Files.readString(configFile).trim();
                referencePrice = Math.max(0L, Long.parseLong(value));
            } else {
                Files.createDirectories(configFile.getParent());
                Files.writeString(configFile, Long.toString(DEFAULT_REFERENCE_PRICE));
                referencePrice = DEFAULT_REFERENCE_PRICE;
            }
        } catch (IOException | NumberFormatException exception) {
            referencePrice = DEFAULT_REFERENCE_PRICE;
            System.err.println("[FakeElytra] Konnte fakeelytra-price.txt nicht lesen. "
                    + "Nutze den Standard-Referenzpreis.");
        }
    }
}
