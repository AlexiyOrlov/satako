package dev.buildtool.satako.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.buildtool.satako.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Radio button is used in a group, when a choice for a user is provided - {@link ButtonGroup}. Only one radio button
 * in a group must be active/selected at the same time
 */
public class RadioButton extends BetterButton
{
    public boolean selected;
    private List<RadioButton> neighbours = new ArrayList<>();

    public RadioButton(int x, int y, int width, int height, ITextComponent text)
    {
        super(x, y, text);
        this.width = width;
        this.height = height;
    }

    public RadioButton(int x, int y, ITextComponent text)
    {
        this(x, y, Minecraft.getInstance().fontRenderer.getStringWidth(text.getString()) + 8, Constants.BUTTONHEIGHT, text);
    }

    public RadioButton(int x, int y, ITextComponent text, IPressable consumer)
    {
        super(x, y, text, consumer);
    }

    public void setNeighbours(RadioButton... neighbours_)
    {
        neighbours.addAll(Arrays.asList(neighbours_));
    }


    public void addNeighbour(RadioButton r)
    {
        neighbours.add(r);
    }

    @Override
    public void renderButton(MatrixStack matrixStack,int mouseX, int mouseY, float partial)
    {
        if (this.visible)
        {
            Minecraft mc = Minecraft.getInstance();
            this.isHovered = selected;// mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int k = this.getYImage(this.isHovered);
            GuiUtils.drawContinuousTexturedBox(WIDGETS_LOCATION, this.x, this.y, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, this.getBlitOffset());
            this.renderBg(matrixStack,mc, mouseX, mouseY);
            int color = getFGColor();

            if (this.isHovered && this.packedFGColor == Widget.UNSET_FG_COLOR)
                color = 0xFFFFA0; // Slightly Yellow

            ITextComponent buttonText = this.getMessage();
            int strWidth = mc.fontRenderer.getStringWidth(buttonText.getUnformattedComponentText());
            int ellipsisWidth = mc.fontRenderer.getStringWidth("...");

            //TODO
//            if (strWidth > width - 6 && strWidth > ellipsisWidth)
//                buttonText = mc.fontRenderer.trimStringToWidth(buttonText, width - 6 - ellipsisWidth).trim() + "...";

            drawCenteredString(matrixStack,mc.fontRenderer, buttonText, this.x + this.width / 2, this.y + (this.height - 8) / 2, color);
        }
    }

    //	@Override
//	public void drawButton(Minecraft mc, int mousex, int mousey, float partial) {
//		if(visible) {
//			if (texture != null)
//				mc.getTextureManager().bindTexture(texture);
//			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//			GL11.glEnable(GL11.GL_BLEND);
//			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
//			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//			int texture;
//			if (selected) texture = 2;
//			else texture = 1;
//			this.drawTexturedModalRect(this.x, this.y, 0, 46 + texture * 20, this.width / 2, this.height);
//			this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + texture * 20, this.width / 2, this.height);
//			this.mouseDragged(mc, mousex, mousey);
//			int color;
//			if (selected) color = 16777120;
//			else color = 10526880;
//			this.drawCenteredString(fontRenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, color);
//		}
//	}


    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_)
    {
        boolean pressed = super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
        if (pressed)
        {
            this.selected = true;
            neighbours.forEach(b -> b.selected = false);
        }
        return pressed;
    }

    public boolean isSelected()
    {
        return selected;
    }
}
