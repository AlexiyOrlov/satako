package dev.buildtool.satako.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.buildtool.satako.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

/**
 * A UI without slots
 */
public class Screen2 extends Screen
{
    /**
     * GUI's center coordinates
     */
    protected int centerX, centerY;
    private final ArrayList<ScrollList> scrollLists = new ArrayList<>(1);

    public Screen2(Component title) {
        super(title);
    }

    /**
     * Call this first. Provides gui center position
     */
    @Override
    public void init()
    {
        scrollLists.clear();
        centerX = width / 2;
        centerY = height / 2;
    }

    /**
     * This should be called first
     */
    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float tick) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, tick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        try
        {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode)
    {
        try
        {
            return super.charTyped(typedChar, keyCode);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double amount)
    {
        int mousewheeld = (int) Math.signum(amount) * Constants.BUTTONHEIGHT;
        boolean verticalscroll = Screen.hasAltDown();
        if (mousewheeld != 0)
        {
            for (Renderable button : renderables) {
                if (button instanceof Scrollable) {
                    ((Scrollable) button).scroll(mousewheeld, verticalscroll);
                }
            }
            for (ScrollList scrollList : scrollLists)
                scrollList.scroll(mousewheeld, verticalscroll);
        }
        return false;
    }

    @Override
    public boolean isPauseScreen()
    {
        return Minecraft.getInstance().player.getHealth() < 10;
    }
}
