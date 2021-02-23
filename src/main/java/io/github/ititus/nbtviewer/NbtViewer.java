package io.github.ititus.nbtviewer;

import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

@Mod(NbtViewer.MOD_ID)
public class NbtViewer {

    public static final String MOD_ID = "nbtviewer";

    public NbtViewer() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
                () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (remoteVersion, isNetwork) -> true));
    }
}
