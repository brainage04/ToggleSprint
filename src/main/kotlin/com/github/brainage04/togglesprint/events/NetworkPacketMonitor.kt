package com.github.brainage04.togglesprint.events

import com.github.brainage04.togglesprint.gui.TPSTracker
import com.github.brainage04.togglesprint.ToggleSprintMain
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import net.minecraft.network.Packet
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent

/** Observes inbound packets without consuming or rescheduling them. */
@ChannelHandler.Sharable
object NetworkPacketMonitor : ChannelInboundHandlerAdapter() {
    private const val HANDLER_NAME = "togglesprint_packet_monitor"

    @SubscribeEvent
    fun onConnected(event: FMLNetworkEvent.ClientConnectedToServerEvent) {
        val pipeline = event.manager.channel().pipeline()
        if (pipeline.get(HANDLER_NAME) == null) {
            pipeline.addBefore("packet_handler", HANDLER_NAME, this)
            ToggleSprintMain.LOGGER.info("Installed inbound packet monitor.")
        }
    }

    override fun channelRead(context: ChannelHandlerContext, message: Any) {
        if (message is Packet<*>) TPSTracker.onPacketReceived()
        context.fireChannelRead(message)
    }
}
