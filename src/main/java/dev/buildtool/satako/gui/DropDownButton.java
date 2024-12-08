package dev.buildtool.satako.gui;

import dev.buildtool.satako.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.*;

public class DropDownButton extends BetterButton {
    private HashMap<Component, RadioButton> choices;
    private final Screen parent;
    private boolean open;
    private final List<GuiEventListener> overlappingElements = new ArrayList<>();

    public DropDownButton(int x, int y, Screen parent, Component text) {
        super(x, y, text);
        this.parent = parent;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public void onPress() {
        open = !open;
        if (open) {
            if (overlappingElements.isEmpty()) {
                choices.values().forEach(radioButton -> {
                    radioButton.setHidden(false);
                    parent.children().forEach(guiEventListener -> {
                        if (!choices.containsValue(guiEventListener) && guiEventListener != this) {
                            if (guiEventListener instanceof Positionable positionable) {
                                if (guiEventListener instanceof Hideable hideable) {
                                    for (int i = 0; i < positionable.getElementWidth(); i++) {
                                        for (int j = 0; j < positionable.getElementHeight(); j++) {
                                            if (isInsideArea(positionable.getXPos() + i, positionable.getYPos() + j, radioButton.getXPos(), radioButton.getXPos() + radioButton.getElementWidth(), radioButton.getYPos(), radioButton.getYPos() + radioButton.getElementHeight())) {
                                                hideable.setHidden(true);
                                                overlappingElements.add(guiEventListener);
                                                break;
                                            }
                                        }
                                    }
                                }
                            } else if (guiEventListener instanceof AbstractWidget abstractWidget) {
                                for (int i = 0; i < abstractWidget.getWidth(); i++) {
                                    for (int j = 0; j < abstractWidget.getHeight(); j++) {
                                        if (isInsideArea(abstractWidget.getX() + i, abstractWidget.getY() + j, radioButton.getXPos(), radioButton.getXPos() + radioButton.getElementWidth(), radioButton.getYPos(), radioButton.getYPos() + radioButton.getElementHeight())) {
                                            abstractWidget.visible = false;
                                            overlappingElements.add(guiEventListener);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    });
                });
            } else {
                choices.values().forEach(radioButton -> radioButton.setHidden(false));
                overlappingElements.forEach(guiEventListener -> {
                    if (guiEventListener instanceof Positionable) {
                        if (guiEventListener instanceof Hideable hideable) {
                            hideable.setHidden(true);
                        }
                    } else if (guiEventListener instanceof AbstractWidget abstractWidget) {
                        abstractWidget.visible = false;
                    }
                });
            }
        } else {
            choices.values().forEach(radioButton -> radioButton.setHidden(true));
            overlappingElements.forEach(guiEventListener -> {
                if (guiEventListener instanceof Positionable) {
                    if (guiEventListener instanceof Hideable hideable)
                        hideable.setHidden(false);
                } else if (guiEventListener instanceof AbstractWidget a) {
                    a.visible = true;
                }
            });
        }
    }

    @Override
    public void renderWidget(GuiGraphics poseStack, int mx, int my, float p_93660_) {
        super.renderWidget(poseStack, mx, my, p_93660_);
        if (open)
        {
            int widest=choices.values().stream().reduce((radioButton, radioButton2) -> radioButton.getElementWidth()>radioButton2.getElementWidth() ? radioButton : radioButton2).get().getElementWidth();
            choices.values().forEach(radioButton -> poseStack.fill(radioButton.getXPos(),radioButton.getYPos(),radioButton.getXPos()+widest,radioButton.getYPos()+radioButton.getElementHeight(), Constants.GRAY.getIntColor()));
            poseStack.drawString(fontRenderer, " :", getXPos() + width, getYPos() + height / 2 - 4, 0xffffffff);
        }
        else
            poseStack.drawString(fontRenderer, " V", getXPos() + width, getYPos() + height / 2 - 4, 0xffffffff);
    }

    private boolean isInsideArea(int x, int y, int x1, int x2, int y1, int y2) {
        return x >= x1 && x <= x2 && y >= y1 && y <= y2;
    }

    /**
     * @param map            text to action pairs. Actions must change this button's message and call {@link DropDownButton#onPress()} method
     * @param selectedButton initially selected button index
     */
    public void setChoices(LinkedHashMap<Component, OnPress> map, int selectedButton) {
        int offset = 1;
        ButtonGroup buttonGroup = new ButtonGroup();
        this.choices = new HashMap<>(map.size());
        for (Map.Entry<Component, OnPress> entry : map.entrySet()) {
            Component component = entry.getKey();
            OnPress onPress1 = entry.getValue();
            RadioButton radioButton = new RadioButton(getXPos(), getYPos() + 20 * offset++, component, onPress1);
            radioButton.setHidden(true);
            radioButton.selected = selectedButton + 1 - offset == -1;
            if (radioButton.selected)
                setMessage(radioButton.getMessage());
            this.parent.addRenderableWidget(radioButton);
            this.choices.put(component, radioButton);
            buttonGroup.add(radioButton);
            if (radioButton.getElementWidth() > getElementWidth())
                this.width = radioButton.getElementWidth();
        }
        buttonGroup.connect();
    }
}
