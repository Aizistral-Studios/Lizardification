package com.integral.lizardification.handlers;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfigHandler {
	public static ForgeConfigSpec clientConfig;

	public static boolean allowModelForOthers;
	public static boolean allowModelForSelf;

	private static ForgeConfigSpec.BooleanValue allowModelForOthersSpec;
	private static ForgeConfigSpec.BooleanValue allowModelForSelfSpec;

	public static void constructConfig() {
		final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

		builder.comment("General mod config").push("Generic Config");

		allowModelForOthersSpec = builder
				.comment("If set to false, custom model parts won't render on other players regardless of "
						+ "whether they have enabled those for themselves.")
				.define("allowModelForOthers", true);

		allowModelForSelfSpec = builder
				.comment("Set to true to enable rendering of custom model parts for yourself, with textures "
						+ "being drawn from your skin layout. Your decision will be synced to other players.")
				.define("allowModelForSelf", false);

		builder.pop();

		clientConfig = builder.build();
	}

	public static void solidifyDecision() {
		allowModelForOthers = allowModelForOthersSpec.get();
		allowModelForSelf = allowModelForSelfSpec.get();
	}
}
