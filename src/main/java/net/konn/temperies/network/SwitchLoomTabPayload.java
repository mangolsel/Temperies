package net.konn.temperies.network;

import net.konn.temperies.Temperies;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SwitchLoomTabPayload(
        BlockPos pos,
        boolean liningTab
) implements CustomPacketPayload {

    public static final Type<SwitchLoomTabPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            Temperies.MOD_ID,
                            "switch_loom_tab"
                    )
            );

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            SwitchLoomTabPayload
            > STREAM_CODEC =
            StreamCodec.composite(

                    BlockPos.STREAM_CODEC,
                    SwitchLoomTabPayload::pos,

                    ByteBufCodecs.BOOL,
                    SwitchLoomTabPayload::liningTab,

                    SwitchLoomTabPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
