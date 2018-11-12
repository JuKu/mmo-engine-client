package com.jukusoft.mmo.engine.shared.data;

import io.vertx.core.json.JsonObject;

public class CharacterSlot {

    public enum GENDER {
        MALE, FEMALE
    }

    //unique id of character
    protected final int cid;

    //name of character
    protected final String name;

    protected final GENDER gender;

    //hautfarbe - templateID (?)
    protected final String skinColor;

    //hair color
    protected final String hairColor;

    //hair style (frisur)
    protected final String hairStyle;

    //beart (bart)
    protected final String beart;

    public static final String TEXT_SKINCOLOR = "skinColor";
    public static final String TEXT_HAIRCOLOR = "hairColor";
    public static final String TEXT_HAIRSTYLE = "hairStyle";
    public static final String TEXT_BEART = "beart";

    public CharacterSlot(final int cid, final String name, final GENDER gender, String skinColor, String hairColor, String hairStyle, String beart) {
        if (cid <= 0) {
            throw new IllegalArgumentException("cid has to be greater than 0.");
        }

        if (name == null) {
            throw new NullPointerException("name cannot be null.");
        }

        if (name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be empty.");
        }

        this.cid = cid;
        this.name = name;
        this.gender = gender;
        this.skinColor = skinColor;
        this.hairColor = hairColor;
        this.hairStyle = hairStyle;
        this.beart = beart;
    }

    public int getCID () {
        return cid;
    }

    public String getName () {
        return name;
    }

    public GENDER getGender () {
        return gender;
    }

    public String getSkinColor () {
        return skinColor;
    }

    public String getHairColor () {
        return hairColor;
    }

    public String getHairStyle () {
        return hairStyle;
    }

    public String getBeart () {
        return beart;
    }

    public JsonObject toJson () {
        JsonObject json = new JsonObject();
        json.put("cid", this.cid);
        json.put("name", this.name);
        json.put("gender", this.gender.toString().toLowerCase());
        json.put(TEXT_SKINCOLOR, this.skinColor);
        json.put(TEXT_HAIRCOLOR, this.hairColor);
        json.put(TEXT_HAIRSTYLE, this.hairStyle);
        json.put(TEXT_BEART, this.beart);

        return json;
    }

    public static CharacterSlot createDummyMaleCharacterSlot () {
        return new CharacterSlot(10, "name", GENDER.MALE, TEXT_SKINCOLOR, TEXT_HAIRCOLOR, TEXT_HAIRSTYLE, TEXT_BEART);
    }

    public static CharacterSlot createDummyFemaleCharacterSlot () {
        return new CharacterSlot(10, "name", GENDER.FEMALE, TEXT_SKINCOLOR, TEXT_HAIRCOLOR, TEXT_HAIRSTYLE, TEXT_BEART);
    }

    public static CharacterSlot createFromJson (int cid, String name, JsonObject json) {
        GENDER gender = (json.getString("gender").equals("male") ? GENDER.MALE : GENDER.FEMALE);
        String skinColor = json.getString(TEXT_SKINCOLOR);
        String hairColor = json.getString(TEXT_HAIRCOLOR);
        String hairStyle = json.getString(TEXT_HAIRSTYLE);
        String beart = json.getString(TEXT_BEART);

        return new CharacterSlot(cid, name, gender, skinColor, hairColor, hairStyle, beart);
    }

    public static CharacterSlot create (String name, GENDER gender, String skinColor, String hairColor, String hairStyle, String beart) {
        return new CharacterSlot(Integer.MAX_VALUE, name, gender, skinColor, hairColor, hairStyle, beart);
    }

}
