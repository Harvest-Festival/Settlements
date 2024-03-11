package uk.joshiejack.settlements.world.entity;

import com.google.common.collect.Sets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.event.NPCEvent;
import uk.joshiejack.settlements.network.npc.PacketSetAnimation;
import uk.joshiejack.settlements.world.entity.ai.EntityAIActionQueue;
import uk.joshiejack.settlements.world.entity.ai.EntityAISchedule;
import uk.joshiejack.settlements.world.entity.ai.EntityAITalkingTo;
import uk.joshiejack.settlements.world.entity.ai.action.chat.AskAction;
import uk.joshiejack.settlements.world.entity.ai.action.chat.LookAction;
import uk.joshiejack.settlements.world.entity.animation.Animation;
import uk.joshiejack.settlements.world.entity.npc.Age;
import uk.joshiejack.settlements.world.entity.npc.DynamicNPC;
import uk.joshiejack.settlements.world.entity.npc.NPC;
import uk.joshiejack.settlements.world.entity.npc.NPCInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.UUID;

public class NPCMob extends AgeableMob implements IEntityWithComplexSpawn {
    protected static final EntityDataAccessor<Byte> DATA_PLAYER_MODE_CUSTOMISATION = SynchedEntityData.defineId(NPCMob.class, EntityDataSerializers.BYTE);
    public float renderOffsetX, renderOffsetY, renderOffsetZ;
    protected NPCInfo npc;
    private final Set<Player> talkingTo = Sets.newHashSet();
    private int town = 0; //Default unset value
    private Animation animation;
    private EntityAIActionQueue physicalAI;
    private EntityAIActionQueue mentalAI;
    private EntityAIActionQueue lookAI;
    private int lifespan;
    private boolean lootDisabled;

    public NPCMob(EntityType<? extends AgeableMob> type, Level world) {
        this(type, world, NPC.NULL);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, @NotNull AgeableMob parent) {
        ResourceLocation uniqueID = new ResourceLocation("custom", UUID.randomUUID().toString());
        DynamicNPC.Builder builder = new DynamicNPC.Builder(level.random, uniqueID);
        //TODO: Maybe move to town.getCensus().createCustomNPC???
        NPCMob npc = new NPCMob((EntityType<? extends AgeableMob>) parent.getType(), level, builder.build());
        npc.setAge(-24000);
        return npc;
    }

