package com.november.mcphone.core.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.november.mcphone.MCphone;
import com.november.mcphone.api.client.app.IPhoneApp;
import com.november.mcphone.core.PhoneLocation;
import com.november.mcphone.platform.client.Screens;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.settings.KeyModifier;

/**
 * App hotkey handling for 26.3, whose key event exposes SDL physical keys instead
 * of the older key-code plus scan-code pair.
 */
public final class AppHotkeyHandler {

    private AppHotkeyHandler() {}

    public static void onKeyInput(InputEvent.Key event) {
        if (event.getAction() != InputConstants.PRESS) return;

        Minecraft mc = Minecraft.getInstance();
        if (Screens.current(mc) != null || mc.player == null || mc.level == null) return;

        InputConstants.Key key = com.november.mcphone.platform.client.PhoneKeyInput.key(
                event.getKey(), -1);
        if (KeyModifier.isKeyCodeModifier(key)) return;

        AppHotkeys.Binding pressed = AppHotkeys.Binding.of(key, AppHotkeys.activeModifiers());
        if (!launch(mc, pressed)) return;
        drainConflicting(pressed);
    }

    public static void onMouseInput(InputEvent.MouseButton.Pre event) {
        if (event.getAction() != InputConstants.PRESS) return;

        Minecraft mc = Minecraft.getInstance();
        if (Screens.current(mc) instanceof PhoneScreen phone
                && phone.captureHotkeyMouse(event.getButton())) {
            event.setCanceled(true);
            return;
        }

        if (Screens.current(mc) != null || mc.player == null || mc.level == null) return;

        InputConstants.Key key = InputConstants.Type.MOUSE.getOrCreate(event.getButton());
        AppHotkeys.Binding pressed = AppHotkeys.Binding.of(key, AppHotkeys.activeModifiers());
        if (!launch(mc, pressed)) return;
        event.setCanceled(true);
    }

    private static boolean launch(Minecraft mc, AppHotkeys.Binding pressed) {
        Identifier appId = AppHotkeys.appFor(pressed);
        if (appId == null) return false;
        if (!PhoneScreenRegistry.isInstalled(appId)) return false;

        IPhoneApp app = PhoneScreenRegistry.getApp(appId);
        if (app == null) return false;

        if (!opensInsidePhone(app)) return launchOutside(mc, app);
        if (!PhoneScreenOpener.open(mc.player)) return false;
        if (Screens.current(mc) instanceof PhoneScreen phone) phone.launchApp(app);
        return true;
    }

    private static boolean launchOutside(Minecraft mc, IPhoneApp app) {
        if (mc.player == null || PhoneLocation.find(mc.player).isEmpty()) return false;
        try {
            app.onPress();
        } catch (Throwable t) {
            MCphone.LOGGER.error("[MCphone] App {} 的 onPress() 抛异常", app.getId(), t);
        }
        return true;
    }

    private static boolean opensInsidePhone(IPhoneApp app) {
        try {
            return app.opensInsidePhone();
        } catch (Throwable t) {
            MCphone.LOGGER.error("[MCphone] App {} 的 opensInsidePhone() 抛异常，当它在手机里",
                    app.getId(), t);
            return true;
        }
    }

    private static void drainConflicting(AppHotkeys.Binding binding) {
        for (KeyMapping clash : AppHotkeys.conflictingMappings(binding)) {
            while (clash.consumeClick()) { /* 倒空 */ }
        }
    }
}
