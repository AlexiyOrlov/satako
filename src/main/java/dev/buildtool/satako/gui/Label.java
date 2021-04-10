package dev.buildtool.satako.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

/**
 * Label is a string for use in GUIs
 */
public class Label extends Button
{
    @SuppressWarnings("ConstantConditions")
    public Label(int x, int y, ITextComponent text) {
        super(x,y,Minecraft.getInstance().font.width(text.getString())+8, 18, text,null);
    }

    @Override
    public void onPress() {

    }


    @Override
    public void renderButton(MatrixStack matrixStack,int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        drawString(matrixStack,Minecraft.getInstance().font,this.getMessage(), this.x, this.y + (this.height - 8) / 2, 16777215 | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }
}
