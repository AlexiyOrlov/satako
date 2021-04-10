package dev.buildtool.satako.gui;

import dev.buildtool.satako.Functions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.lang.reflect.Field;

public class BetterButton extends ExtendedButton implements Scrollable, Positionable, Hideable
{
    public String string;
    public boolean verticalScroll, horizontalScroll;
    /**
     * By how much the button will be able to scroll
     */
    public int scrollingAmount;
    protected FontRenderer fontRenderer;

    {
        fontRenderer = Minecraft.getInstance().fontRenderer;
        scrollingAmount = height;
    }

    /**
     * @param x
     * @param y
     * @param width
     * @param height optimal height is 20
     * @param text
     */
    public BetterButton(int x, int y, int width, int height, ITextComponent text, IPressable pressable)
    {
        super(x, y, width, height, text, pressable);
        string = text.getString();
    }

    /**
     * Construct a button with optimal height and width fitted to label
     */
    public BetterButton(int x, int y, ITextComponent text)
    {
        this(x, y, Minecraft.getInstance().fontRenderer.getStringWidth(text.getString()) + 8, 20, text,
                p_onPress_1_ -> {});
        string = text.getString();
    }

    public BetterButton(int x, int y, ITextComponent text, IPressable onPress)
    {
        this(x, y, Minecraft.getInstance().fontRenderer.getStringWidth(text.getString()) + 8, 20, text, onPress);
    }

    public BetterButton(int x, int y, ITextComponent text, boolean verticalScroll_, boolean horizontalScroll_)
    {
        this(x, y, text);
        verticalScroll = verticalScroll_;
        horizontalScroll = horizontalScroll_;
    }

    public static BetterButton createPositionlessButton(ITextComponent caption)
    {
        return new BetterButton(0, 0, caption);
    }

    public void scroll(int amount, boolean vertical)
    {

        if (vertical && verticalScroll)
        {
            y += Math.signum(amount) * scrollingAmount;
        }
        else if (horizontalScroll)
            x += amount;
    }

    @Override
    public void setScrollable(boolean vertical, boolean b)
    {
        if (b)
        {
            if (vertical)
            {
                verticalScroll = true;
            }
            else
            {
                horizontalScroll = true;
            }
        }
        else
        {
            if (vertical)
            {
                verticalScroll = false;
            }
            else
            {
                horizontalScroll = false;
            }
        }
    }

    @Override
    public boolean isEnabled()
    {
        return this.active;
    }

    @Override
    public void setEnabled()
    {
        active = true;
    }

    @Override
    public void setDisabled()
    {
        active = false;
    }

//	@Override
//	public void renderButton(int mouseX, int mouseY, float partial)
//	{
//		if(texture==null)
//			super.renderButton(mouseX, mouseY, partial);
//		else{
//			if(visible)
//			{
//				Minecraft minecraft = Minecraft.getInstance();
//				minecraft.getTextureManager().bindTexture(texture);
//				renderBg(minecraft,x,y);
//			}
//		}
//	}

//	@Override
//	public void drawButton(Minecraft m, int mx, int my, float partial) {
//		if(texture ==null)
//		{
//			super.drawButton(m, mx, my,partial);
//		}
//		else
//		{
//			if(visible)
//			{
//				m.getTextureManager().bindTexture(texture);
//				drawTexturedModalRect(x, y, textureIndex, 0, width, height);
//				if(mx>=x && mx<=x+width && my>=y && my<=y+height)mouseDragged(m, mx, my);
//			}
//
//		}
//	}

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public int getX()
    {
        return x;
    }

    @Override
    public void setX(int X)
    {
        x = X;
    }

    @Override
    public int getY()
    {
        return y;
    }

    @Override
    public void setY(int Y)
    {
        y = Y;
    }

    @Override
    public void setHidden()
    {
        visible = false;
    }

    @Override
    public void setVisible()
    {
        visible = true;
    }

    public void setClickHandler(IPressable handler)
    {
        Field field= Functions.getSecureField(Button.class,0);
        if(field!=null && field.getType()==IPressable.class)
        {
            try
            {
                field.set(this,handler);
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
    }
}
