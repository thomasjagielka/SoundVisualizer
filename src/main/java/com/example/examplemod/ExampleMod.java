package com.example.examplemod;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExampleMod.MODID)
public class ExampleMod
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "examplemod";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    private static String lastName = "block.amethyst_block";

    // Define a resource location for the circle texture
    public ExampleMod()
    {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private static int circleX = 0, circleY = 0;

    public static IGuiOverlay HUD_PLAYER = (gui, poseStack, partialTicks, screenWidth, screenHeight) -> {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        // RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        ResourceLocation CIRCLE_TEXTURE = new ResourceLocation(MODID, "hud/" + lastName + ".png");

        LOGGER.info(lastName);
        RenderSystem.setShaderTexture(0, CIRCLE_TEXTURE);
        int circleXFixed = screenWidth / 2 - circleX;
        int circleYFixed = screenHeight / 2 - circleY;
        GuiComponent.blit(poseStack, circleXFixed, circleYFixed, 0, 0, 12, 12, 12, 12);
    };

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("player", HUD_PLAYER);
        }
    }

    // Subscribe to an event that is fired every tick on the client side
//    @SubscribeEvent
//    public void onClientTick(TickEvent.ClientTickEvent event) {
//        // Get the Minecraft instance and the player entity
//        Minecraft mc = Minecraft.getInstance();
//        Entity player = mc.player;
//        // Check if the player exists and the game is not paused
//        if (player != null && !mc.isPaused()) {
//            // Get a list of all entities within 10 blocks of the player
//            List<Entity> entities = player.level.getEntities(player, player.getBoundingBox().inflate(10));
//            // Remove the player from the list
//            entities.remove(player);
//            // Check if there are any other entities nearby
//            if (!entities.isEmpty()) {
//                // Sort the list by distance to the player
//                entities.sort(Comparator.comparingDouble(player::distanceToSqr));
//                // Get the first entity in the list, which is the closest one
//                Entity closest = entities.get(0);
//                // Log some information about the closest entity, such as its name and position
//                LogManager.getLogger().info("The closest entity to you is: " + closest.getName().getString());
//                LogManager.getLogger().info("Its position is: " + closest.getX() + ", " + closest.getY() + ", " + closest.getZ());
//            }
//        }
//    }

    // Subscribe to an event that is fired when a sound is played at an entity
    @SubscribeEvent
    public void onSoundPlayed(PlaySoundEvent event) {
        // Get the sound event and the sound source
        SoundInstance sound = event.getSound();
        // Check if the sound and the source are not null
        if (sound != null) {
            // Log some information about the sound, such as its name and source
//            LogManager.getLogger().info("2A sound was played: " + event.getName());
            double soundX = sound.getX();
            double soundY = sound.getY();
            double soundZ = sound.getZ();

            // Get the player's position and direction
            LocalPlayer player = Minecraft.getInstance().player;

            if (player != null) {
                double playerX = player.getX();
                double playerY = player.getY();
                double playerZ = player.getZ();
                double playerYaw = player.yRotO;

                // Calculate the angle between the player's forward direction and the sound direction
                double angle = Math.atan2(soundZ - playerZ, soundX - playerX) - Math.toRadians(playerYaw);

                // Convert the angle to screen coordinates
                int circleRadius = 50; // adjust this as necessary

                // Convert the angle to screen coordinates
                circleX = (int) (circleRadius * Math.cos(angle));
                circleY = (int) (circleRadius * Math.sin(angle));
                lastName = event.getName().substring(0, event.getName().lastIndexOf("."));
                LogManager.getLogger().info("2A sound was played: " + lastName);
            }
        }
    }
}
