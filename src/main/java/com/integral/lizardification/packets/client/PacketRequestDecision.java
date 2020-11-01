package com.integral.lizardification.packets.client;

import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

import com.integral.lizardification.Lizardification;
import com.integral.lizardification.handlers.DecisionHandler;
import static com.integral.lizardification.Lizardification.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

/**
 * Sent by client to ask server about given player's model rendering decision.
 * @author Integral
 */

public class PacketRequestDecision {
	private UUID playerUUID;

	public PacketRequestDecision(UUID playerUUID) {
		this.playerUUID = playerUUID;
	}

	public static void encode(PacketRequestDecision msg, PacketBuffer buf) {
		buf.writeUniqueId(msg.playerUUID);
	}

	public static PacketRequestDecision decode(PacketBuffer buf) {
		return new PacketRequestDecision(buf.readUniqueId());
	}

	/**
	 * Handled by server.
	 */

	public static void handle(PacketRequestDecision msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (ctx.get().getSender() != null) {
				ServerPlayerEntity sender = ctx.get().getSender();

				PlayerEntity player = null;
				if (ServerLifecycleHooks.getCurrentServer() != null) {
					player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(msg.playerUUID);
				}

				if (player != null)
					if (proxy.hasRenderingDecision(player)) {
						proxy.syncRenderingDecision(sender, player);
					}
			}


		});

		ctx.get().setPacketHandled(true);
	}

}


