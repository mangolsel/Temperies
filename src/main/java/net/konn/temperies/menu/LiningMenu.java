package net.konn.temperies.menu;

import net.konn.temperies.temperature.ThermalLining;
import net.konn.temperies.util.Temperies_Tags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.ItemAbilities;

public class LiningMenu extends AbstractContainerMenu {

    private static final int ARMOR_SLOT = 0;
    private static final int MATERIAL_SLOT = 1;
    private static final int RESULT_SLOT = 2;

    private final SimpleContainer input =
            new SimpleContainer(2);

    private final ResultContainer result =
            new ResultContainer();

    private final ContainerLevelAccess access;

    private final BlockPos loomPos;

    /*
     * Клиентский конструктор.
     */
    public LiningMenu(
            int containerId,
            Inventory playerInventory,
            RegistryFriendlyByteBuf buffer
    ) {
        this(
                containerId,
                playerInventory,
                ContainerLevelAccess.NULL,
                buffer.readBlockPos()
        );
    }

    /*
     * Серверный конструктор.
     */
    public LiningMenu(
            int containerId,
            Inventory playerInventory,
            ContainerLevelAccess access,
            BlockPos loomPos
    ) {
        super(
                Temperies_Menus.LINING_MENU.get(),
                containerId
        );

        this.access = access;
        this.loomPos = loomPos;

        input.addListener(
                container -> slotsChanged(container)
        );

        addLiningSlots();
        addPlayerInventory(playerInventory);
    }

