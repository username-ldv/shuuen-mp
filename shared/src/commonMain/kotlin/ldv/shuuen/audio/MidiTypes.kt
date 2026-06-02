package ldv.shuuen.audio

enum class MidiChannel(val id: Int) {
    Notes(0),
    Drone(1),
    Cadence(2),
}

data class Preset(
    val bank: Int,
    val id: Int,
    val name: String? = null,
) {
    fun toPacked(): Int = (id and 0xffff) or ((bank and 0xffff) shl 16)

    companion object {
        fun fromPacked(packed: Int, name: String? = null): Preset =
            Preset(
                id = packed and 0xffff,
                bank = (packed shr 16) and 0xffff,
                name = name,
            )
    }
}

data class ChannelPresets(
    val notes: Preset = DefaultPreset.Notes.preset,
    val drone: Preset = DefaultPreset.Drone.preset,
    val cadence: Preset = DefaultPreset.Cadence.preset,
) {
    fun forChannel(channel: MidiChannel): Preset =
        when (channel) {
            MidiChannel.Notes -> notes
            MidiChannel.Drone -> drone
            MidiChannel.Cadence -> cadence
        }
}

enum class DefaultPreset(val preset: Preset) {
    Notes(Preset(bank = 0, id = 0)),
    Drone(Preset(bank = 0, id = 42)),
    Cadence(Preset(bank = 0, id = 0)),
}
