package dev.buildtool.satako.clientside.gui;

import net.minecraft.network.chat.Component;
@FunctionalInterface
public interface DynamicTooltip {
    Component getTooltip();
}
