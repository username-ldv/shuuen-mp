package ldv.shuuen

import ldv.shuuen.audio.SoundFontProvider
import org.koin.core.module.Module
import org.koin.dsl.module
import java.nio.file.Files
import kotlin.io.path.exists
import kotlin.io.path.outputStream

fun desktopPlatformModules(): List<Module> =
    listOf(
        module {
            single<SoundFontProvider> { DesktopSoundFontProvider() }
        },
    )

private class DesktopSoundFontProvider : SoundFontProvider {
    override suspend fun defaultSoundFontPath(): String {
        val target = Files.createTempDirectory("shuuen-soundfont").resolve("GeneralUser-GS.sf2")
        if (target.exists()) return target.toAbsolutePath().toString()

        val stream = DesktopSoundFontProvider::class.java
            .getResourceAsStream("/soundfonts/GeneralUser-GS.sf2")
            ?: error("Default soundfont resource was not found.")

        stream.use { input ->
            target.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return target.toAbsolutePath().toString()
    }
}
