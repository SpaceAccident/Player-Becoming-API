package space.accident.pbapi.api

import net.minecraft.nbt.NBTTagCompound
import space.accident.pbapi.api.models.ComponentDataClass
import space.accident.pbapi.api.models.ComponentDataPlayer
import space.accident.pbapi.api.models.PlayerData
import space.accident.pbapi.extra.SaveManager

object API {

    private val COMPONENT_DATA_CLASSES = HashSet<ComponentDataClass>()
    internal val COMPONENT_DATA_PLAYERS = HashSet<PlayerData>()
    private val REMOVE_CANDIDATES = HashSet<PlayerData>()

    /**
     * Register component
     *
     * @param componentId string id of component
     * @param componentClass component class
     */
    fun registerComponent(componentId: String, componentClass: Class<out IComponentPlayerData>) {
        COMPONENT_DATA_CLASSES.add(ComponentDataClass(componentId, componentClass))
    }

    /**
     * Auto save data components
     */
    fun saveNBT() {
        REMOVE_CANDIDATES.forEach { removeCandidate ->
            COMPONENT_DATA_PLAYERS.remove(removeCandidate)
            SaveManager.savePlayerData(removeCandidate)
        }
        SaveManager.savePlayersData()
        REMOVE_CANDIDATES.clear()
    }

    /**
     * Login player.
     * Called only if the player join in the game
     *
     * @param playerName
     */
    fun playerLogin(playerName: String) {
        REMOVE_CANDIDATES.find { it.playerName == playerName }?.let {
            REMOVE_CANDIDATES -= it
            return
        }

        val dataList = arrayListOf<ComponentDataPlayer>()

        COMPONENT_DATA_CLASSES.forEach { dataClass ->
            try {
                val playerData = dataClass.cls.newInstance()
                playerData.loginPlayer(playerName)
                dataList += ComponentDataPlayer(dataClass.componentId, playerData, NBTTagCompound())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val playerData = PlayerData(playerName, dataList)
        SaveManager.loadPlayerData(playerData)
        COMPONENT_DATA_PLAYERS += playerData
    }

    /**
     * Logout player.
     * Called only if the player is out of the game
     *
     * @param playerName
     */
    fun playerLogout(playerName: String) {
        COMPONENT_DATA_PLAYERS.find { it.playerName == playerName }?.also {
            REMOVE_CANDIDATES += it
        }
    }
}