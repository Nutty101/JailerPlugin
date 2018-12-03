package net.livecar.nuttyworks.npc_police.jails;

public class DistanceDelaySetting {
    private Double delay;
    private int distance;

    public DistanceDelaySetting(int distance, Double delay) {
        this.delay = delay;
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public int getDistanceSquared() {
        return distance * distance;
    }

    public Double getDelay() {
        return delay;
    }

    public Double getSeconds() {
        return delay * distance;
    }
}
