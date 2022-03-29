package io.github.janotlelapin.slate.util

import io.github.janotlelapin.slate.SlatePlugin
import io.github.janotlelapin.slate.game.Game
import io.github.janotlelapin.slate.game.GameSettings
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minecraft.server.v1_8_R3.ChatComponentText
import net.minecraft.server.v1_8_R3.IChatBaseComponent
import net.minecraft.server.v1_8_R3.PacketPlayOutChat
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter
import org.apache.commons.io.FileUtils
import org.bukkit.*
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.floor

/**
 * @return The Slate plugin instance
 */
fun slateInstance(): SlatePlugin {
    return Bukkit.getPluginManager().getPlugin("Slate") as SlatePlugin
}

/**
 * @return a human-readable time representation of this number
 */
fun Number.toTimeString(): String {
    return String.format("%02d:%02d", this.toLong() / 60, this.toLong() % 60)
}

/**
 * @return Serializes this component as a legacy string
 */
fun Component.legacy(): String {
    return LegacyComponentSerializer.legacySection().serialize(this)
}

/**
 * @return Serializes this component as a JSON object
 */
fun Component.json(): String {
    return GsonComponentSerializer.gson().serialize(this)
}

/**
 * Sends a component message
 *
 * @param message The JSON message
 */
fun CommandSender.sendMessage(message: Component) {
    (this as CraftPlayer).handle.playerConnection
        .sendPacket(PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(message.json())))
}

/**
 * Sends a message above the action bar of this player
 *
 * @param message The JSON message
 */
fun Player.sendActionBar(message: Component) {
    (this as CraftPlayer).handle.playerConnection
        .sendPacket(PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(message.legacy()), 2))
}

/**
 * Sets this player's name in the tab list
 *
 * @param name The JSON name
 */
fun Player.playerListName(name: Component) {
    this.playerListName = name.legacy()
}

/**
 * Decorates this player's tab list with a custom header and footer
 *
 * @param header The JSON header
 * @param footer The JSON footer
 */
fun Player.sendPlayerListHeaderAndFooter(header: Component, footer: Component) {
    val packet = PacketPlayOutPlayerListHeaderFooter()

    hashMapOf("a" to header, "b" to footer).forEach { (t, u) ->
        val field = packet::class.java.getDeclaredField(t)
        field.isAccessible = true
        field.set(packet, ChatComponentText(u.legacy()))
    }

    (this as CraftPlayer).handle.playerConnection.sendPacket(packet)
}

/**
 * Clears the player, making it ready for games
 */
fun Player.clear() {
    inventory.clear()
    scoreboard = player.server.scoreboardManager.mainScoreboard
    foodLevel = 20
    saturation = 20F
    health = 20.0
    totalExperience = 0
    exp = 0F
    level = 0
}

private fun Player.metadata(key: String): MetadataValue? {
    return if (hasMetadata(key)) getMetadata(key)[0] else null
}

private fun Player.metadata(key: String, value: Any, plugin: JavaPlugin) {
    setMetadata(key, FixedMetadataValue(plugin, value))
}

/**
 * @return The game this player is in
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified S : GameSettings> Player.game(): Game<S>? {
    val game = this.game ?: return null
    if (game.settings is S) return game as Game<S>
    return null
}

/**
 * The game this player is in
 */
var Player.game: Game<out GameSettings>?
    get() {
        val id = metadata("game") ?: return null
        return slateInstance().gameManager.game(UUID.fromString(id.asString()))
    }
    set(value) = if (value != null) metadata("game", value.id, value.plugin) else Unit

private fun World.ground(x: Int, z: Int, start: Int = 48, end: Int = 255): Int? {
    if (start > end) return null

    val y = floor(((start + end) / 2).toDouble()).toInt()
    val block = getBlockAt(x, y, z).type

    if (block == Material.GRASS) return y
    if (block == Material.AIR) return ground(x, z, start, y - 1)
    return ground(x, z, y + 1, end)
}

/**
 * Generates random coordinates on the ground of this world.
 */
fun World.randomCoordinates(range: Int = 500): Location {
    fun random() = ThreadLocalRandom.current().nextInt(-range, range)

    while (true) {
        val x = random()
        val z = random()
        val y = ground(x, z) ?: continue

        return Location(this, (x + .5), (y + .5), (z + .5))
    }
}

/**
 * Copies this world
 *
 * @param name         The name of the world copy
 * @throws IOException Directory copy failed
 */
fun World.copy(name: String): World {
    val target = File(Bukkit.getWorldContainer(), name)

    FileUtils.copyDirectory(worldFolder, target) {
        !it.name.equals("uid.dat") && !it.name.equals("session.lock")
    }

    return WorldCreator(name).createWorld()
}

/**
 * Deletes the folder containing this world
 *
 * @throws IOException Directory delete failed
 */
fun World.delete() {
    if (!Bukkit.unloadWorld(this, false)) throw IOException("Could not unload World")

    FileUtils.deleteDirectory(worldFolder)
}
