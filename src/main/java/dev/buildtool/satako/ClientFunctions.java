package dev.buildtool.satako;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collection;

@OnlyIn(Dist.CLIENT)
public class ClientFunctions {
    /**
     * Returns the length of longest string
     */
    public static int calculateLongestStringWidth(Collection<Component> objects) {
        int width = 0;
        for (Component s : objects) {
            int nextwidth = calculateStringWidth(s);
            if (nextwidth > width) width = nextwidth;
        }
        return width;
    }

    public static int calculateStringWidth(Component string) {
        if (string != null) {
            return Minecraft.getInstance().font.width(string.getString());
        }
        return 0;
    }
}
