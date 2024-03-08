function onRightClickBlock(player, pos, item, hand) {
  if (hand === main_hand) {
    if (!player.level().isClient()) {
        player.give(createStack(getItem(player)));
    }
  }
}