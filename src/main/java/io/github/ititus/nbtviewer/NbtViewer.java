package io.github.ititus.nbtviewer;

import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;

@Mod(NbtViewer.MOD_ID)
public class NbtViewer {

    public static final String MOD_ID = "nbtviewer";

    public NbtViewer() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(() -> FMLNetworkConstants.IGNORESERVERONLY, (remoteVersion, isNetwork) -> true));
    }
}
