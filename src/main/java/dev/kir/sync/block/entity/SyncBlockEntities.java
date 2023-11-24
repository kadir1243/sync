package dev.kir.sync.block.entity;

import dev.kir.sync.block.SyncBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.function.BiFunction;

public final class SyncBlockEntities {
    public static final BlockEntityType<ShellStorageBlockEntity> SHELL_STORAGE;
    public static final BlockEntityType<ShellConstructorBlockEntity> SHELL_CONSTRUCTOR;
    public static final BlockEntityType<TreadmillBlockEntity> TREADMILL;

    static {
        SHELL_STORAGE = register(ShellStorageBlockEntity::new, SyncBlocks.SHELL_STORAGE);
        SHELL_CONSTRUCTOR = register(ShellConstructorBlockEntity::new, SyncBlocks.SHELL_CONSTRUCTOR);
        TREADMILL = register(TreadmillBlockEntity::new, SyncBlocks.TREADMILL);
    }

    public static void init() { }

    private static <T extends BlockEntity> BlockEntityType<T> register(BiFunction<BlockPos, BlockState, T> factory, Block block) {
        Identifier id = Registry.BLOCK.getId(block);
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.create(factory::apply, block).build());
    }

}
