package dev.kir.sync.networking;

import dev.kir.sync.api.networking.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class SyncPackets {
    public static void init() {
        ServerPlayerPacket.register(SynchronizationRequestPacket::new);
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        ClientPlayerPacket.register(ShellUpdatePacket::new);
        ClientPlayerPacket.register(ShellStateUpdatePacket::new);
        ClientPlayerPacket.register(SynchronizationResponsePacket::new);
        ClientPlayerPacket.register(PlayerIsAlivePacket::new);
        ClientPlayerPacket.register(ShellDestroyedPacket::new);
    }
}
