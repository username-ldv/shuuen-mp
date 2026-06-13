package ldv.shuuen.di

import ldv.shuuen.data.audio.BassMidiEngine
import ldv.shuuen.domain.audio.engine.MidiEngine
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

expect val platformModule: Module

val commonModule = module {
  single<BassMidiEngine>() bind MidiEngine::class

  includes(dataModule, navigationModule)
}
