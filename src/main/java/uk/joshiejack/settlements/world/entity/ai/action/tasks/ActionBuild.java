package uk.joshiejack.settlements.world.entity.ai.action.tasks;

//@PenguinLoader("build")
//TODO
/*public class ActionBuild extends ActionPhysical {
    private Building building;
    private Placeable.ConstructionStage stage;
    private int index;
    private Rotation rotation;
    private BlockPos target;
    private boolean setAnimation;

    public ActionBuild() { this.setMemorable(); }
    public ActionBuild(Building building, BlockPos target, Rotation rotation) {
        this.building = building;
        this.stage = Placeable.ConstructionStage.BUILD;
        this.index = 0;
        this.rotation = rotation;
        this.target = target;
        this.setMemorable();
    }

    @Override
    public Action withData(Object... params) {
        this.building = Settlements.Registries.BUILDINGS.get(new ResourceLocation((String) params[0]));
        this.stage = Placeable.ConstructionStage.BUILD;
        this.index = 0;
        this.target = (BlockPos) params[1];
        this.rotation = (Rotation) params[2];
        this.setMemorable();
        return this;
    }

    @Override
    public boolean has(String... data) {
        Building check = Settlements.Registries.BUILDINGS.get(new ResourceLocation(data[0]));
        return check != null && check == building;
    }

    @Override
    public InteractionResult execute(EntityNPC npc) {
        if (building == null) return InteractionResult.FAIL; //Skip if the building has been removed
        //Set the animation for the builder
        if (!setAnimation) {
            setAnimation = true;
            npc.setAnimation("build");
        }

        if (npc.level().getGameTime() %10 == 0) {
            //Set the held item to hammer if we are building
            if (npc.getMainHandItem().isEmpty()) {
                npc.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.FLINT)); //TODO: Add a different item that's in vanilla
            }

            while (true) {
                Placeable current = building.getTemplate().getComponents()[index];
                boolean placed = true;
                npc.swing(InteractionHand.MAIN_HAND);
                if (current instanceof PlaceableLiving && ((PlaceableLiving) current).getEntityName().equals(Building.NPCS)) {
                    ResourceLocation toSpawn = new ResourceLocation(((PlaceableLiving) current).getTag().getString("NPC"));
                    TownServer town = AdventureDataLoader.get(npc.world).getTownByID(npc.world.provider.getDimension(), npc.getTown());
                    town.getCensus().invite(toSpawn); //Spawn the npc in on the next day
                } else placed = current.place(npc.world, target, rotation, stage, true);

                BlockPos lookTarget = BlockPosHelper.getTransformedPosition(current.getOffsetPos(), target, rotation);
                npc.getLookHelper().setLookPosition(lookTarget.getX(), lookTarget.getY(), lookTarget.getZ(), (float)npc.getHorizontalFaceSpeed(), (float)npc.getVerticalFaceSpeed());
                index++;

                if (index >= building.getTemplate().getComponents().length) {
                    index = 0; //Reset this
                    stage = stage.next();
                    if (stage == Placeable.ConstructionStage.FINISHED) {
                        TownBuilding townBuilding = new TownBuilding(building, target, rotation).setBuilt();
                        Town<?> town = AdventureDataLoader.get(npc.world).getTownByID(npc.world.provider.getDimension(), npc.getTown());
                        town.getLandRegistry().addBuilding(npc.world, townBuilding);
                        PenguinNetwork.sendToEveryone(new PacketAddBuilding(npc.world.provider.getDimension(), town.getID(), townBuilding));
                        return EnumActionResult.SUCCESS;
                    }
                }

                if (placed) {
                    break;
                }
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Building", building.id().toString());
        tag.putByte("Stage", (byte) stage.ordinal());
        tag.putInt("Index", index);
        tag.putLong("Target", target.asLong());
        tag.putByte("Rotation", (byte) rotation.ordinal());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        building = Settlements.Registries.BUILDINGS.get(new ResourceLocation(nbt.getString("Building")));
        stage = Placeable.ConstructionStage.values()[nbt.getByte("Stage")];
        index = nbt.getInt("Index");
        target = BlockPos.of(nbt.getLong("Target"));
        rotation = Rotation.values()[nbt.getByte("Rotation")];
    }
}
*/