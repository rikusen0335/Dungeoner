package net.rikusen.dungeoner

import net.rikusen.dungeoner.command.CommandResult
import org.bukkit.attribute.AttributeInstance
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil

class CustomPlayer(player: Player, level: Int) {

    object Users : Table() {
        val uuid = uuid("uuid")
        val name = text("name")
        val level = integer("level")
        val experience = integer("experience")
        val maxHealth = double("maxHealth")
        val health = double("health")
        val maxMana = double("maxMana")
        val mana = double("mana")
        val maxStamina = double("maxStamina")
        val stamina = double("stamina")
        val strength = double("strength")
        val intelligence = double("intelligence")
        val agility = double("agility")

        override val primaryKey = PrimaryKey(uuid, name = "PK_CustomPlayer_UUID")
    }

    companion object {
        private var registry: ArrayList<CustomPlayer> = ArrayList()

        fun registerPlayer(customPlayer: CustomPlayer) {
            registry.add(customPlayer)
        }

        fun unregisterPlayer(customPlayer: CustomPlayer) {
            registry.remove(customPlayer)
        }

        fun getPlayer(player: Player): CustomPlayer {
            val iterator: Iterator<CustomPlayer> = registry.iterator()
            transaction { SchemaUtils.create(Users) }

            // Initialize if there is no data
            if (!checkUserExists(player.uniqueId)) {
                transaction {
                    Users.insert {
                        it[uuid] = player.uniqueId
                        it[name] = player.name
                        it[level] = 1
                        it[experience] = 0
                        it[maxHealth] = 20.0
                        it[health] = 20.0
                        it[maxMana] = 10.0
                        it[mana] = 10.0
                        it[maxStamina] = 20.0
                        it[strength] = 1.0
                        it[intelligence] = 1.0
                        it[agility] = 1.0
                    }
                }
            }

            var currentPlayerLevel = 0
            transaction {
                val query: Query = Users.select { Users.uuid eq player.uniqueId }
                query.forEach { currentPlayerLevel = it[Users.level] }
            }

            while (iterator.hasNext()) {
                val customPlayer: CustomPlayer = (iterator.next())

                if (customPlayer.player == player) {
                    return customPlayer
                }
            }

            return CustomPlayer(player, currentPlayerLevel) // DBから取ってきたレベルを入れる
        }

        private fun checkUserExists(uuid: UUID): Boolean {
            return transaction { Users.select { Users.uuid eq uuid }.count() != 0L }
        }
    }

    /* ----- Getter / Setter ----- */
    var player: Player = player
    var name: String
        get() = getData(Users.name)
        set(value) = setData(Users.name, value)
    var level: Int
        get() = getData(Users.level)
        set(value) = setData(Users.level, value)
    val requiredExperience: Int
        get() = calcRequiredExperience()
    var experience: Int
        get() = getData(Users.experience)
        set(value) = setData(Users.experience, value)
    var maxHealth: Double
        get() = getData(Users.maxHealth)
        set(value) = setData(Users.maxHealth, value)
    var health: Double
        get() = getData(Users.health)
        set(value) = setData(Users.health, value)
    var maxMana: Double
        get() = getData(Users.maxMana)
        set(value) = setData(Users.maxMana, value)
    var mana: Double
        get() = getData(Users.mana)
        set(value) = setData(Users.mana, value)
    var maxStamina: Double
        get() = getData(Users.maxStamina)
        set(value) = setData(Users.maxStamina, value)
    var stamina: Double = maxStamina
        set(value) {
            if (value >= maxStamina) {
                field = maxStamina
            } else if (value < 0) {
                field = 0.0
            } else {
                field = value
            }
            player.foodLevel = calculateFoodLevel()
        }
    var staminaDelay: Double = 1.0
    var strength: Double
        get() = getData(Users.strength)
        set(value) = setData(Users.strength, value)
    var intelligence: Double
        get() = getData(Users.intelligence)
        set(value) = setData(Users.intelligence, value)
    var agility: Double
        get() = getData(Users.agility)
        set(value) = setData(Users.agility, value)
    var cooldown: Int = 1500
    val cooldownTime: Int = 1500
    var isSneaking: Boolean = false
    var moveVector: Vector = Vector(0.0, 0.0, 0.0)
    /* ----- Getter / Setter END ----- */


    /* ----- private Getter / Setter functions ----- */
    private fun<T> getData(name: Column<T>): T =
        transaction {
            Users.select {
                Users.uuid eq player.uniqueId
            }.limit(1).first()[name]
        }

    private fun<T> setData(name: Column<T>, value: T) {
        transaction {
            Users.update({
                Users.uuid eq player.uniqueId
            }) {
                it[name] = value
            }
        }
    }
    /* ----- private Getter / Setter functions END ----- */

    fun updateClientHealth() {player.health = health / maxHealth * 20}
    fun updateHealth(damage: Double): Boolean {
        if (damage < 0) throw IllegalArgumentException("0以上の値を指定してください。") // Says: "Specify number of 0 or more."

        health -= damage
        updateClientHealth()
        return true
    }

    fun regenerateStamina() {
        if (staminaDelay > 0) return
        stamina += 1.3 // This will be changed
    }

    fun consumeStamina() {
        staminaDelay = 1.0
        stamina -= 0.5 // This will be changed
    }

    fun consumeJumpStamina() {
        staminaDelay = 1.0
        stamina -= 1.0 // This will be changed
    }

    fun calculateFoodLevel(): Int {
//        player.sendMessage(stamina.toString())
//        player.sendMessage((stamina / maxStamina).toString())
//        player.sendMessage(ceil(stamina / maxStamina * 20).toString())
        return ceil(stamina / maxStamina * 14).toInt() + 6
    }

    private fun calcRequiredExperience(): Int {
        return getData(Users.level) * 10
    }

    fun calculateExperience(currentExp: Int, earnedExperience: Int): Int {
        return earnedExperience + currentExp
    }

    private fun calculateLevel(currentLevel: Int, currentExp: Int) {
        val requiredExp = calcRequiredExperience()
        val nextRequiredExp = requiredExp - currentExp

        if (requiredExp <= experience) return calculateLevel(currentLevel + 1, nextRequiredExp)
        val nextLevel = currentLevel + 1

        setData(Users.level, nextLevel)
        setData(Users.experience, nextRequiredExp)
    }
}