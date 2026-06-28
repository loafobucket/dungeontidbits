package com.loafobucket.dungeontidbits.misc;

import com.loafobucket.dungeontidbits.block.entity.PottleBlockEntity;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class ModCapabilities {
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        PottleBlockEntity.registerCapabilities(event);
    }
}
