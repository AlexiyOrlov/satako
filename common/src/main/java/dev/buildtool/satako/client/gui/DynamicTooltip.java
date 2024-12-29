package dev.buildtool.satako.client.gui;

import net.minecraft.network.chat.Component;

@FunctionalInterface
public interface DynamicTooltip {
    Component getTooltip();
}
