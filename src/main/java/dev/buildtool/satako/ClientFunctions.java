package dev.buildtool.satako;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ClientFunctions {
    public static int calculateStringWidth(ITextComponent string) {
        if (string != null) {
            return Minecraft.getInstance().font.width(string.getString());
        }
        return 0;
    }

    /**
     * Splits every line so it fits the max width parameter
     */
    private static List<String> splitString(List<String> strings, List<String> returnList, int nextIndex, FontRenderer font, int maxWidth) {
        String string = strings.get(nextIndex);
        if (font.width(string) > maxWidth) {
            String part = string.substring(0, string.lastIndexOf(' '));
            while (font.width(part) > maxWidth) {
                part = part.substring(0, part.length() - 2);
            }
            while (!part.endsWith(" ")) {
                part = part.substring(0, part.length() - 2);
            }
            String part2 = string.substring(part.length());
            returnList.add(part);
            if (font.width(part2) <= maxWidth) {
                int next = nextIndex + 1;
                if (next < strings.size()) {
                    String nextString = strings.get(next);
                    if (font.width(part2 + nextString) <= maxWidth) {
                        returnList.add(part2 + nextString);
                        if (next + 1 < strings.size())
                            splitString(strings, returnList, next + 1, font, maxWidth);
                    } else returnList.add(part2);
                } else {
                    returnList.add(part2);
                }
            }
            int indexOfNext = strings.indexOf(string) + 1;
            if (indexOfNext < strings.size()) {
                splitString(strings, returnList, indexOfNext, font, maxWidth);
            }
        } else {
            if (string.isEmpty()) {
                splitString(strings, returnList, nextIndex + 1, font, maxWidth);
            } else {
                returnList.add(string);
                int indexOfNext = strings.indexOf(string) + 1;
                if (indexOfNext < strings.size()) {
                    splitString(strings, returnList, indexOfNext, font, maxWidth);
                }
            }
        }
        return returnList;
    }
}
