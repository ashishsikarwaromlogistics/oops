package com.example.omoperation

import com.example.omoperation.adapters.Dash_Adapt
import com.example.omoperation.adapters.DrawerAdapter
import com.example.omoperation.repositories.DasboardRepo
import com.example.omoperation.repositories.DrawerRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Named


@InstallIn(ActivityComponent::class)
@Module
class UserModule {

    @Provides
    @Named("dashboard")
    fun Dashboard():Dash_Adapt.DashInterface{
        return DasboardRepo()
    }

    @Provides
    @Named("drawer")
    fun CallDrawer():DrawerAdapter.Drawerinterface{
        return DrawerRepo()
    }

    @Provides
    @Named("firebase")
    fun ProvideUserRepo():UserRepository{
        return FirebaseRepo()
    }
  @Provides
  @Named("sql")
    fun ProvideUserRepo2():UserRepository{
        return SqLRepo()
    }

}