package me.damon;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomFov implements ModInitializer {
	public static final String NAME = "custom-fov";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing " + NAME + "...");
	}
}