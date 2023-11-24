package dev.kir.sync.client.render.entity;

import dev.kir.sync.api.shell.ShellState;
import dev.kir.sync.client.model.ShellModel;
import dev.kir.sync.client.texture.GeneratedTextureManager;
import dev.kir.sync.client.texture.TextureGenerators;
import dev.kir.sync.compat.iris.IrisRenderLayer;
import dev.kir.sync.entity.ShellEntity;
import dev.kir.sync.mixin.client.AnimalModelAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public class ShellEntityRenderer extends PlayerEntityRenderer {
    private final ShellModel<PlayerEntityModel<?>> shellModel;

    public ShellEntityRenderer(EntityRendererFactory.Context context, boolean slim) {
        super(context, slim);
        this.shellModel = new ShellModel<>(this.model);
        this.shadowRadius = 0;
        this.shadowOpacity = 0;
    }

    @Override
    public void render(AbstractClientPlayerEntity player, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.translate(0.5, 0, 0.5);
        if (player instanceof ShellEntity shell && !shell.isActive) {
            float progress = shell.getState().getProgress();

            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180F));
            matrices.scale(-1.0F, -1.0F, 1.0F);
            this.scale(player, matrices, yaw);
            matrices.translate(0.0D, -1.5010000467300415D, 0.0D);
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(yaw));

            this.applyStateToModel(this.shellModel, shell.getState());
            VertexConsumer consumer = this.getVertexConsumerForPartiallyTexturedEntity(shell, progress, this.shellModel.getLayer(shell.getSkinTexture()), vertexConsumers);
            this.shellModel.render(matrices, consumer, light, getOverlay(player, tickDelta), 1F, 1F, 1F, 1F);
            if (progress >= ShellState.PROGRESS_DONE) {
                for (FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> feature : this.features) {
                    feature.render(matrices, vertexConsumers, light, player, 0, 0, tickDelta, 0, 0, 0);
                }
            }
        } else {
            Direction direction = Direction.fromRotation(yaw);
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(yaw));
            if (direction == Direction.WEST || direction == Direction.EAST) {
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180F));
            }

            float maxPitch = player.getEquippedStack(EquipmentSlot.CHEST).isEmpty() ? 15 : 5;
            float pitch = maxPitch * (player instanceof ShellEntity shell ? shell.pitchProgress : 0);
            player.setPitch(pitch);
            player.prevPitch = pitch;
            super.render(player, yaw, tickDelta, matrices, vertexConsumers, light);
        }
        matrices.pop();
    }

    private static final boolean IRIS = FabricLoader.getInstance().isModLoaded("iris");
    private VertexConsumer getVertexConsumerForPartiallyTexturedEntity(ShellEntity shell, float progress, RenderLayer baseLayer, VertexConsumerProvider vertexConsumers) {
        VertexConsumer baseConsumer = vertexConsumers.getBuffer(baseLayer);

        if (IRIS) {
            if (!(progress >= ShellState.PROGRESS_PRINTING && progress < ShellState.PROGRESS_DONE)) {
                return baseConsumer;
            }

            Identifier[] textures = GeneratedTextureManager.getTextures(TextureGenerators.PlayerEntityPartiallyTexturedTextureGenerator);
            if (textures.length == 0) {
                return baseConsumer;
            }

            float printingProgress = (progress - ShellState.PROGRESS_PRINTING) / (ShellState.PROGRESS_PAINTING);
            RenderLayer printingMaskLayer = IrisRenderLayer.getPrintingMask(textures[(int)(textures.length * printingProgress)]);

            // TODO
            // Fix dev.kir.sync.compat.iris.IrisRenderLayer::getPrintingMask,
            // and then fix combining baseConsumer with printingMaskVertexConsumer
            VertexConsumer printingMaskVertexConsumer = vertexConsumers.getBuffer(printingMaskLayer);
            if (printingMaskVertexConsumer == baseConsumer) {
                return vertexConsumers.getBuffer(baseLayer);
            }
            return VertexConsumers.union(printingMaskVertexConsumer, baseConsumer);
        }

        return baseConsumer;
    }

    @Override
    protected boolean hasLabel(AbstractClientPlayerEntity player) {
        return player.shouldRenderName() && super.hasLabel(player);
    }

    protected void applyStateToModel(ShellModel<PlayerEntityModel<?>> model, ShellState state) {
        AnimalModel<?> animalModel = model.parentModel;
        ((AnimalModelAccessor)animalModel).getHeadParts().forEach(x -> x.setAngles(0, 0, 0));
        ((AnimalModelAccessor)animalModel).getBodyParts().forEach(x -> x.setAngles(0, 0, 0));
        animalModel.child = false;
        model.parentModel.setVisible(true);
        model.setBuildProgress(state.getProgress());
    }
}