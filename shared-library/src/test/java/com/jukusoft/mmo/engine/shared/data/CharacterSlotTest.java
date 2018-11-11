package com.jukusoft.mmo.engine.shared.data;

import io.vertx.core.json.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CharacterSlotTest {

    @Test (expected = IllegalArgumentException.class)
    public void testConstructorNullCID () {
        new CharacterSlot(0, "name", CharacterSlot.GENDER.MALE, "", "", "", "");
    }

    @Test (expected = NullPointerException.class)
    public void testConstructorNullName () {
        new CharacterSlot(1, null, CharacterSlot.GENDER.MALE, "", "", "", "");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructorEmptyName () {
        new CharacterSlot(1, "", CharacterSlot.GENDER.MALE, "", "", "", "");
    }

    @Test
    public void testGetter () {
        CharacterSlot slot = new CharacterSlot(10, "name", CharacterSlot.GENDER.MALE, "skinColor", "hairColor", "hairStyle", "beart");

        assertEquals(10, slot.getCID());
        assertEquals("name", slot.getName());
        assertEquals(CharacterSlot.GENDER.MALE, slot.getGender());
        assertEquals("skinColor", slot.getSkinColor());
        assertEquals("hairColor", slot.getHairColor());
        assertEquals("hairStyle", slot.getHairStyle());
        assertEquals("beart", slot.getBeart());
    }

    @Test
    public void testMaleCharacterToJson () {
        CharacterSlot slot = new CharacterSlot(10, "name", CharacterSlot.GENDER.MALE, "skinColor", "hairColor", "hairStyle", "beart");

        JsonObject json = slot.toJson();
        assertEquals(new Integer(10), json.getInteger("cid"));
        assertEquals("name", json.getString("name"));
        assertEquals("male", json.getString("gender"));
        assertEquals("skinColor", json.getString("skinColor"));
        assertEquals("hairColor", json.getString("hairColor"));
        assertEquals("hairStyle", json.getString("hairStyle"));
        assertEquals("beart", json.getString("beart"));
    }

    @Test
    public void testFemaleCharacterToJson () {
        CharacterSlot slot = new CharacterSlot(10, "name", CharacterSlot.GENDER.FEMALE, "skinColor", "hairColor", "hairStyle", "beart");

        JsonObject json = slot.toJson();
        assertEquals(new Integer(10), json.getInteger("cid"));
        assertEquals("name", json.getString("name"));
        assertEquals("female", json.getString("gender"));
        assertEquals("skinColor", json.getString("skinColor"));
        assertEquals("hairColor", json.getString("hairColor"));
        assertEquals("hairStyle", json.getString("hairStyle"));
        assertEquals("beart", json.getString("beart"));
    }

    @Test
    public void testCreateMaleCharacterFromJson () {
        JsonObject json = new JsonObject();
        json.put("gender", "male");
        json.put("skinColor", "skinColor");
        json.put("hairColor", "hairColor");
        json.put("hairStyle", "hairStyle");
        json.put("beart", "beart");

        CharacterSlot slot = CharacterSlot.createFromJson(10, "name", json);
        assertEquals(10, slot.getCID());
        assertEquals("name", slot.getName());
        assertEquals(CharacterSlot.GENDER.MALE, slot.getGender());
        assertEquals("skinColor", slot.getSkinColor());
        assertEquals("hairColor", slot.getHairColor());
        assertEquals("hairStyle", slot.getHairStyle());
        assertEquals("beart", slot.getBeart());
    }

    @Test
    public void testCreateFemaleCharacterFromJson () {
        JsonObject json = new JsonObject();
        json.put("gender", "female");
        json.put("skinColor", "skinColor");
        json.put("hairColor", "hairColor");
        json.put("hairStyle", "hairStyle");
        json.put("beart", "beart");

        CharacterSlot slot = CharacterSlot.createFromJson(10, "name", json);
        assertEquals(10, slot.getCID());
        assertEquals("name", slot.getName());
        assertEquals(CharacterSlot.GENDER.FEMALE, slot.getGender());
        assertEquals("skinColor", slot.getSkinColor());
        assertEquals("hairColor", slot.getHairColor());
        assertEquals("hairStyle", slot.getHairStyle());
        assertEquals("beart", slot.getBeart());
    }

}
