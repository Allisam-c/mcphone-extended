package com.november.mcphone.target263;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.november.mcphone.core.client.PhoneItemProperties;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Guards the 26.3 NeoForge item definitions against upstream resource overrides.
 *
 * <p>The Java side registers a range select property, so an item definition using
 * {@code minecraft:condition} cannot resolve the property and renders as a missing
 * model.
 */
public final class PhoneItemModelResourceTest {
    private static int checks;
    private static int failures;

    private PhoneItemModelResourceTest() {}

    public static void main(String[] args) {
        checkDevice("phone");
        checkDevice("tablet");
        System.out.printf("PhoneItemModelResourceTest: %d checks, %d failures%n", checks, failures);
        if (failures != 0) {
            System.exit(1);
        }
    }

    private static void checkDevice(String device) {
        String path = "assets/mcphone/items/" + device + ".json";
        try (InputStream stream = PhoneItemModelResourceTest.class.getClassLoader()
                .getResourceAsStream(path)) {
            check(stream != null, path + " is packaged");
            if (stream == null) {
                return;
            }

            String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            JsonObject model = JsonParser.parseString(json).getAsJsonObject()
                    .getAsJsonObject("model");
            checkString(model, "type", "minecraft:range_dispatch", path);
            checkString(model, "property", PhoneItemProperties.SCREEN_ON.toString(), path);

            var entries = model.getAsJsonArray("entries");
            check(entries != null && entries.size() == 1,
                    path + " has exactly one range entry");
            if (entries != null && entries.size() == 1) {
                JsonObject entry = entries.get(0).getAsJsonObject();
                check(entry.get("threshold").getAsFloat() == 1.0F,
                        path + " uses the screen-on threshold");
                checkModel(entry.getAsJsonObject("model"),
                        "mcphone:item/" + device + "_white", path);
            }

            checkModel(model.getAsJsonObject("fallback"),
                    "mcphone:item/" + device + "_black", path);
        } catch (Exception error) {
            failures++;
            System.err.println("FAIL " + path + ": " + error);
        }
    }

    private static void checkModel(JsonObject model, String expected, String path) {
        check(model != null, path + " has a model entry");
        if (model == null) {
            return;
        }
        checkString(model, "type", "minecraft:model", path);
        checkString(model, "model", expected, path);
    }

    private static void checkString(JsonObject object, String field, String expected, String path) {
        check(object != null && object.has(field)
                        && expected.equals(object.get(field).getAsString()),
                path + " " + field + " should be " + expected);
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) {
            failures++;
            System.err.println("FAIL " + message);
        }
    }
}
