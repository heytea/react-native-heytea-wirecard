package com.heytea.wirecard

import android.view.View
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ReactShadowNode
import com.facebook.react.uimanager.ViewManager

/**
 * Package     ：com.heytea.wirecard
 * Description ：
 * Company     ：Heytea
 * Author      ：Created by ChengGuang
 * CreateTime  ：2020/5/19.
 */
class RNWirecardPackage : ReactPackage {



    override fun createNativeModules(reactContext: ReactApplicationContext): MutableList<NativeModule> {
       val modules = ArrayList<NativeModule>()
        modules.add(RNWirecardModule(reactContext))
        return modules
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): MutableList<ViewManager<View, ReactShadowNode<*>>> {
        return emptyList<ViewManager<View,ReactShadowNode<*>>>().toMutableList()
    }
}