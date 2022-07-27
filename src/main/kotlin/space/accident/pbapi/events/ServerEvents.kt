package space.accident.pbapi.events

import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.common.MinecraftForge
import space.accident.pbapi.api.API
import space.accident.pbapi.config.Config

class ServerEvents {

    companion object {
        fun register() {
            ServerEvents().let {
                FMLCommonHandler.instance().bus().register(it)
                MinecraftForge.EVENT_BUS.register(it)
            }
        }
    }

    @SubscribeEvent
    fun onPlayerLogin(event: PlayerLoggedInEvent) {
        val player = event.player as EntityPlayerMP
        API.playerLogin(player.gameProfile.name)
    }

    @SubscribeEvent
    fun onPlayerLogout(event: PlayerLoggedOutEvent) {
        val player = event.player as EntityPlayerMP
        API.playerLogout(player.gameProfile.name)
    }

    var saveTicker = 0

    @SubscribeEvent
    fun onWorldTick(e: WorldTickEvent) {
        if (FMLCommonHandler.instance().effectiveSide.isServer) {
            if (e.world.provider.dimensionId == 0) {
                saveTicker++
                if (saveTicker >= (1200 * Config.SAVE_TIME)) {
                    API.saveNBT()
                    saveTicker = 0
                }
            }
        }
    }
}