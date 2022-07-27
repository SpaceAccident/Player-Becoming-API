package space.accident.pbapi.api

import net.minecraft.nbt.NBTTagCompound

interface IComponentPlayerData {
    /**
     * Login player.
     * Called only if the player join in the game
     *
     * @param playerName
     */
    fun loginPlayer(playerName: String)

    /**
     * Load nbt component
     */
    fun loadNBT(aNBT: NBTTagCompound)

    /**
     * Save nbt component
     */
    fun saveNBT(aNBT: NBTTagCompound)
}