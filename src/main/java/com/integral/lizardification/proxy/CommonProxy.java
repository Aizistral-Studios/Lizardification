package com.integral.lizardification.proxy;

import java.util.HashMap;

import javax.annotation.Nullable;

import com.integral.lizardification.Lizardification;
import com.integral.lizardification.handlers.ClientConfigHandler;
import com.integral.lizardification.packets.client.PacketSendDecision;
import com.integral.lizardification.packets.server.PacketSyncDecision;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;

public class CommonProxy {
	private static final HashMap<PlayerEntity, Boolean> decisionMapServer = new HashMap<>();

	public void initAuxiliaryRender() {
		// Insert existential void here
	}

	public HashMap<PlayerEntity, Boolean> getDecisionMap() {
		return decisionMapServer;
	}

	public void syncRenderingDecision(PlayerEntity target, int blockRadius) {
		boolean targetDecision = this.hasRenderingDecision(target) ? this.getRenderingDecision(target) : false;

		if (target instanceof ServerPlayerEntity) {
			Lizardification.packetInstance.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(target.getPosX(), target.getPosY(), target.getPosZ(), blockRadius, target.world.getDimensionKey())), new PacketSyncDecision(target.getUniqueID(), targetDecision));
		}
	}

	/**
	 * Sync rendering decision of target player to client.
	 * @param player the player to sync to
	 * @param target the target player who's checked for decision
	 */

	public void syncRenderingDecision(PlayerEntity player, PlayerEntity target) {
		boolean targetDecision = this.hasRenderingDecision(target) ? this.getRenderingDecision(target) : false;

		if (player instanceof ServerPlayerEntity) {
			Lizardification.packetInstance.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new PacketSyncDecision(target.getUniqueID(), targetDecision));
		}
	}

	/**
	 * Sync client-sided rendering decision to server.
	 * @param decision
	 */

	public void sendRenderingDecision(boolean decision) {
		// NO-OP, because only clients should send their decisions
	}

	/**
	 * Kindly ask server about given player's decision on model rendering.
	 * @param player
	 */

	public void requestRenderingDecision(PlayerEntity player) {
		// NO-OP, because only clients should request other's decisions
	}

	public boolean hasRenderingDecision(PlayerEntity player) {
		return decisionMapServer.containsKey(player);
	}

	public Boolean getRenderingDecision(PlayerEntity player) {
		return decisionMapServer.get(player);
	}

	@Nullable
	public PlayerEntity getClientPlayer() {
		return null;
	}

}
