package WhonoMod.PowerSystem;

public enum PowerVoltage {

    ULV(8, "Ultra Low"),
    LV(32, "Low"),
    MV(128, "Medium"),
    HV(512, "High"),
    EV(2048, "Extreme"),
    IV(8192, "Insane"),
    LuV(32768, "Ludicrous"),
    UV(131072, "Ultimate"),
    InV(524288, "Infinity"),
    WV(2147483647, "Whono");

    public final int voltage;
    public final String name;
    public static final PowerVoltage[] VALID_VOLTAGE = {ULV, LV, MV, HV, EV, IV, LuV, UV, InV, WV};

    private PowerVoltage(int voltage, String name) {

        this.voltage = voltage;
        this.name = name + " Voltage";
    }

    public static PowerVoltage getVoltageLevel(int id) {

        if (id >= 0 && id < VALID_VOLTAGE.length) {

            return VALID_VOLTAGE[id];
        }
        return null;
    }

    public boolean isOverVoltage(int power) {

        return voltage < power;
    }
}
