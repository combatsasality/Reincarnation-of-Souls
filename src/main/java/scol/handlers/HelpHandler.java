package scol.handlers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Random;

public class HelpHandler {
    public static int getRandomInRange(int min, int max) {

        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }


    public static RayTraceResult pick(PlayerEntity player) {
        Vector3d vector3d = player.getEyePosition(0.0F);
        Vector3d vector3d1 = player.getViewVector(0.0F);
        Vector3d vector3d2 = vector3d.add(vector3d1.x * 20.0, vector3d1.y * 20.0, vector3d1.z * 20.0);
        return player.level.clip(new RayTraceContext(vector3d, vector3d2, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, player));

    }
}
