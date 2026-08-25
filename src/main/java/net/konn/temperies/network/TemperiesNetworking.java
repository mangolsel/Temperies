package net.konn.temperies.network;

import net.konn.temperies.client.ClientTemperatureState;
import net.konn.temperies.menu.LoomTabHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class TemperiesNetworking {
    private TemperiesNetworking() {
    }

    public static void registerPayloads(
            RegisterPayloadHandlersEvent event
    ) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                TemperatureSyncPayload.TYPE,
                TemperatureSyncPayload.STREAM_CODEC,
                (payload, context) ->
                        ClientTemperatureState.setExposure(
                                payload.heatExposure(),
                                payload.coldExposure()
                        )
        );
        registrar.playToServer(
                SwitchLoomTabPayload.TYPE,
                SwitchLoomTabPayload.STREAM_CODEC,
                (payload, context) -> {

                    context.enqueueWork(() -> {

                        if (context.player()
                                instanceof ServerPlayer player) {

                            LoomTabHandler.switchTab(
                                    player,
                                    payload.pos(),
                                    payload.liningTab()
                            );
                        }
                    });
                }
        );
    }
}
