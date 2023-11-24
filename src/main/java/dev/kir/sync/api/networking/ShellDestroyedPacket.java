package dev.kir.sync.api.networking;

import dev.kir.sync.Sync;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.ApiStatus;

public class ShellDestroyedPacket implements ClientPlayerPacket {
    private BlockPos pos;

    public ShellDestroyedPacket(BlockPos pos) {
        this.pos = pos == null ? BlockPos.ORIGIN : pos;
    }

    @ApiStatus.Internal
    public ShellDestroyedPacket() {
        this(null);
    }

    @Override
    public Identifier getId() {
        return Sync.locate("packet.shell.destroyed");
    }

    @Override
    public void write(PacketByteBuf buffer) {
        buffer.writeBlockPos(this.pos);
    }

    @Override
    public void read(PacketByteBuf buffer) {
        this.pos = buffer.readBlockPos();
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void execute(MinecraftClient client, ClientPlayerEntity player, ClientPlayNetworkHandler handler, PacketSender responseSender) {
        for (int i = 0; i < 3; ++i) {
            player.clientWorld.addBlockBreakParticles(this.pos, Blocks.DEEPSLATE.getDefaultState());
            player.clientWorld.addBlockBreakParticles(this.pos.up(), Blocks.DEEPSLATE.getDefaultState());
        }
        player.clientWorld.playSound(this.pos, SoundEvents.BLOCK_DEEPSLATE_BREAK, SoundCategory.BLOCKS, 1F, player.getSoundPitch(), true);
    }
}