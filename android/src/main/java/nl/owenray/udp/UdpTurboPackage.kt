package nl.owenray.udp

import com.facebook.react.BaseReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider

class UdpTurboPackage : BaseReactPackage() {
    override fun getModule(name: String, reactApplicationContext: ReactApplicationContext): NativeModule? {
        if (name.equals(UdpTurboModule.NAME)) {
            return UdpTurboModule(reactApplicationContext);
        } else {
            return null;
        }
    }

    override fun getReactModuleInfoProvider(): ReactModuleInfoProvider {

        return object : ReactModuleInfoProvider {
            override fun getReactModuleInfos(): Map<String, ReactModuleInfo> {
                val map = HashMap<String, ReactModuleInfo>()
                map[UdpTurboModule.NAME] = ReactModuleInfo(
                  UdpTurboModule.NAME,  // name
                  UdpTurboModule.NAME,  // className
                    false,  // canOverrideExistingModule
                    false,  // needsEagerInit
                    false,  // isCXXModule
                    true // isTurboModule
                )
                return map
            }
        }
    }
}
