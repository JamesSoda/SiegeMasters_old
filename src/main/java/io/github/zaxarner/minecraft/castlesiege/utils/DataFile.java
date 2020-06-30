package io.github.zaxarner.minecraft.castlesiege.utils;

import io.github.zaxarner.minecraft.castlesiege.CastleSiege;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

/**
 * Created by JamesCZ98 on 7/24/2019
 */
public class DataFile {


    private File folder;
    private File file;
    private FileConfiguration config;

    public DataFile(String fileName, String folderName, boolean hasDefaults) {

        if (!fileName.endsWith(".yml"))
            fileName = fileName + ".yml";

        createFolder(folderName);
        createFileConfiguration(fileName, hasDefaults);

    }

    private void createFolder(String folderName) {
        if (folderName != null) {
            this.folder = new File(CastleSiege.getPlugin().getDataFolder().toString() + File.separator + folderName);
        } else {
            this.folder = new File(CastleSiege.getPlugin().getDataFolder().toString());
        }

        if (!this.folder.exists()) {
            if (this.folder.mkdir()) {
                CastleSiege.log("Folder '" + this.folder.getName() + "' created.", Level.INFO);
            } else {
                CastleSiege.log("Unable to create folder " + this.folder.getName() + ".", Level.SEVERE);
            }
        }
    }

    private void createFile(String fileName) {
        this.file = new File(folder, fileName);

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                CastleSiege.log("Could not create DataFile: " + this.folder + '/' + fileName, Level.SEVERE);
            }
        }
    }

    private void createFileConfiguration(String fileName, boolean hasDefaults) {

        if (hasDefaults && !new File(folder, fileName).exists()) {

            InputStream input = CastleSiege.getPlugin().getResource(fileName);
            if (input == null) {
                CastleSiege.log("Could not find resource for DataFile that 'hasDefaults' - " + fileName, Level.SEVERE);
                return;
            }
            this.config = YamlConfiguration.loadConfiguration(new InputStreamReader(input));
            createFile(fileName);

        } else {

            if (this.file == null || !this.file.exists())
                createFile(fileName);

            this.config = YamlConfiguration.loadConfiguration(file);
        }

        save();
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void setIfNull(String path, Object o) {
        if (config.get(path) == null)
            config.set(path, o);
    }

    public Location getLocation(String path) {
        return WorldUtils.getLocationFromString(config.getString(path));
    }

    public void setLocation(String path, Location location) {
        config.set(path, WorldUtils.getLocationString(location));
    }


}

