package com.integral.lizardification.handlers;

import java.util.HashMap;

import com.integral.lizardification.Lizardification;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Lizardification.MODID)
public class DecisionHandler {

	@SubscribeEvent
	public void onEntityAdded(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof PlayerEntity && event.getWorld().isRemote) {
			PlayerEntity player = (PlayerEntity) event.getEntity();
			PlayerEntity clientPlayer = Lizardification.proxy.getClientPlayer();

			System.out.println("Client player: " + clientPlayer);

			if (clientPlayer != null) {
				if (clientPlayer == player) {
					Lizardification.proxy.sendRenderingDecision(ClientConfigHandler.allowModelForSelf);
				} else {
					Lizardification.proxy.requestRenderingDecision(player);
				}
			}
		}
	}

}
