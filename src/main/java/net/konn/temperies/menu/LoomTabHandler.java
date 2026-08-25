package net.konn.temperies.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.Blocks;

public final class LoomTabHandler {

    private LoomTabHandler() {
    }

    public static void switchTab(
            ServerPlayer player,
            BlockPos pos,
            boolean liningTab
    ) {

        if (!player.level()
                .getBlockState(pos)
                .is(Blocks.LOOM)) {

            return;
        }

        /*
         * Защита от пакета с координатами
         * ткацкого станка на другом конце мира.
         */
        if (player.distanceToSqr(
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5
        ) > 64.0D) {

            return;
        }

        if (liningTab) {

            MenuProvider provider =
                    new SimpleMenuProvider(
                            (containerId,
                             inventory,
                             menuPlayer) ->

                                    new LiningMenu(
                                            containerId,
                                            inventory,
                                            ContainerLevelAccess.create(
                                                    player.level(),
                                                    pos
                                            ),
                                            pos
                                    ),

                            Component.translatable(
                                    "container.temperies.lining"
                            )
                    );

            /*
             * NeoForge сам передаст BlockPos клиентскому
             * конструктору нашего LiningMenu.
             */
            player.openMenu(
                    provider,
                    pos
            );

        } else {

            /*
             * Возвращаем настоящий vanilla loom.
             */
            MenuProvider provider =
                    player.level()
                            .getBlockState(pos)
                            .getMenuProvider(
                                    player.level(),
                                    pos
                            );

            if (provider != null) {
                player.openMenu(provider);
            }
        }
    }
}
