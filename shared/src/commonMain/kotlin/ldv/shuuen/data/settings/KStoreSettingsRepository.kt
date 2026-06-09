package ldv.shuuen.data.settings

import io.github.xxfast.kstore.file.storeOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.io.files.Path
import ldv.shuuen.common.ResponseState
import ldv.shuuen.common.upsertBy
import ldv.shuuen.domain.audio.midi.MidiChannel
import ldv.shuuen.domain.audio.midi.Preset
import ldv.shuuen.domain.repository.AppSettings
import ldv.shuuen.domain.repository.SettingsRepository
import ldv.shuuen.domain.training.singles.SinglesLevel
import org.koin.core.annotation.Named

class KStoreSettingsRepository(
  @Named("files") path: Path
) : SettingsRepository {
  val store = storeOf(file = Path(path, "settings.json"), default = AppSettings())
  override val settings: Flow<AppSettings> = store.updates.map { it ?: AppSettings() }

  override suspend fun setPreset(channel: MidiChannel, preset: Preset) {
    store.update {
      it?.copy(
        presets = when (channel) {
          MidiChannel.Notes -> it.presets.copy(notes = preset)
          MidiChannel.Drone -> it.presets.copy(drone = preset)
          MidiChannel.Cadence -> it.presets.copy(cadence = preset)
        },
      )
    }
  }

  override suspend fun setSoundFontPath(path: String?) {
    store.update { it?.copy(soundFontPath = path) }
  }


  override suspend fun upsertLevel(level: SinglesLevel) {
    store.update { settings ->
      settings?.copy(singlesLevels = settings.singlesLevels.upsertBy(level, SinglesLevel::id))
    }
  }

  override suspend fun getLevelById(id: String): Flow<ResponseState<SinglesLevel>> {
    return flow {
      emit(ResponseState.Loading)
      try {
        val result =
          settings.map { settings -> settings.singlesLevels.first { it.id == id } }.first()
        emit(ResponseState.Success(result))
      } catch (e: Exception) {
        emit(ResponseState.Error(e))
      }
    }
  }
}
