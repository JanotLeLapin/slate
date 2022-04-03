package io.github.janotlelapin.slate.util

import io.github.janotlelapin.slate.SlatePlugin
import io.github.janotlelapin.slate.game.Game
import io.github.janotlelapin.slate.game.GameSettings
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.minecraft.server.v1_8_R3.*
import org.apache.commons.io.FileUtils
import org.bukkit.*
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.floor
import kotlin.math.roundToInt

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
fun Player.clear(plugin: JavaPlugin) {
    inventory.clear()
    scoreboard = player.server.scoreboardManager.mainScoreboard
    foodLevel = 20
    saturation = 20F
    health = 20.0
    totalExperience = 0
    exp = 0F
    level = 0

    arrayOf(
        "lastAttacker",
        "dead"
    ).forEach { removeMetadata(it, plugin) }
}

fun Player.findNearestPlayer(range: Double): Player? {
    var distance = Double.MAX_VALUE
    var nearest: Player? = null
    this.getNearbyEntities(range, range, range).forEach {
        if (it is Player && !it.isGameDead()) {
            val newDistance = this.location.distance(it.location)
            if (newDistance < distance) {
                nearest = it
                distance = newDistance
            }
        }
    }
    return nearest
}

private fun Player.metadata(key: String): MetadataValue? {
    return if (hasMetadata(key)) getMetadata(key)[0] else null
}

private fun Player.metadata(key: String, value: Any, plugin: JavaPlugin) {
    removeMetadata(key, plugin)
    setMetadata(key, FixedMetadataValue(plugin, value))
}

/**
 * @return Whether the game considers this player dead
 */
fun Player.isGameDead(): Boolean {
    return this.hasMetadata("dead")
}

/**
 * Sets whether the game considers this player dead
 */
fun Player.isGameDead(gameDead: Boolean, plugin: JavaPlugin) {
    if (gameDead) this.metadata("dead", true, plugin)
    else this.removeMetadata("dead", plugin)
}

fun Player.lastAttacker(): Player? {
    val id = this.metadata("lastAttacker") ?: return null
    return this.server.getPlayer(UUID.fromString(id.asString()))
}

fun Player.lastAttacker(player: Player, plugin: JavaPlugin) {
    this.metadata("lastAttacker", player.uniqueId, plugin)
}

/**
 * @return The game this player is in
 */
inline fun <reified S : GameSettings> Player.game(): Game<S>? {
    return this.world.game()
}

/**
 * @param numbers The style of the coordinate values
 * @param uppercase Whether the coordinate keys should be uppercase
 * @return This location as a component
 */
fun Location.toComponent(
    numbers: Style = Style.style(NamedTextColor.GRAY),
    uppercase: Boolean = true,
): Component {
    var comp = Component.empty()
    hashMapOf(
        (if (uppercase) "X" else "x") to x,
        (if (uppercase) "Y" else "y") to y,
        (if (uppercase) "Z" else "z") to z,
    ).forEach { (key, value) ->
        comp = comp.append(Component.join(
            JoinConfiguration.noSeparators(),
            Component.space(),
            Component.text("$key:"),
            Component.space(),
            Component.text(value.roundToInt()).style(numbers),
        ))
    }

    return comp
}

fun World.ground(x: Int, z: Int, start: Int = 48, end: Int = 255): Int? {
    if (start > end) return null

    val y = floor(((start + end) / 2).toDouble()).toInt()
    val block = getBlockAt(x, y, z).type

    if (block == Material.GRASS) return y + 1
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

        if ((0..4).map { getBlockAt(x, y + it, z).type }.all { it == Material.AIR })
            return Location(this, x + .5, y.toDouble(), z + .5)
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

/**
 * @return The game that uses this world
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified S : GameSettings> World.game(): Game<S>? {
    val tokens = this.name.split("_")
    if (tokens.size <= 1) return null

    val game = slateInstance().gameManager.game(UUID.fromString(tokens[1])) ?: return null
    if (game.settings is S) return game as Game<S>
    return null
}

/**
 * Sets the drop for a block
 */
fun BlockBreakEvent.setDrops(newDrops: Collection<ItemStack>) {
    block.type = Material.AIR
    newDrops.forEach {
        block.world.dropItemNaturally(
            Location(
                block.world,
                block.x + .5,
                block.y + .5,
                block.z + .5),
            it)
    }
}
