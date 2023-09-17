Satako is a library for Forge modding, which I use in many mods. It contains:

+ simple to use class for color
+ custom Slot implementation
+ convenient custom implementation of AbstractContainerMenu
+ convenient to use custom implementation of AbstractContainerScreen, which allows slots with custom colors
+ custom text field, button, label, radio button, switch button, toggle button, drop-down button
+ several rendering methods
+ functions for converting time to and from Minecraft ticks
+ randomized and unique element list implementations
+ other useful functions and methods

It also adds 3 commands to the game: `give2` and `summon2` which let you specify namespace and path of the
arguments separately.

If you want to develop mods using Satako, you just need
to add following repository:

```
maven {
        url 'https://mymavenrepo.com/repo/XXNTQu0VdMr93BjoOV1S/'
}
```

And this dependency:

```
implementation fg.deobf('dev.buildtool:satako:[version]')
```