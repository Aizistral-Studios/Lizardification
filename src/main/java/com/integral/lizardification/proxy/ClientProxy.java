package com.integral.lizardification.proxy;

import java.util.HashMap;
import java.util.Map;

import com.integral.lizardification.Lizardification;
import com.integral.lizardification.client.renderers.LizardLayer;
import com.integral.lizardification.handlers.ClientConfigHandler;
import com.integral.lizardification.packets.client.PacketRequestDecision;
import com.integral.lizardification.packets.client.PacketSendDecision;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;

public class ClientProxy extends CommonProxy {
	private static final HashMap<PlayerEntity, Boolean> decisionMapClient = new HashMap<>();

	@Override
	public void initAuxiliaryRender() {
		Map<String, PlayerRenderer> skinMap = Minecraft.getInstance().getRenderManager().getSkinMap();

		PlayerRenderer renderSteve;
		PlayerRenderer renderAlex;

		renderSteve = skinMap.get("default");
		renderAlex = skinMap.get("slim");

		renderSteve.addLayer(new LizardLayer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>(renderSteve));
		renderAlex.addLayer(new LizardLayer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>(renderAlex));
	}

	@Override
	public void syncRenderingDecision(PlayerEntity player, PlayerEntity target) {
		// NO-OP, because only server should sync decision between clients
	}

	@Override
	public void syncRenderingDecision(PlayerEntity target, int blockRadius) {
		// NO-OP
	}

	@Override
	public void requestRenderingDecision(PlayerEntity player) {
		Lizardification.packetInstance.send(PacketDistributor.SERVER.noArg(), new PacketRequestDecision(player.getUniqueID()));
	}

	@Override
	public void sendRenderingDecision(boolean decision) {
		Lizardification.packetInstance.send(PacketDistributor.SERVER.noArg(), new PacketSendDecision(decision));
	}

	@Override
	public boolean hasRenderingDecision(PlayerEntity player) {
		return decisionMapClient.containsKey(player);
	}

	@Override
	public Boolean getRenderingDecision(PlayerEntity player) {
		return decisionMapClient.get(player);
	}

	@Override
	public HashMap<PlayerEntity, Boolean> getDecisionMap() {
		return decisionMapClient;
	}

	@Override
	public PlayerEntity getClientPlayer() {
		return Minecraft.getInstance().player;
	}

}
