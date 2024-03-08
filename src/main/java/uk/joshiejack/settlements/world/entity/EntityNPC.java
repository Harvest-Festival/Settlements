package uk.joshiejack.settlements.world.entity;

import com.google.common.collect.Sets;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.settlements.AdventureDataLoader;
import uk.joshiejack.settlements.Settlements;
import uk.joshiejack.settlements.client.animation.Animation;
import uk.joshiejack.settlements.entity.ai.EntityAIActionQueue;
import uk.joshiejack.settlements.entity.ai.EntityAISchedule;
import uk.joshiejack.settlements.entity.ai.EntityAITalkingTo;
import uk.joshiejack.settlements.entity.ai.action.chat.ActionGreet;
import uk.joshiejack.settlements.event.NPCEvent;
import uk.joshiejack.settlements.network.npc.PacketSetAnimation;
import uk.joshiejack.settlements.npcs.Age;
import uk.joshiejack.settlements.npcs.DynamicNPC;
import uk.joshiejack.settlements.npcs.NPC;
import uk.joshiejack.settlements.npcs.NPCInfo;
import uk.joshiejack.settlements.util.TownFinder;
import uk.joshiejack.settlements.world.entity.ai.EntityAIActionQueue;
import uk.joshiejack.settlements.world.entity.ai.EntityAISchedule;
import uk.joshiejack.settlements.world.entity.ai.EntityAITalkingTo;
import uk.joshiejack.settlements.world.entity.ai.action.chat.ActionGreet;
import uk.joshiejack.settlements.world.entity.animation.Animation;
import uk.joshiejack.settlements.world.entity.npc.Age;
import uk.joshiejack.settlements.world.entity.npc.DynamicNPC;
import uk.joshiejack.settlements.world.entity.npc.NPC;
import uk.joshiejack.settlements.world.entity.npc.NPCInfo;
import uk.joshiejack.settlements.world.town.TownServer;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.UUID;

public class NPCMob extends AgeableMob implements IEntityWithComplexSpawn {
    public float renderOffsetX, renderOffsetY, renderOffsetZ;
    protected CompoundTag custom;
    protected NPC npc;
    protected NPCInfo info;
    private final Set<Player> talkingTo = Sets.newHashSet();
    private int town = 0; //Default unset value
    private Animation animation;
    private EntityAIActionQueue physicalAI;
    private EntityAIActionQueue mentalAI;
    private int lifespan;
    private boolean lootDisabled;

    public NPCMob(EntityType<? extends AgeableMob> type, Level world) {
        this(type, world, NPC.NULL, null);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, @NotNull AgeableMob parent) {
        ResourceLocation uniqueID = new ResourceLocation("custom", UUID.randomUUID().toString());
        CompoundTag tag = new DynamicNPC.Builder(level.random, uniqueID).build();
        //TODO: Maybe move to town.getCensus().createCustomNPC???
        NPCMob npc =  new NPCMob((EntityType<? extends AgeableMob>) parent.getType(), level, NPC.NULL, tag);
        npc.setAge(-24000);
        return npc;
    }

    public NPCMob(EntityType<? extends AgeableMob> type, Level world, @Nonnull NPC npc, @Nullable CompoundTag customData) {
        super(type, world);
        this.npc = npc;
        this.custom = customData == null ? new CompoundTag() : customData;
        this.info = custom.isEmpty() ? npc : DynamicNPC.fromTag(custom);
        this.setPersistenceRequired();
        this.initNPCData();
        setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
    }

    @SuppressWarnings("unchecked")
    public NPCMob(NPCMob entity) {
        this((EntityType<? extends AgeableMob>) entity.getType(), entity.level(), entity.npc, entity.custom);
    }

    public void setDropItemsWhenDead(boolean dropWhenDead) {
        this.lootDisabled = !dropWhenDead;
    }

    private void initNPCData() {
        if (info == null || info.getNPCClass() == null) this.setRemoved(RemovalReason.DISCARDED);
        else {
            this.lifespan = info.getNPCClass().lifespan();
            float modifier = info.getNPCClass().height();
            setSize(0.6F * modifier, 1.6F * modifier);
            setEntityInvulnerable(info.getNPCClass().invulnerable());
            this.noClip = info.getNPCClass().isImmovable();
            this.setNoGravity(info.getNPCClass().immovable());
        }
    }

    public CompoundTag getCustomData() {
        return custom;
    }

    @Override
    protected void dropFromLootTable(@NotNull DamageSource source, boolean hmm) {
        if (!lootDisabled) super.dropFromLootTable(source, hmm);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld(); //Call the server trackers for the spawning of this npc
        if (!level().isClientSide)
            info.callScript("onNPCSpawned", this);
    }

