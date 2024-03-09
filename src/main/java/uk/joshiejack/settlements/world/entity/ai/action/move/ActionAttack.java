package uk.joshiejack.settlements.world.entity.ai.action.move;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import uk.joshiejack.settlements.world.entity.NPCMob;
import uk.joshiejack.settlements.world.entity.ai.action.ActionPhysical;

//TODO@PenguinLoader("attack")
public class ActionAttack extends ActionPhysical {
    private MeleeAttackGoal ai;
    private int delayCounter;
    private int attackTick;
    private double targetX;
    private double targetY;
    private double targetZ;
    private double speed;

    public ActionAttack withData(Object... params) {
        this.speed = (double) params[0];
        return this;
    }

    @Override
    public InteractionResult execute(NPCMob attacker) {
        if (ai == null) {
            ai = new MeleeAttackGoal(attacker, 1.0D, false);
        }

        if (player != null) {
            attacker.setTarget(player);
            attacker.getLookControl().setLookAt(player, 30.0F, 30.0F);
            double d0 = attacker.distanceToSqr(player.getX(), player.getBoundingBox().minY, player.getZ());
            --delayCounter;

            if (attacker.getSensing().hasLineOfSight(player) && delayCounter <= 0 && (targetX == 0.0D && targetY == 0.0D && targetZ == 0.0D || player.distanceToSqr(targetX, targetY, targetZ) >= 1.0D || attacker.getRandom().nextFloat() < 0.05F)) {
                targetX = player.getX();
                targetY = player.getBoundingBox().minY;
                targetZ = player.getZ();
                delayCounter = 4 + attacker.getRandom().nextInt(7);

                if (d0 > 1024.0D) {
                    delayCounter += 10;
                } else if (d0 > 256.0D) {
                    delayCounter += 5;
                }

                if (!attacker.getNavigation().moveTo(player, speed)) {
                    delayCounter += 15;
                }
            }

            attackTick = Math.max(attackTick - 1, 0);
            checkAndPerformAttack(attacker, player, d0);
            return InteractionResult.PASS;
        }

        return InteractionResult.SUCCESS;
    }

    private void checkAndPerformAttack(NPCMob attacker, LivingEntity enemy, double distToEnemySqr) {
        double d0 = getAttackReachSqr(attacker, enemy);

        if (distToEnemySqr <= d0 && this.attackTick <= 0) {
            this.attackTick = 20;
            attacker.swing(InteractionHand.MAIN_HAND);
            attacker.doHurtTarget(enemy);
        }
    }

    protected double getAttackReachSqr(NPCMob attacker, LivingEntity attackTarget) {
        return attacker.getBbWidth() * 2.0F * attacker.getBbWidth() * 2.0F + attackTarget.getBbWidth();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("Speed", speed);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        speed = nbt.getDouble("Speed");
    }
}
