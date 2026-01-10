package dev.buildtool.satako.client.gui;

import dev.buildtool.satako.IntegerColor;

@FunctionalInterface
public interface DynamicColor {
    IntegerColor getColor();
}