    @Override
    protected void registerGoals() {
        ((PathNavigateGround) getNavigator()).setEnterDoors(true);
        ((PathNavigateGround) getNavigator()).setBreakDoors(true);
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(0, new EntityAITalkingTo(this));
        goalSelector.addGoal(1, new EntityAIWatchClosest(this, Player.class, 8.0F));
        goalSelector.addGoal(4, new EntityAIOpenDoor(this, true));
        physicalAI = new EntityAIActionQueue(this, Goal.Flag.MOVE);
        mentalAI = new EntityAIActionQueue(this, Goal.Flag.LOOK);
        goalSelector.addGoal(6, mentalAI);
        goalSelector.addGoal(6, physicalAI);
        goalSelector.addGoal(7, new EntityAISchedule(this));
        goalSelector.addGoal(9, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
        goalSelector.addGoal(9, new EntityAIWatchClosest(this, NPCMob.class, 5.0F, 0.02F));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0D);
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

    @Override
    @Nonnull
    public Component getName() {
        return info.getLocalizedName();
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
        return info.getNPCClass().age() == Age.CHILD;
    }

    @Override
    public boolean canDespawn() {
        return false;
    }

    @Override
    public @NotNull InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        boolean flag = held.getItem() instanceof SpawnEggItem;
        if (!flag && isAlive()) {
            if (!level().isClientSide) {
                NeoForge.EVENT_BUS.post(new NPCEvent.NPCRightClickedEvent(this, player, hand));
                if (mentalAI.getCurrent() == null && mentalAI.all().isEmpty() && talkingTo.isEmpty()) {
                    //If a quest has been started by the event, we'd know this if the npc is talking
                    //If they aren't talking, open the random chat
                    mentalAI.addToEnd(new ActionGreet().withPlayer(player));
                }

                lifespan = npc.getNPCClass().getLifespan(); //Reset the lifespan
            }

            return InteractionResult.SUCCESS;
        } else {
            return super.mobInteract(player, hand);
        }
    }

    @Override
    public void onLivingUpdate() {
        updateArmSwingProgress();
        super.onLivingUpdate();
        if (world.isRemote && animation != null) {
            animation.play(this);
        }

        if (!world.isRemote) {
            npc.callScript("onNPCUpdate", this);
        }

        if (!world.isRemote && talkingTo.size() == 0 && physicalAI.all().isEmpty() && mentalAI.all().isEmpty()) {
            if (npc.getNPCClass().getLifespan() > 0) {
                lifespan--;
                if (lifespan <= 0) {
                    dropLoot(false, 0, DamageSource.MAGIC);
                    this.setDead(); //Kill the bitch
                }
            }
        }
    }

    @Override
    public boolean canBreatheUnderwater() {
        return npc.getNPCClass().canBreatheUnderwater();
    }

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
    }

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
    }

    @Override
    public void readEntityFromNBT(@Nonnull CompoundTag nbt) {
        super.readEntityFromNBT(nbt);
        npc = NPC.getNPCFromRegistry(new ResourceLocation(nbt.getString("NPC")));
        if (npc == NPC.NULL_NPC) this.setDead(); //Kill off null npcs
        physicalAI.deserializeNBT(nbt.getTagList("PhysicalActions", 10));
        mentalAI.deserializeNBT(nbt.getTagList("MentalActions", 10));
        lootDisabled = nbt.getBoolean("LootDisabled");
        town = nbt.getInteger("Town");
        custom = nbt.getCompoundTag("Custom");
        info = custom.hasNoTags() ? npc : DynamicNPC.fromTag(custom);
        initNPCData(); //Reload in the data where applicable
    }

    @Override
    public void writeEntityToNBT(@Nonnull NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setString("NPC", npc.getRegistryName().toString());
        nbt.setTag("PhysicalActions", physicalAI.serializeNBT());
        nbt.setTag("MentalActions", mentalAI.serializeNBT());
        nbt.setBoolean("LootDisabled", lootDisabled);
        nbt.setInteger("Town", town);
        nbt.setTag("Custom", custom);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buf) {
        buf.writeBoolean(animation != null);
        if (animation != null) {
            buf.writeUtf(animation.getID());
        }

        buf.writeBoolean(npc != NPC.NULL);
        PenguinNetwork.writeRegistry(npc, buf);
        buf.writeNbt(custom);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buf) {
        if (buf.readBoolean()) setAnimation(buf.readUtf()); //Animation bitch
        ResourceLocation name = buf.readBoolean() ? buf.readResourceLocation() : null;
        npc = Settlements.Registries.NPCS.getOrDefault(name, NPC.NULL);
        custom = buf.readNbt();
        assert custom != null;
        info = custom.isEmpty() ? npc : DynamicNPC.fromTag(custom);
        initNPCData(); //Update the data on the client side too
    }

    public void setAnimation(String animation, Object... object) {
        try {
            this.animation = Animation.create(animation).withData(object);
            PenguinNetwork.sendToNearby(this, new PacketSetAnimation(getEntityId(), this.animation.getID(), this.animation.serializeNBT()));
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

    public NPCInfo getInfo() {
        return info;
    }

    public NPC getBaseNPC() {
        return npc;
    }

    public String substring(String name) {
        return info.substring(name);
    }

    public Animation getAnimation() {
        return animation;
    }
}