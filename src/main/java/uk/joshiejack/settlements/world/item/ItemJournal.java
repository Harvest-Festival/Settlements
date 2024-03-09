package uk.joshiejack.settlements.world.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import uk.joshiejack.penguinlib.world.item.BookItem;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class ItemJournal extends BookItem {
    public ItemJournal(Item.Properties properties, Supplier<MenuProvider> provider) {
        super(properties, provider);
    }

    @Nonnull
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (player instanceof ServerPlayer) {
            //TODO PenguinNetwork.sendToClient(new PacketSyncInformation(AdventureDataLoader.get(level).getInformation(player)), (ServerPlayer) player);
        }

        return super.use(level, player, hand);
    }
}
