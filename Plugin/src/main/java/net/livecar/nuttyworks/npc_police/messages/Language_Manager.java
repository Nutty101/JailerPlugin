package net.livecar.nuttyworks.npc_police.messages;

import net.livecar.nuttyworks.npc_police.NPC_Police;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;

public class Language_Manager {

    public HashMap<String, FileConfiguration> languageStorage = new HashMap<String, FileConfiguration>();

    private NPC_Police getStorageReference = null;

    public Language_Manager(NPC_Police policeRef) {
        getStorageReference = policeRef;
    }

    public void loadLanguages() {
        loadLanguages(false);
    }

    public void loadLanguages(boolean silent) {
        if (languageStorage == null)
            languageStorage = new HashMap<String, FileConfiguration>();
        languageStorage.clear();

        File[] languageFiles = getStorageReference.languagePath.listFiles(
                new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.getName().endsWith(".yml");
                    }
                }
        );

        for (File ymlFile : languageFiles) {
            FileConfiguration oConfig = getStorageReference.getUtilities.loadConfiguration(ymlFile);
            if (oConfig == null) {
                getStorageReference.getMessageManager.logToConsole(
                        "Problem loading language file (" + ymlFile.getName().toLowerCase().replace(".yml", "") + ")"
                );
            } else {
                languageStorage.put(ymlFile.getName().toLowerCase().replace(".yml", ""), oConfig);
                if (!silent) {

                }

            }
        }
    }
}
