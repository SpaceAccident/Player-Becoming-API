package space.accident.pbapi.extra

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.DimensionManager
import space.accident.pbapi.api.API.COMPONENT_DATA_PLAYERS
import space.accident.pbapi.api.models.JsonComponentDataPlayer
import space.accident.pbapi.api.models.PlayerData
import java.io.File
import java.io.FileWriter

object SaveManager {

    private var WORLD_DIRECTORY: File? = null
    private const val ROOT_FOLDER = "SpaceAccident"
    private const val PLAYER_FOLDER = "PlayerBecoming"

    private lateinit var rootDirectory: File
    private lateinit var playerDirectory: File

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(NBTTagCompound::class.java, JsonNbtSerialized())
        .setPrettyPrinting()
        .create()

    private fun initData() {
        WORLD_DIRECTORY = DimensionManager.getCurrentSaveRootDirectory()

        if (WORLD_DIRECTORY == null) {
            println(IllegalStateException("[PlayerBecoming] ERROR NOT SET WORLD DIRECTORY"))
        }

        rootDirectory = File(WORLD_DIRECTORY, ROOT_FOLDER)
        playerDirectory = File(rootDirectory, PLAYER_FOLDER)

        if (!rootDirectory.isDirectory && !rootDirectory.mkdirs()) {
            println(IllegalStateException("[PlayerBecoming] Failed to create ${rootDirectory.absolutePath}"))
        }
        if (!playerDirectory.isDirectory && !playerDirectory.mkdirs()) {
            println(IllegalStateException("[PlayerBecoming] Failed to create ${playerDirectory.absolutePath}"))
        }
    }

    private fun clearData() {
        COMPONENT_DATA_PLAYERS.clear()
    }

    /**
     * Load this Manager pre start sertver
     */
    fun load() {
        initData()
        clearData()
    }

    /**
     * Save data pre stop server
     */
    fun saveAndStopWorld() {
        savePlayersData()

        clearData()
        WORLD_DIRECTORY = null
    }

    /**
     * Save component data of player
     *
     * @param data playerData
     * @param isSaveIOThread save data in IO kotlin dispatcher
     */
    @JvmOverloads
    fun savePlayerData(data: PlayerData, isSaveIOThread: Boolean = true) {
        if (!playerDirectory.isDirectory) return

        val dispatcher = if (isSaveIOThread) Dispatchers.IO else Dispatchers.Main
        runBlocking(dispatcher) {

            val playerFolder = File(playerDirectory, data.playerName).apply { mkdirs() }

            data.dataList.forEach { component ->

                component.playerData.saveNBT(component.nbt)

                val jsonComponent = JsonComponentDataPlayer(component.componentId, component.nbt)

                val file = File(playerFolder, "${component.componentId}.json")

                FileWriter(file).buffered().use {
                    gson.toJson(jsonComponent, it)
                }
            }
        }
    }

    /**
     * Save component data of all players
     *
     * @param isSaveIOThread save data in IO kotlin dispatcher
     */
    @JvmOverloads
    fun savePlayersData(isSaveIOThread: Boolean = true) {
        if (!playerDirectory.isDirectory) return
        COMPONENT_DATA_PLAYERS.forEach { playerData ->
            savePlayerData(playerData, isSaveIOThread)
        }
    }

    /**
     * Load player data
     *
     * @param playerData player data
     */
    fun loadPlayerData(playerData: PlayerData) {
        if (!playerDirectory.isDirectory) return

        playerDirectory.listFiles { _, name ->
            name == playerData.playerName
        }?.firstOrNull()?.let { folderPlayer ->
            if (folderPlayer.isDirectory) {
                folderPlayer.listFiles()?.forEach { json ->
                    json.bufferedReader().use { reader ->
                        playerData.dataList.find { data ->
                            data.componentId == json.name.replace(".json", "")
                        }?.apply {
                            gson.fromJson(reader, JsonComponentDataPlayer::class.java)?.let {
                                nbt = it.nbt
                            }
                        }
                        playerData.dataList.forEach {
                            it.playerData.loadNBT(it.nbt)
                        }
                    }
                }
            }
        }
    }
}