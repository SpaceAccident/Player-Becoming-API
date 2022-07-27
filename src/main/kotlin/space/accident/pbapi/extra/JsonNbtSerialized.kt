package space.accident.pbapi.extra

import codechicken.nei.util.NBTJson
import com.google.gson.*
import net.minecraft.nbt.NBTTagCompound
import java.lang.reflect.Type

class JsonNbtSerialized : JsonSerializer<NBTTagCompound>, JsonDeserializer<NBTTagCompound> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): NBTTagCompound {
        return NBTJson.toNbt(json) as NBTTagCompound
    }

    override fun serialize(src: NBTTagCompound, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return NBTJson.toJsonObject(src)
    }
}