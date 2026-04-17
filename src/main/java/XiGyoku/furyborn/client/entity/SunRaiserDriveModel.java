package XiGyoku.furyborn.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class SunRaiserDriveModel<T extends LivingEntity> extends EntityModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("furyborn", "sunraiser_drive"), "main");
    private final ModelPart bone;

    public SunRaiserDriveModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(84, 27).addBox(-9.0F, -9.0F, -1.0F, 15.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(104, 73).addBox(3.0F, 0.0F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(98, 105).addBox(4.0F, 0.0F, 2.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(102, 105).addBox(4.0F, 0.0F, -3.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(46, 30).addBox(-17.0F, 1.0F, -1.0F, 20.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(86, 15).addBox(2.0F, 1.0F, 3.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(90, 30).addBox(2.0F, 1.0F, -5.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(30, 85).addBox(2.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(104, 81).addBox(2.0F, 4.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(92, 90).addBox(-7.0F, -1.0F, 1.0F, 8.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(86, 34).addBox(-7.0F, -2.0F, -3.0F, 8.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(86, 41).addBox(-7.0F, 5.0F, -3.0F, 8.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(30, 94).addBox(-7.0F, -1.0F, -2.0F, 8.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(86, 73).addBox(-4.0F, -3.0F, 5.0F, 8.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 74).addBox(-4.0F, 7.0F, -5.0F, 8.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(46, 19).addBox(-5.0F, -4.0F, -5.0F, 9.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(86, 62).addBox(-5.0F, -3.0F, -6.0F, 9.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(86, 55).addBox(-3.0F, -6.0F, -3.0F, 7.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(64, 97).addBox(4.0F, -6.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(64, 101).addBox(4.0F, 1.0F, 6.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(16, 101).addBox(-3.0F, -1.0F, 7.0F, 7.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(64, 105).addBox(4.0F, 8.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(86, 48).addBox(-4.0F, 9.0F, -3.0F, 8.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(92, 105).addBox(4.0F, 1.0F, -8.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(32, 101).addBox(-3.0F, -1.0F, -8.0F, 7.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 35).addBox(5.0F, -7.0F, -9.0F, 1.0F, 21.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(86, 84).addBox(5.0F, -9.0F, -11.0F, 7.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(30, 88).addBox(5.0F, -9.0F, 7.0F, 7.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(48, 97).addBox(6.0F, -7.0F, -13.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(56, 97).addBox(6.0F, -7.0F, 11.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(96, 15).addBox(7.0F, -9.0F, -7.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(100, 30).addBox(7.0F, -9.0F, 4.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(52, 88).addBox(6.0F, -9.0F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(7.0F, -3.0F, -11.0F, 1.0F, 13.0F, 22.0F, new CubeDeformation(0.0F))
                .texOffs(92, 97).addBox(6.0F, 0.0F, -19.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 100).addBox(6.0F, 0.0F, 13.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(84, 19).addBox(-10.0F, 0.0F, 15.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(84, 23).addBox(-10.0F, 0.0F, -17.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 85).addBox(-9.0F, 1.0F, -15.0F, 1.0F, 1.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(86, 0).addBox(-9.0F, 1.0F, 1.0F, 1.0F, 1.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(46, 0).addBox(-11.0F, -9.0F, -9.0F, 2.0F, 1.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(70, 88).addBox(-11.0F, -8.0F, -10.0F, 2.0F, 19.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(38, 69).addBox(-11.0F, 11.0F, -9.0F, 2.0F, 1.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(86, 90).addBox(-11.0F, -8.0F, 9.0F, 2.0F, 19.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 12.0F, 2.0F));

        bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(38, 35).addBox(-2.0F, -14.0F, -7.0F, 2.0F, 16.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 25.0F, -2.0F, 0.0F, 0.0F, 0.0873F));
        bone.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(78, 71).addBox(-2.0F, 0.0F, 0.0F, 2.0F, 35.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(78, 34).addBox(-2.0F, 0.0F, -20.0F, 2.0F, 35.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -9.0F, 9.0F, 0.0F, 0.0F, 0.0873F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}