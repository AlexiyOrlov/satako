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

It also adds 4 commands to the game: 
1. `killall` - similar to kill, lets you specify namespace and path separately
2. `removeall` - same as `killall`, but removes the entities instead of killing them
3. `give2` - same as `give`, but lets you specify the namesapce and path separately
4. `summon2` -same as `summon`, but lets you specify namespace and path arguments separately.

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

Versioning - Satako version format is [mod version]-[minecraft version].