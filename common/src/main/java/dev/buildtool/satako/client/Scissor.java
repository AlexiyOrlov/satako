package dev.buildtool.satako.client;

import com.mojang.blaze3d.platform.Window;
import org.lwjgl.opengl.GL11;

import java.util.Stack;

public class Scissor {
    private static final Stack<Scissor> scissorStack=new Stack<>();

    private final int x;
    private final int y;
    private final int w;
    private final int h;

    private Scissor(int _x, int _y, int _w, int _h) {
        this.x = _x;
        this.y = _y;
        this.w = Math.max(0, _w);
        this.h = Math.max(0, _h);
    }

    public Scissor crop(int sx, int sy, int sw, int sh) {
        int x0 = Math.max(this.x, sx);
        int y0 = Math.max(this.y, sy);
        int x1 = Math.min(this.x + this.w, sx + sw);
        int y1 = Math.min(this.y + this.h, sy + sh);
        return new Scissor(x0, y0, x1 - x0, y1 - y0);
    }

    public void scissor(Window screen) {
        double scale = screen.getGuiScale();
        int sx = (int)((double)this.x * scale);
        int sy = (int)((double)(screen.getGuiScaledHeight() - (this.y + this.h)) * scale);
        int sw = (int)((double)this.w * scale);
        int sh = (int)((double)this.h * scale);
        GL11.glScissor(sx, sy, sw, sh);
    }

    public static void pushScissor(Window screen, int x, int y, int w, int h) {
        if (scissorStack.isEmpty()) {
            GL11.glEnable(3089);
        }

        Scissor scissor = scissorStack.isEmpty() ? new Scissor(x, y, w, h) : (scissorStack.lastElement()).crop(x, y, w, h);
        scissorStack.push(scissor);
        scissor.scissor(screen);
    }

    public static void popScissor(Window screen) {
        scissorStack.pop();
        if (scissorStack.isEmpty()) {
            GL11.glDisable(3089);
        } else {
            (scissorStack.lastElement()).scissor(screen);
        }
    }
}