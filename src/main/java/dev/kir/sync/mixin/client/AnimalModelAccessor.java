package dev.kir.sync.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AnimalModel.class)
@Environment(EnvType.CLIENT)
public interface AnimalModelAccessor {
    @Invoker("getHeadParts")
    Iterable<ModelPart> getHeadParts();

    @Invoker("getBodyParts")
    Iterable<ModelPart> getBodyParts();

}
