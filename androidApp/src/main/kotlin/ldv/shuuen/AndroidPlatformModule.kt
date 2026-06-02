package ldv.shuuen

import android.content.Context
import ldv.shuuen.audio.SoundFontProvider
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

fun androidPlatformModules(context: Context): List<Module> =
    listOf(
        module {
            single<SoundFontProvider> { AndroidSoundFontProvider(context.applicationContext) }
        },
    )

private class AndroidSoundFontProvider(
    private val context: Context,
) : SoundFontProvider {
    override suspend fun defaultSoundFontPath(): String {
        val target = File(context.cacheDir, "GeneralUser-GS.sf2")
        if (!target.exists()) {
            context.assets.open("GeneralUser-GS.sf2").use { input ->
                target.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
        return target.absolutePath
    }
}
