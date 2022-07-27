package space.accident.pbapi

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.common.event.FMLServerStartedEvent
import cpw.mods.fml.common.event.FMLServerStoppedEvent
import space.accident.pbapi.config.Config
import space.accident.pbapi.events.ServerEvents
import space.accident.pbapi.extra.SaveManager

@Mod(
    modid = MODID,
    version = VERSION,
    name = MODNAME,
    modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter",
    acceptedMinecraftVersions = "[1.7.10]",
    dependencies = "required-after:forgelin;"
)
object PlayerBecomingApi {

    @Mod.InstanceFactory
    fun instance() = PlayerBecomingApi

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        Config.createConfig(event.modConfigurationDirectory)
        ServerEvents.register()
    }

    @Mod.EventHandler
    fun serverStarted(event: FMLServerStartedEvent) {
        SaveManager.load()
    }

    @Mod.EventHandler
    fun serverStopped(event: FMLServerStoppedEvent) {
        SaveManager.saveAndStopWorld()
    }
}