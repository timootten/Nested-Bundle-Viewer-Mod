package eu.shadehost.neoforge;

import net.neoforged.fml.common.Mod;

import eu.shadehost.ExampleMod;

@Mod(ExampleMod.MOD_ID)
public final class ExampleModNeoForge {
    public ExampleModNeoForge() {
        // Run our common setup.
        ExampleMod.init();
    }
}
