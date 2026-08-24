package me.damon;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomFov implements ModInitializer {
	public static final String NAME = "custom-fov";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Initializing " + NAME + "...");
	}
}