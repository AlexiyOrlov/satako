package dev.buildtool.satako.client.gui;

import dev.buildtool.satako.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.network.chat.Component;

import java.util.*;

/**
 * A button that provides a dropdown choice selection.
 * Choice buttons must call onPress and change the message
 */
public class DropDownButton extends BetterButton {
    public HashMap<Component, RadioButton> choices;
    protected final Screen parent;
    protected boolean open;
    protected final List<GuiEventListener> overlappingElements = new ArrayList<>();

    public DropDownButton(int x, int y, Screen parent) {
        super(x, y, Component.empty());
        this.parent = parent;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public void onPress() {
        open = !open;
        if (open) {
            if (overlappingElements.isEmpty()) {
                choices.values().forEach(radioButton -> {
                    radioButton.visible = true;
                    parent.renderables.forEach(guiEventListener -> {
                        if (!choices.containsValue(guiEventListener) && guiEventListener != this) {
                            if (guiEventListener instanceof AbstractWidget abstractWidget) {
                                for (int i = 0; i < abstractWidget.getWidth(); i++) {
                                    for (int j = 0; j < abstractWidget.getHeight(); j++) {
                                        if (isInsideArea(abstractWidget.getX() + i, abstractWidget.getY() + j, radioButton.getX(), radioButton.getX() + radioButton.getWidth(), radioButton.getY(), radioButton.getY() + radioButton.getHeight())) {
                                            abstractWidget.visible = false;
                                            overlappingElements.add(abstractWidget);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    });
                });
            } else {
                choices.values().forEach(radioButton -> radioButton.visible = true);
                overlappingElements.forEach(guiEventListener -> {
                    if (guiEventListener instanceof AbstractWidget abstractWidget) {
                        abstractWidget.visible = false;
                    }
                });
            }
        } else {
            choices.values().forEach(radioButton -> radioButton.visible = false);
            overlappingElements.forEach(guiEventListener -> {
                if (guiEventListener instanceof AbstractWidget a) {
                    a.visible = true;
                }
            });
        }
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mx, int my, float p_93660_) {
        super.renderWidget(guiGraphics, mx, my, p_93660_);
        if (open) {
            int widest = choices.values().stream().reduce((radioButton, radioButton2) -> radioButton.getWidth() > radioButton2.getWidth() ? radioButton : radioButton2).get().getWidth();
            choices.values().forEach(radioButton -> TooltipRenderUtil.renderTooltipBackground(guiGraphics, radioButton.getX(), radioButton.getY(), radioButton.getX() + widest, radioButton.getY() + radioButton.getHeight(), Constants.GRAY.getIntColor()));
            guiGraphics.drawString(fontRenderer, " :", getX() + width, getY() + height / 2 - 4, 0xffffffff);
        } else
            guiGraphics.drawString(fontRenderer, " V", getX() + width, getY() + height / 2 - 4, 0xffffffff);
    }

    private boolean isInsideArea(int x, int y, int x1, int x2, int y1, int y2) {
        return x >= x1 && x <= x2 && y >= y1 && y <= y2;
    }

    /**
     * TODO remove 2nd argument
     * @param map            text to action pairs.
     * @param selectedButton initially selected button index
     */
    public void setChoices(LinkedHashMap<Component, OnPress> map, int selectedButton) {
        int offset = 1;
        ButtonGroup buttonGroup = new ButtonGroup();
        this.choices = new HashMap<>(map.size());
        for (Map.Entry<Component, OnPress> entry : map.entrySet()) {
            Component component = entry.getKey();
            OnPress onPress1 = entry.getValue();
            RadioButton radioButton = new RadioButton(getX(), getY() + 20 * offset++, component, onPress1) {
                @Override
                public void onPress() {
                    super.onPress();
                    DropDownButton.this.setMessage(getMessage());
                    DropDownButton.this.onPress();
                }
            };
            radioButton.visible = false;
            radioButton.selected = selectedButton + 1 - offset == -1;
            if (radioButton.selected)
                setMessage(radioButton.getMessage());
            this.parent.addRenderableWidget(radioButton);
            this.choices.put(component, radioButton);
            buttonGroup.add(radioButton);
            if (radioButton.getWidth() > getWidth())
                this.width = radioButton.getWidth();
        }
        buttonGroup.connect();
    }

    public void setSelectedButton(int index)
    {
        int number=0;
        for (RadioButton radioButton : choices.values()) {
            if(index==number)
            {
                radioButton.selected=true;
                setMessage(radioButton.getMessage());
                break;
            }
            else
                number++;
        }
    }
}
