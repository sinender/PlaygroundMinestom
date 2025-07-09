package net.sinender.app

import com.github.shynixn.mccoroutine.minestom.launch
import io.github.togar2.pvp.MinestomPvP
import io.github.togar2.pvp.feature.CombatFeatureSet
import io.github.togar2.pvp.feature.CombatFeatures
import net.minestom.server.MinecraftServer
import net.minestom.server.extras.MojangAuth
import net.minestom.vanilla.VanillaReimplementation
import net.sinender.common.commands.CommandInstance
import net.sinender.common.managers.loadSandboxes
import net.sinender.common.postgres.DatabaseFactory
import net.sinender.common.utils.initMinecraftServer
import net.sinender.lobby.createLobbyInstance
import org.everbuild.blocksandstuff.blocks.BlockBehaviorRuleRegistrations
import org.everbuild.blocksandstuff.blocks.BlockPlacementRuleRegistrations
import org.everbuild.blocksandstuff.blocks.PlacedHandlerRegistration
import org.everbuild.blocksandstuff.blocks.group.VanillaPlacementRules
import org.everbuild.blocksandstuff.fluids.MinestomFluids
import org.reflections.Reflections
import revxrsal.commands.minestom.MinestomLamp


fun main() {
    // Initialize the server
    val minecraftServer = MinecraftServer.init()
    initMinecraftServer(minecraftServer)
    MojangAuth.init()

    createLobbyInstance()

    val vri = VanillaReimplementation.hook(MinecraftServer.process())

    MinestomPvP.init()


    BlockPlacementRuleRegistrations.registerDefault()
    BlockBehaviorRuleRegistrations.registerDefault()
    PlacedHandlerRegistration.registerDefault()
    MinestomFluids.enableFluids()
    MinestomFluids.enableVanillaFluids()

    val modernVanilla: CombatFeatureSet = CombatFeatures.legacyVanilla()
    MinecraftServer.getGlobalEventHandler().addChild(modernVanilla.createNode())

    // Start the server
    minecraftServer.start("0.0.0.0", 25565)

    val lamp = MinestomLamp.builder().build()

    val reflections = Reflections("net.sinender.common.commands")
    val commandClasses = reflections.getSubTypesOf(CommandInstance::class.java)
    for (commandClass in commandClasses) {
        try {
            val command = commandClass.getDeclaredConstructor().newInstance()
            lamp.register(command)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    minecraftServer.launch {
        DatabaseFactory.init()

        loadSandboxes()
    }
}