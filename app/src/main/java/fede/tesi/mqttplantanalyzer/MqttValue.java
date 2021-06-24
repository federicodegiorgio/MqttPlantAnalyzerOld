package fede.tesi.mqttplantanalyzer;

public class MqttValue {
    private int timestamp;
    private int lux;

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getLux() {
        return lux;
    }

    public void setLux(int lux) {
        this.lux = lux;
    }
}
