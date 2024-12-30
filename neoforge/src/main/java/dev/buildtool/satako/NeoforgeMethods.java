package dev.buildtool.satako;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;

public class NeoforgeMethods {

    public static void transferItems(IItemHandler inputHandler, IItemHandler outputHandler, int byAmount) {
        both:
        for (int i = 0; i < inputHandler.getSlots(); i++) {
            ItemStack itemStack = inputHandler.getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                int clamped = Mth.clamp(byAmount, 1, 64);
                for (int i1 = 0; i1 < outputHandler.getSlots(); i1++) {
                    ItemStack present = outputHandler.getStackInSlot(i1);
                    if (!present.isEmpty()) {
                        ItemStack tryExtract = inputHandler.extractItem(i, clamped, true);
                        ItemStack tryInsert = outputHandler.insertItem(i1, tryExtract, true);
                        if (tryInsert.isEmpty()) {
                            tryExtract = inputHandler.extractItem(i, tryExtract.getCount(), false);
                            outputHandler.insertItem(i1, tryExtract, false);
                            break both;
                        }
                    }
                }
                for (int i1 = 0; i1 < outputHandler.getSlots(); i1++) {
                    ItemStack tryExtract = inputHandler.extractItem(i, clamped, true);
                    ItemStack tryInsert = outputHandler.insertItem(i1, tryExtract, true);
                    if (tryInsert.isEmpty()) {
                        tryExtract = inputHandler.extractItem(i, tryExtract.getCount(), false);
                        outputHandler.insertItem(i1, tryExtract, false);
                        break both;
                    }
                }
            }
        }
    }

    @Deprecated
    public static void loadConfig(Pair<ModConfigSpec, ModConfigSpec> pair, String path) {
        String s = FMLPaths.CONFIGDIR.get().resolve(path).toString();
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(s)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
//        pair.getRight().setConfig(file);
    }
}
