package ru.expensive.common.util.auction;

import net.minecraft.item.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuctionPriceParser {
    private final Pattern funTimePricePattern = Pattern.compile("\\$(\\d+(?:\\s\\d{3})*(?:\\.\\d{2})?)");

    public int getPrice(ItemStack stack) {
        Matcher matcher = funTimePricePattern.matcher(stack.getOrCreateSubNbt("display").toString());
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1).replaceAll("[^\\d.]", ""));
        }
        return -1;
    }
}