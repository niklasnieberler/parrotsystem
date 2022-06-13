package net.mrmanhd.parrot.lib.api.service

import net.mrmanhd.parrot.api.group.IParrotGroup
import net.mrmanhd.parrot.api.service.IParrotService
import net.mrmanhd.parrot.api.service.player.IGamePlayer
import net.mrmanhd.parrot.api.service.player.PlayerState
import net.mrmanhd.parrot.api.service.state.ServiceState
import net.mrmanhd.parrot.api.utils.ParrotLocation
import net.mrmanhd.parrot.lib.Parrot
import net.mrmanhd.parrot.lib.api.group.ParrotGroup
import net.mrmanhd.parrot.lib.api.service.player.GamePlayer
import net.mrmanhd.parrot.lib.repository.info.ParrotServiceInfo
import java.util.*

/**
 * Created by MrManHD
 * Class create at 10.06.2022 19:29
 */

class ParrotService(
    private val uniqueId: UUID,
    private val parrotGroup: ParrotGroup,
    private val name: String,
    private val owner: UUID?,
    private val privateService: Boolean,
    private val removeWhenServiceEmpty: Boolean,
    private val cloudServiceName: String,
    private val spawnLocation: ParrotLocation,
    private val createdAt: Long = System.currentTimeMillis()
) : IParrotService {

    override fun getUniqueId(): UUID = this.uniqueId

    override fun getGroup(): IParrotGroup = this.parrotGroup

    override fun getName(): String = this.name

    override fun createdAt(): Long = this.createdAt

    override fun getSpawnLocation(): ParrotLocation = this.spawnLocation

    override fun getCloudServiceName(): String = this.cloudServiceName

    override fun getOwner(): UUID? = this.owner

    override fun isPrivate(): Boolean = this.privateService

    override fun isRemoveWhenServiceEmpty(): Boolean = this.removeWhenServiceEmpty

    override fun getMotd(): String {
        return getInfo()?.motd ?: "Just Another ParrotService"
    }

    override fun setMotd(motd: String) {
        val serviceInfo = getInfo() ?: return
        serviceInfo.motd = motd
        serviceInfo.update()
    }

    override fun getState(): ServiceState {
        return getInfo()?.state ?: ServiceState.OFFLINE
    }

    override fun setState(state: ServiceState) {
        val serviceInfo = getInfo() ?: return
        serviceInfo.state = state
        serviceInfo.update()
    }

    override fun getProperties(): Map<String, Any> {
        return getInfo()?.propertyMap ?: emptyMap()
    }

    override fun setProperty(key: String, value: Any) {
        val serviceInfo = getInfo() ?: return
        serviceInfo.propertyMap[key] = value
        serviceInfo.update()
    }

    override fun removeProperty(key: String) {
        val serviceInfo = getInfo() ?: return
        serviceInfo.propertyMap.remove(key)
        serviceInfo.update()
    }

    override fun getGamePlayers(): List<IGamePlayer> {
        return getInfo()?.gamePlayers ?: emptyList()
    }

    override fun updateGamePlayer(uniqueId: UUID, playerState: PlayerState): IGamePlayer? {
        val gamePlayer = getGamePlayer(uniqueId) ?: return null
        val serviceInfo = getInfo() ?: return gamePlayer

        val gamePlayerInstance = GamePlayer(uniqueId, gamePlayer.getName(), playerState, gamePlayer.createdAt())
        serviceInfo.gamePlayers.removeIf { it.getUniqueId() == uniqueId }
        serviceInfo.gamePlayers.add(gamePlayerInstance)
        serviceInfo.update()
        return gamePlayerInstance
    }

    override fun getPreConnectedPlayers(): List<UUID> {
        return getInfo()?.preConnectedPlayers ?: emptyList()
    }

    override fun getMaxPlayers(): Int {
        return getInfo()?.maxPlayers ?: 0
    }

    override fun setMaxPlayers(maxPlayers: Int) {
        val serviceInfo = getInfo() ?: return
        serviceInfo.maxPlayers = maxPlayers
        serviceInfo.update()
    }

    override fun loadWorld(slimeWorldName: String) {
        TODO("Not yet implemented")
    }

    override fun unloadWorld(slimeWorldName: String) {
        TODO("Not yet implemented")
    }

    override fun stop() {
        TODO("Not yet implemented")
    }

    override fun update() {
        getInfo()?.update()
    }

    override fun toJsonString(): String {
        return getInfo()?.convertToJsonString() ?: "{}"
    }

    private fun getInfo(): ParrotServiceInfo? {
        return Parrot.instance.parrotServiceRepository.find(this.uniqueId)
    }

    fun addGamePlayer(uniqueId: UUID, name: String, playerState: PlayerState): GamePlayer {
        val gamePlayerInstance = GamePlayer(uniqueId, name, playerState)
        val serviceInfo = getInfo() ?: return gamePlayerInstance

        serviceInfo.gamePlayers.removeIf { it.getUniqueId() == uniqueId }
        serviceInfo.gamePlayers.add(gamePlayerInstance)
        serviceInfo.update()
        return gamePlayerInstance
    }

    fun removeGamePlayer(uniqueId: UUID) {
        val serviceInfo = getInfo() ?: return
        serviceInfo.gamePlayers.removeIf { it.getUniqueId() == uniqueId }
        serviceInfo.update()
    }

    fun addPreConnectedPlayer(uniqueId: UUID) {
        val serviceInfo = getInfo() ?: return
        serviceInfo.preConnectedPlayers.add(uniqueId)
        serviceInfo.update()
    }

    fun removePreConnectedPlayer(uniqueId: UUID) {
        val serviceInfo = getInfo() ?: return
        serviceInfo.preConnectedPlayers.remove(uniqueId)
        serviceInfo.update()
    }

}