    public NPCMob(EntityType<? extends AgeableMob> type, Level world, @Nonnull NPCInfo npc) {
        super(type, world);
        this.npc = npc;
        this.setPersistenceRequired();
        this.initNPCData();
        setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        if (!world.isClientSide) {
            this.getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, (byte) 0);
        }
    }

    @SuppressWarnings("unchecked")
    public NPCMob(NPCMob entity) {
        this((EntityType<? extends AgeableMob>) entity.getType(), entity.level(), entity.npc);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_PLAYER_MODE_CUSTOMISATION, (byte) 0);
    }

    public void setDropItemsWhenDead(boolean dropWhenDead) {
        this.lootDisabled = !dropWhenDead;
    }

    private void initNPCData() {
        if (npc == null || npc.getNPCClass() == null)
            this.setRemoved(RemovalReason.DISCARDED);
        else {
            this.lifespan = npc.getNPCClass().lifespan();
            float modifier = npc.getNPCClass().height();
            //TODO? setSize(0.6F * modifier, 1.6F * modifier);
            setInvulnerable(npc.getNPCClass().invulnerable());
            //TODO>? this.noClip = info.getNPCClass().isImmovable();
            setNoGravity(npc.getNPCClass().immovable());
        }
    }

    @Override
    protected void dropFromLootTable(@NotNull DamageSource source, boolean hmm) {
        if (!lootDisabled) super.dropFromLootTable(source, hmm);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld(); //Call the server trackers for the spawning of this npc
        if (!level().isClientSide)
            npc.callScript("onNPCSpawned", this);
    }

    @Override
    protected void registerGoals() {
        ((GroundPathNavigation) this.getNavigation()).setCanPassDoors(true);
        ((GroundPathNavigation) this.getNavigation()).setCanOpenDoors(true);
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(0, new EntityAITalkingTo(this));
        //TODO? goalSelector.addGoal(1, new EntityAIWatchClosest(this, Player.class, 8.0F));
        //TODO? goalSelector.addGoal(4, new EntityAIOpenDoor(this, true));
        physicalAI = new EntityAIActionQueue(this, Goal.Flag.MOVE);
        mentalAI = new EntityAIActionQueue(this, Goal.Flag.TARGET);
        lookAI = new EntityAIActionQueue(this, Goal.Flag.LOOK);
        goalSelector.addGoal(5, lookAI);
        goalSelector.addGoal(6, mentalAI);
        goalSelector.addGoal(6, physicalAI);
        goalSelector.addGoal(7, new EntityAISchedule(this));
        //TODO? goalSelector.addGoal(9, new EntityAIWatchClosest(this, Player.class, 3.0F, 1.0F));
        //TODO? goalSelector.addGoal(9, new EntityAIWatchClosest(this, EntityNPC.class, 5.0F, 0.02F));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    public boolean isModelPartShown(PlayerModelPart p_36171_) {
        return (this.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION) & p_36171_.getMask()) == p_36171_.getMask();
    }

   /* @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        float f = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        int i = 0;

        if (entityIn instanceof EntityLivingBase) {
            f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((EntityLivingBase) entityIn).getCreatureAttribute());
            i += EnchantmentHelper.getKnockbackModifier(this);
        }

        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), f);

        if (flag) {
            if (i > 0) {
                ((EntityLivingBase) entityIn).knockBack(this, (float) i * 0.5F, MathHelper.sin(this.rotationYaw * 0.017453292F), -MathHelper.cos(this.rotationYaw * 0.017453292F));
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }

            int j = EnchantmentHelper.getFireAspect(this);

            if (j > 0) {
                entityIn.setFire(j * 4);
            }

            if (entityIn instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) entityIn;
                ItemStack itemstack = getHeldItemMainhand();
                ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.EMPTY;

                if (!itemstack.isEmpty() && !itemstack1.isEmpty() && itemstack.getItem().canDisableShield(itemstack, itemstack1, entityplayer, this) && itemstack1.getItem().isShield(itemstack1, entityplayer)) {
                    float f1 = 0.25F + (float) EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;
                    if (rand.nextFloat() < f1) {
                        entityplayer.getCooldownTracker().setCooldown(itemstack1.getItem(), 100);
                        world.setEntityState(entityplayer, (byte) 30);
                    }
                }
            }

            applyEnchantments(this, entityIn);
        }

        return flag;
    }*/

    @SuppressWarnings("all")
    @Override
    public ResourceLocation getDefaultLootTable() {
        return npc.getLootTable();
    }

    /*public int getTown() {
        //If we have the default town id, grab the closest town to us and SET it
        if (town == 0 && !dead) {
            town = TownFinder.find(world, getPosition()).getID();
        }

        return town;
    } */

    public EntityAIActionQueue getMentalAI() {
        return mentalAI;
    }

    public EntityAIActionQueue getPhysicalAI() {
        return physicalAI;
    }

    public EntityAIActionQueue getLookAI() {
        return lookAI;
    }

    @Override
    @Nonnull
    public Component getName() {
        return npc.name();
    }

    public boolean IsNotTalkingTo(Player player) {
        return !talkingTo.contains(player);
    }

    public void addTalking(Player player) {
        talkingTo.add(player);
    }

    public void removeTalking(Player player) {
        talkingTo.remove(player);
    }

    public Set<Player> getTalkingTo() {
        return talkingTo;
    }

    @Override
    public boolean isBaby() {
        return npc.getNPCClass().age() == Age.CHILD;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    public @NotNull InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        boolean flag = held.getItem() instanceof SpawnEggItem;
        if (!flag && isAlive()) {
            if (player instanceof ServerPlayer serverPlayer) {
                NeoForge.EVENT_BUS.post(new NPCEvent.NPCRightClickedEvent(this, player, hand));
                if (mentalAI.getCurrent() == null && mentalAI.all().isEmpty() && talkingTo.isEmpty()) {
                    //If a quest has been started by the event, we'd know this if the npc is talking
                    //If they aren't talking, open the random chat
                    lookAI.addToEnd(new LookAction().withPlayer(serverPlayer));
                    //mentalAI.addToEnd(new GreetAction().withPlayer(serverPlayer));
                    mentalAI.addToEnd(new AskAction(false, "How are you?", "Good->callback1", "Bad->callback2", "Well->callback3", "What->callback4", "Hmm->callback5").withPlayer(serverPlayer).withScript(new ResourceLocation("settlements", "npcs/greet"), false));
                }

                lifespan = npc.getNPCClass().lifespan(); //Reset the lifespan
            }

            return InteractionResult.SUCCESS;
        } else {
            return super.mobInteract(player, hand);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        updateSwingTime();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && animation != null) {
            animation.play(this);
        }

        if (!level().isClientSide) {
            npc.callScript("onNPCUpdate", this);
        }

        if (!level().isClientSide && talkingTo.isEmpty() && physicalAI.all().isEmpty() && mentalAI.all().isEmpty()) {
            if (npc.getNPCClass().lifespan() > 0) {
                lifespan--;
                if (lifespan <= 0) {
                    dropFromLootTable(damageSources().magic(), false);
                    this.setRemoved(RemovalReason.DISCARDED); //Kill the bitch
                }
            }
        }
    }

    @Override
    public boolean canDrownInFluidType(@NotNull FluidType type) {
        return !npc.getNPCClass().underwater() && super.canDrownInFluidType(type);
    }

    @Override
    public void move(@NotNull MoverType type, @NotNull Vec3 vec3) {
        if (!npc.getNPCClass().floats()) {
            super.move(type, vec3);
        }
    }

    /*
    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (npc.getNPCClass().floats()) {
            if (isInWater()) {
                moveRelative(strafe, vertical, forward, 0.02F);
                move(MoverType.SELF, motionX, motionY, motionZ);
                motionX *= 0.800000011920929D;
                motionY *= 0.800000011920929D;
                motionZ *= 0.800000011920929D;
            } else if (isInLava()) {
                moveRelative(strafe, vertical, forward, 0.02F);
                move(MoverType.SELF, motionX, motionY, motionZ);
                motionX *= 0.5D;
                motionY *= 0.5D;
                motionZ *= 0.5D;
            } else {
                float f = 0.91F;

                if (onGround) {
                    BlockPos underPos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ));
                    IBlockState underState = this.world.getBlockState(underPos);
                    f = underState.getBlock().getSlipperiness(underState, this.world, underPos, this) * 0.91F;
                }

                float f1 = 0.16277136F / (f * f * f);
                moveRelative(strafe, vertical, forward, onGround ? 0.1F * f1 : 0.02F);
                f = 0.91F;

                if (onGround) {
                    BlockPos underPos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ));
                    IBlockState underState = this.world.getBlockState(underPos);
                    f = underState.getBlock().getSlipperiness(underState, this.world, underPos, this) * 0.91F;
                }

                move(MoverType.SELF, motionX, motionY, motionZ);
                motionX *= f;
                motionY *= f;
                motionZ *= f;
            }

            prevLimbSwingAmount = limbSwingAmount;
            limbSwingAmount = 0F;
            limbSwing = 0F;
        } else super.travel(strafe, vertical, forward);
    } */

    //TODO:
    /*
    @Override
    public void onDeath(@Nonnull DamageSource cause) {
        super.onDeath(cause);
        if (!world.isRemote) {
            TownServer town = AdventureDataLoader.get(world).getTownByID(dimension, getTown());
            this.town = 0; //Clear out this npcs town
            town.getCensus().onNPCDeath(this); //Remember my actions
            town.getCensus().onNPCsChanged(this.world); //Update invitable list
            AdventureDataLoader.get(this.world).markDirty(); //Save Stuff
        }
    }*/

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.getTagType("NPC") == CompoundTag.TAG_STRING)
            npc = Settlements.Registries.NPCS.getOrEmpty(new ResourceLocation(nbt.getString("NPC")));
        else if (nbt.getTagType("NPC") == CompoundTag.TAG_COMPOUND) {
            npc = DynamicNPC.fromTag(nbt.getCompound("NPC"));
        }

        //TODO? if (npc == NPC.NULL) this.setRemoved(RemovalReason.DISCARDED); //Kill off null npcs
        physicalAI.deserializeNBT(nbt.getList("PhysicalActions", 10));
        mentalAI.deserializeNBT(nbt.getList("MentalActions", 10));
        lootDisabled = nbt.getBoolean("LootDisabled");
        town = nbt.getInt("Town");
        initNPCData(); //Reload in the data where applicable
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        if (npc instanceof NPC)
            nbt.putString("NPC", npc.id().toString());
        else if (npc instanceof DynamicNPC dynamic) {
            nbt.put("NPC", dynamic.toTag());
        }

        nbt.put("PhysicalActions", physicalAI.serializeNBT());
        nbt.put("MentalActions", mentalAI.serializeNBT());
        nbt.putBoolean("LootDisabled", lootDisabled);
        nbt.putInt("Town", town);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buf) {
        buf.writeBoolean(animation != null);
        if (animation != null) {
            buf.writeUtf(animation.getID());
        }

        buf.writeBoolean(npc instanceof NPC);
        if (npc instanceof NPC npc2)
            PenguinNetwork.writeRegistry(npc2, buf);
        else if (npc instanceof DynamicNPC dynamic)
            buf.writeNbt(dynamic.toTag());
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buf) {
        if (buf.readBoolean()) setAnimation(buf.readUtf()); //Animation bitch
        boolean isNormalNPC = buf.readBoolean();
        if (isNormalNPC)
            npc = PenguinNetwork.readRegistry(Settlements.Registries.NPCS, buf);
        else {
            CompoundTag tag = buf.readNbt();
            if (tag != null)
                npc = DynamicNPC.fromTag(tag);
        }

        initNPCData(); //Update the data on the client side too
    }

    public void setAnimation(String animation, Object... object) {
        try {
            this.animation = Animation.create(animation).withData(object);
            PenguinNetwork.sendToNearby(this, new PacketSetAnimation(getId(), this.animation.getID(), this.animation.serializeNBT()));
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void setAnimation(String animation, CompoundTag tag) {
        try {
            this.animation = animation.isEmpty() ? null : Animation.create(animation);
            if (this.animation != null) {
                this.animation.deserializeNBT(tag);
            }
        } catch (IllegalAccessException | InstantiationException ignored) {
        }
    }

    @Nonnull
    public NPCInfo getNPC() {
        return npc;
    }

    public Animation getAnimation() {
        return animation;
    }
}