    private void addLiningSlots() {

        /*
         * БРОНЯ
         */
        this.addSlot(
                new Slot(
                        input,
                        ARMOR_SLOT,
                        37,
                        35
                ) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return stack.getItem()
                                instanceof ArmorItem;
                    }

                    @Override
                    public int getMaxStackSize() {
                        return 1;
                    }
                }
        );

        /*
         * ШЕРСТЬ / ЛЁН
         */
        this.addSlot(
                new Slot(
                        input,
                        MATERIAL_SLOT,
                        76,
                        35
                ) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return isLiningInput(stack);
                    }
                }
        );

        /*
         * РЕЗУЛЬТАТ
         */
        this.addSlot(
                new Slot(
                        result,
                        0,
                        134,
                        35
                ) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }

                    @Override
                    public void onTake(
                            Player player,
                            ItemStack stack
                    ) {
                        super.onTake(player, stack);


                        ItemStack materialStack =
                                input.getItem(MATERIAL_SLOT);

                        boolean cuttingLining =
                                materialStack.canPerformAction(
                                        ItemAbilities.SHEARS_TRIM
                                );


                        /*
                         * Забираем исходную броню.
                         */
                        input.removeItem(
                                ARMOR_SLOT,
                                1
                        );


                        if (cuttingLining) {

                            /*
                             * Ножницы не расходуются.
                             * Вместо этого теряют 1 прочность.
                             */
                            if (player instanceof ServerPlayer serverPlayer) {

                                materialStack.hurtAndBreak(
                                        1,
                                        serverPlayer.serverLevel(),
                                        serverPlayer,
                                        brokenItem -> {
                                        }
                                );
                            }

                            input.setChanged();

                        } else {

                            /*
                             * Шерсть / лён расходуются как раньше.
                             */
                            input.removeItem(
                                    MATERIAL_SLOT,
                                    1
                            );
                        }


                        access.execute(
                                (level, pos) ->
                                        level.playSound(
                                                null,
                                                pos,

                                                cuttingLining
                                                        ? SoundEvents.SHEEP_SHEAR
                                                        : SoundEvents.UI_LOOM_TAKE_RESULT,

                                                SoundSource.BLOCKS,
                                                1.0F,
                                                1.0F
                                        )
                        );


                        slotsChanged(input);
                    }
                }
        );
    }

    private boolean isLiningInput(ItemStack stack) {
        return stack.is(ItemTags.WOOL)
                || stack.is(
                Temperies_Tags.Items.COOLING_MATERIALS
        )
                || stack.canPerformAction(
                ItemAbilities.SHEARS_TRIM
        );
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);

        updateResult();
    }

    private void updateResult() {

        ItemStack armorStack =
                input.getItem(ARMOR_SLOT);

        ItemStack materialStack =
                input.getItem(MATERIAL_SLOT);


        if (!(armorStack.getItem()
                instanceof ArmorItem armorItem)) {

            result.setItem(
                    0,
                    ItemStack.EMPTY
            );

            return;
        }


        if (!isLiningInput(materialStack)) {

            result.setItem(
                    0,
                    ItemStack.EMPTY
            );

            return;
        }


        ItemStack output =
                armorStack.copyWithCount(1);


        /*
         * НОЖНИЦЫ
         *
         * Удаляем существующую подкладку.
         */
        if (materialStack.canPerformAction(
                ItemAbilities.SHEARS_TRIM
        )) {

            /*
             * Если подкладки нет,
             * ножницам здесь делать нечего.
             */
            if (!ThermalLining.hasLining(
                    armorStack
            )) {

                result.setItem(
                        0,
                        ItemStack.EMPTY
                );

                return;
            }

            ThermalLining.remove(
                    output
            );
        }

        /*
         * ШЕРСТЬ
         */
        else if (materialStack.is(
                ItemTags.WOOL
        )) {

            ThermalLining.applyWarming(
                    output,
                    armorItem
            );
        }

        /*
         * ЛЬНЯНАЯ ТКАНЬ
         */
        else if (materialStack.is(
                Temperies_Tags.Items.COOLING_MATERIALS
        )) {

            ThermalLining.applyCooling(
                    output,
                    armorItem
            );
        }


        /*
         * Не позволяем совершить операцию,
         * если результат идентичен исходной броне.
         */
        if (ItemStack.isSameItemSameComponents(
                armorStack,
                output
        )) {

            result.setItem(
                    0,
                    ItemStack.EMPTY
            );

            return;
        }


        result.setItem(
                0,
                output
        );

        broadcastChanges();
    }

    private void addPlayerInventory(
            Inventory playerInventory
    ) {

        /*
         * Основной инвентарь.
         */
        for (int row = 0; row < 3; row++) {

            for (int column = 0; column < 9; column++) {

                this.addSlot(
                        new Slot(
                                playerInventory,
                                column
                                        + row * 9
                                        + 9,
                                8
                                        + column * 18,
                                84
                                        + row * 18
                        )
                );
            }
        }

        /*
         * Хотбар.
         */
        for (int column = 0; column < 9; column++) {

            this.addSlot(
                    new Slot(
                            playerInventory,
                            column,
                            8
                                    + column * 18,
                            142
                    )
            );
        }
    }

    public BlockPos getLoomPos() {
        return loomPos;
    }

    @Override
    public boolean stillValid(Player player) {

        return AbstractContainerMenu.stillValid(
                access,
                player,
                Blocks.LOOM
        );
    }

    @Override
    public ItemStack quickMoveStack(
            Player player,
            int index
    ) {

        ItemStack resultStack =
                ItemStack.EMPTY;

        Slot slot =
                this.slots.get(index);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack =
                slot.getItem();

        resultStack =
                stack.copy();

        /*
         * Наши три слота.
         */
        if (index < 3) {

            if (!moveItemStackTo(
                    stack,
                    3,
                    39,
                    true
            )) {

                return ItemStack.EMPTY;
            }

            slot.onQuickCraft(
                    stack,
                    resultStack
            );

        } else {

            /*
             * Броня -> слот брони.
             */
            if (stack.getItem()
                    instanceof ArmorItem) {

                if (!moveItemStackTo(
                        stack,
                        ARMOR_SLOT,
                        ARMOR_SLOT + 1,
                        false
                )) {

                    return ItemStack.EMPTY;
                }

            }

            /*
             * Материал -> слот материала.
             */
            else if (isLiningInput(stack)) {

                if (!moveItemStackTo(
                        stack,
                        MATERIAL_SLOT,
                        MATERIAL_SLOT + 1,
                        false
                )) {

                    return ItemStack.EMPTY;
                }

            } else {

                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return resultStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        /*
         * Если игрок закрыл интерфейс
         * или переключил вкладку —
         * возвращаем вещи.
         */
        this.clearContainer(
                player,
                input
        );
    }
}
