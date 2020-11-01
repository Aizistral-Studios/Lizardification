package com.integral.lizardification.packets.server;

import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

import com.integral.lizardification.Lizardification;
import com.integral.lizardification.handlers.DecisionHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Sent by server to tell client given player's rendering decision.
 * @author Integral
 */

public class PacketSyncDecision {
	private UUID playerUUID;
	private boolean decision;

	public PacketSyncDecision(UUID playerUUID, boolean decision) {
		this.playerUUID = playerUUID;
		this.decision = decision;
	}

	public static void encode(PacketSyncDecision msg, PacketBuffer buf) {
		buf.writeUniqueId(msg.playerUUID);
		buf.writeBoolean(msg.decision);
	}

	public static PacketSyncDecision decode(PacketBuffer buf) {
		return new PacketSyncDecision(buf.readUniqueId(), buf.readBoolean());
	}

	/**
	 * Handled by client.
	 */

	public static void handle(PacketSyncDecision msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			PlayerEntity player = Minecraft.getInstance().world.getPlayerByUuid(msg.playerUUID);

			if (player != null) {
				Lizardification.proxy.getDecisionMap().put(player, msg.decision);
			}


		});

		ctx.get().setPacketHandled(true);
	}

}


