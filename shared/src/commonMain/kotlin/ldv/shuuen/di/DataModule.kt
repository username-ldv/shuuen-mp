package ldv.shuuen.di

import ldv.shuuen.data.database.AppDatabase
import ldv.shuuen.data.database.dao.ContextDao
import ldv.shuuen.data.database.dao.SinglesLevelDao
import ldv.shuuen.data.repository.local.ContextLocalRepositoryImpl
import ldv.shuuen.data.repository.local.SinglesLocalLevelRepositoryImpl
import ldv.shuuen.data.settings.KStoreSettingsRepository
import ldv.shuuen.domain.repository.SettingsRepository
import ldv.shuuen.domain.repository.local.ContextLocalRepository
import ldv.shuuen.domain.repository.local.SinglesLocalLevelRepository
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

val dataModule = module {
  single<KStoreSettingsRepository>() bind SettingsRepository::class

  single<SinglesLevelDao> { get<AppDatabase>().singlesLevelDao() }
  single<ContextDao> { get<AppDatabase>().contextDao() }

  single<ContextLocalRepositoryImpl>() bind ContextLocalRepository::class
  single<SinglesLocalLevelRepositoryImpl>() bind SinglesLocalLevelRepository::class
}