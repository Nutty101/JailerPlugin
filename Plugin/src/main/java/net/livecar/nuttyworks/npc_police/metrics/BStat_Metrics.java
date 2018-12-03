package net.livecar.nuttyworks.npc_police.metrics;

import net.livecar.nuttyworks.npc_police.NPCPolice_Plugin;

public class BStat_Metrics {

    // Private Junk
    @SuppressWarnings("unused")
    private Metrics metrics = null;
    private NPCPolice_Plugin getStorageReference = null;

    public BStat_Metrics(NPCPolice_Plugin npcPolice_Plugin) {
        getStorageReference = npcPolice_Plugin;
    }

    public void Start() {
        metrics = new Metrics(this.getStorageReference);
    }
}