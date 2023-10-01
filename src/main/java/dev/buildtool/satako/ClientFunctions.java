package dev.buildtool.satako;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collection;

@OnlyIn(Dist.CLIENT)
public class ClientFunctions {
    public static int calculateStringWidth(Component string) {
        if (string != null) {
            return Minecraft.getInstance().font.width(string.getString());
        }
        return 0;
    }

    /**
     * Returns the length of longest string
     */
    public static int calculateLongestStringWidth(Collection<TextComponent> objects) {
        int width = 0;
        for (TextComponent s : objects) {
            int nextwidth = calculateStringWidth(s);
            if (nextwidth > width) width = nextwidth;
        }
        return width;
    }
}
