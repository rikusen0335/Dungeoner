package net.rikusen.dungeoner.config

import org.bukkit.configuration.file.FileConfiguration

import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import org.bukkit.configuration.file.YamlConfiguration
import java.io.IOException


class Configuration(val plugin: JavaPlugin, val fileName: String) {
    var file: File = File(plugin.dataFolder, "$fileName.yml")
    lateinit var config: FileConfiguration

    fun open(): Configuration {
        /*
        コンフィグフォルダがない場合は新しく作る
        Make a new config folder if not exists
         */
        if (!plugin.dataFolder.exists()) {
            try {
                plugin.dataFolder.mkdir()
            } catch (error: IOException) {
                error.printStackTrace()
            }
        }

        /*
       指定したコンフィグファイルがない場合は新しく作る
       Make a new file if specified file not exists
        */
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (error: IOException) {
                error.printStackTrace()
            }
        }

        config = YamlConfiguration.loadConfiguration(file)
        return this
    }

    fun save() {
        try {
            config.save(file)
        } catch (error: IOException) {
            error.printStackTrace()
        }
    }

    fun write(path: String, value: Any?) {
        config.set(path, value)
    }
}