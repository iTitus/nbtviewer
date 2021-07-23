package io.github.ititus.nbtviewer.datagen;

import io.github.ititus.nbtviewer.NbtViewer;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = NbtViewer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NbtViewerDataGenerator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        if (event.includeClient()) {
            gen.addProvider(new Language(gen));
        }
    }

    private static class Language extends LanguageProvider {

        public Language(DataGenerator gen) {
            super(gen, NbtViewer.MOD_ID, "en_us");
        }

        @Override
        protected void addTranslations() {
            add("key.categories.nbtviewer", "NBT Viewer");
            add("key.nbtviewer.inspect_item", "Inspect Item");
            add("key.nbtviewer.inspect_world", "Inspect Object In World");
            add("gui.nbtviewer.title", "NBT Viewer");
        }
    }
}
