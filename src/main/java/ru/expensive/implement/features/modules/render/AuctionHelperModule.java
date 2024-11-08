package ru.expensive.implement.features.modules.render;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.ColorHelper;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.implement.ColorSetting;
import ru.expensive.common.util.auction.AuctionPriceParser;
import ru.expensive.implement.events.container.HandledScreenEvent;

import java.util.Comparator;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuctionHelperModule extends Module {
    static final int[] RED_GREEN_COLORS = {0xFF00FF00, 0xFF0000};
    AuctionPriceParser auctionPriceParser = new AuctionPriceParser();

    ColorSetting cheapestItemColorSetting = new ColorSetting("Cheapest Item",
            "Highlight color for the lowest priced item.")
            .setColor(0xFF00FF00)
            .presets(RED_GREEN_COLORS);

    ColorSetting costEffectiveItemColorSetting = new ColorSetting("Cost Effective Item",
            "Highlight color for the best item.")
            .setColor(0xFF0000)
            .presets(RED_GREEN_COLORS);

    public AuctionHelperModule() {
        super("AuctionHelper", "Auction Helper", ModuleCategory.RENDER);
        setup(cheapestItemColorSetting, costEffectiveItemColorSetting);
    }

    @EventHandler
    public void onHandledScreen(HandledScreenEvent event) {
        if (mc.currentScreen instanceof GenericContainerScreen screen) {
            int offsetX = (screen.width - event.getBackgroundWidth()) / 2;
            int offsetY = (screen.height - event.getBackgroundHeight()) / 2;

            int cheapItemColor = getBlinkingColor(cheapestItemColorSetting.getColor());
            int cheapestQuantityColor = getBlinkingColor(costEffectiveItemColorSetting.getColor());

            Slot cheapestSlot = findSlotWithLowestPrice(screen.getScreenHandler().slots);
            Slot costEffectiveSlot = findSlotWithBestPricePerItem(screen.getScreenHandler().slots);

            DrawContext context = event.getDrawContext();
            context.getMatrices().push();
            context.getMatrices().translate(offsetX, offsetY, 0);

            highlightSlot(context, cheapestSlot, cheapItemColor);
            highlightSlot(context, costEffectiveSlot, cheapestQuantityColor);

            context.getMatrices().pop();
        }
    }

    private int getBlinkingColor(int color) {
        int red = ColorHelper.Argb.getRed(color);
        int green = ColorHelper.Argb.getGreen(color);
        int blue = ColorHelper.Argb.getBlue(color);
        return createBlinkingColor(red, green, blue);
    }

    private int createBlinkingColor(int red, int green, int blue) {
        long time = System.currentTimeMillis() / 10;
        int alpha = (int) (Math.abs(Math.sin(time * Math.PI / 180)) * 170.0F);
        return ColorHelper.Argb.getArgb(alpha, red, green, blue);
    }

    private Slot findSlotWithLowestPrice(List<Slot> slots) {
        return slots.stream()
                .filter(this::hasValidPrice)
                .min(Comparator.comparingInt(slot -> auctionPriceParser.getPrice(slot.getStack())))
                .orElse(null);
    }

    private Slot findSlotWithBestPricePerItem(List<Slot> slots) {
        return slots.stream()
                .filter(this::isValidMultiItemSlot)
                .min(Comparator.comparingDouble(this::calculatePricePerItem))
                .orElse(null);
    }

    private boolean hasValidPrice(Slot slot) {
        return auctionPriceParser.getPrice(slot.getStack()) >= 0;
    }

    private boolean isValidMultiItemSlot(Slot slot) {
        return hasValidPrice(slot) && slot.getStack().getCount() > 1;
    }

    private double calculatePricePerItem(Slot slot) {
        ItemStack itemStack = slot.getStack();
        int price = auctionPriceParser.getPrice(itemStack);
        return (double) price / itemStack.getCount();
    }

    private void highlightSlot(DrawContext context, Slot slot, int color) {
        if (slot != null) {
            int x = slot.x;
            int y = slot.y;
            context.fill(x, y, x + 16, y + 16, color);
        }
    }
}
