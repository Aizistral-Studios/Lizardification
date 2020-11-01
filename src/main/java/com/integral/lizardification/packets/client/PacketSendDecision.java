package com.integral.lizardification.packets.client;

import java.util.Random;
import java.util.function.Supplier;

import com.integral.lizardification.Lizardification;
import com.integral.lizardification.handlers.DecisionHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Sent by client to tell the server it's own rendering decision.
 * @author Integral
 */

public class PacketSendDecision {
	private boolean decision;

	public PacketSendDecision(boolean decision) {
		this.decision = decision;
	}

	public static void encode(PacketSendDecision msg, PacketBuffer buf) {
		buf.writeBoolean(msg.decision);
	}

	public static PacketSendDecision decode(PacketBuffer buf) {
		return new PacketSendDecision(buf.readBoolean());
	}

	/**
	 * Handled by server.
	 */

	public static void handle(PacketSendDecision msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (ctx.get().getSender() != null) {
				ServerPlayerEntity sender = ctx.get().getSender();

				Lizardification.proxy.getDecisionMap().put(sender, msg.decision);
				Lizardification.proxy.syncRenderingDecision(sender, 128);
			}

		});

		ctx.get().setPacketHandled(true);
	}

}

