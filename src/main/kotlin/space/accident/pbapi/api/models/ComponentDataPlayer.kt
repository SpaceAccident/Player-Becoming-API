package space.accident.pbapi.api.models

import com.google.gson.annotations.SerializedName
import net.minecraft.nbt.NBTTagCompound
import space.accident.pbapi.api.IComponentPlayerData

/**
 * Component Data of player
 *
 * @param componentId string id of component
 * @param playerData instance of component
 * @param nbt minecraft nbt of component
 */
data class ComponentDataPlayer(
    val componentId: String,
    val playerData: IComponentPlayerData,
    var nbt: NBTTagCompound,
)

/**
 * Player Data
 *
 * @param playerName name of player
 * @param dataList list of component data player
 */
data class PlayerData(
    val playerName: String,
    val dataList: ArrayList<ComponentDataPlayer>
)

/**
 * Class for serialized and save json
 *
 * @param componentId string id of component
 * @param nbt minecraft nbt of component
 */
data class JsonComponentDataPlayer(
    @SerializedName("component_id") val componentId: String,
    @SerializedName("nbt") val nbt: NBTTagCompound,
)

/**
 * Components data class for instance becoming player
 *
 * @param componentId string id of component
 * @param cls class of component
 */
data class ComponentDataClass(
    val componentId: String,
    val cls: Class<out IComponentPlayerData>
)