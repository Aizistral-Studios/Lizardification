package com.integral.lizardification;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.integral.lizardification.handlers.ClientConfigHandler;
import com.integral.lizardification.handlers.DecisionHandler;
import com.integral.lizardification.packets.client.PacketRequestDecision;
import com.integral.lizardification.packets.client.PacketSendDecision;
import com.integral.lizardification.packets.server.PacketSyncDecision;
import com.integral.lizardification.proxy.*;

import java.util.stream.Collectors;

@Mod(Lizardification.MODID)
public class Lizardification {
	public static Lizardification instance;
	public static SimpleChannel packetInstance;

	public static final String MODID = "lizardification";
	public static final String VERSION = "1.0.0";
	public static final String RELEASE_TYPE = "Release";
	public static final String NAME = "Lizardification";
	public static final CommonProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

	private static final String PTC_VERSION = "1";

	public Lizardification() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);

		MinecraftForge.EVENT_BUS.register(this);

		DecisionHandler handler = new DecisionHandler();
		MinecraftForge.EVENT_BUS.register(handler);

		ClientConfigHandler.constructConfig();
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfigHandler.clientConfig, "lizardification-client.toml");
	}

	private void loadComplete(final FMLLoadCompleteEvent event) {
		ClientConfigHandler.solidifyDecision();
		proxy.initAuxiliaryRender();
	}

	private void setup(final FMLCommonSetupEvent event) {
		packetInstance = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MODID, "main")).networkProtocolVersion(() -> PTC_VERSION).clientAcceptedVersions(PTC_VERSION::equals).serverAcceptedVersions(PTC_VERSION::equals).simpleChannel();

		packetInstance.registerMessage(0, PacketSendDecision.class, PacketSendDecision::encode, PacketSendDecision::decode, PacketSendDecision::handle);
		packetInstance.registerMessage(1, PacketRequestDecision.class, PacketRequestDecision::encode, PacketRequestDecision::decode, PacketRequestDecision::handle);
		packetInstance.registerMessage(2, PacketSyncDecision.class, PacketSyncDecision::encode, PacketSyncDecision::decode, PacketSyncDecision::handle);
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		// NO-OP
	}

	private void enqueueIMC(final InterModEnqueueEvent event) {
		// NO-OP
	}

	private void processIMC(final InterModProcessEvent event) {
		// NO-OP
	}

	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent event) {
		// NO-OP
	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents {
		@SubscribeEvent
		public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
			// NO-OP
		}
	}
}
