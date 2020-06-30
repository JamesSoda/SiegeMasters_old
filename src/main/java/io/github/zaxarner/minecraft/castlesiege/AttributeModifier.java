package io.github.zaxarner.minecraft.castlesiege;

import org.bukkit.Bukkit;

/**
 * Created by JamesCZ98 on 11/27/2019.
 */
public class AttributeModifier {

    private Attribute attribute;
    private double value;

    public AttributeModifier(Attribute attribute, double value) {
        this.attribute = attribute;
        this.value = value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public static AttributeModifier getAttributeModifierFromString(String string) {
        String[] split = string.split(":");

        if(split.length != 2) {
            return null;
        }

        Attribute attribute = null;

        for(Attribute s : Attribute.values()) {
            if(s.name().equalsIgnoreCase(split[0])) {
                attribute = s;
                break;
            }
        }

        if(attribute == null)
            return null;

        double value;

        try {
            value = Double.parseDouble(split[1]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }

        return new AttributeModifier(attribute, value);
    }

    public Attribute getAttribute() {
        return attribute;
    }

}
