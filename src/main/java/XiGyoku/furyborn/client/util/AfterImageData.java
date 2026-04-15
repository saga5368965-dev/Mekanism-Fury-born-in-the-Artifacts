package XiGyoku.furyborn.client.util;

import net.minecraft.world.phys.Vec3;

public class AfterImageData {
    public final Vec3 position;
    public final float limbSwing;
    public final float limbSwingAmount;
    public final float ageInTicks;
    public final float netHeadYaw;
    public final float headPitch;
    public final float bodyYaw;

    public AfterImageData(Vec3 position, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float bodyYaw) {
        this.position = position;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
        this.bodyYaw = bodyYaw;
    }
}