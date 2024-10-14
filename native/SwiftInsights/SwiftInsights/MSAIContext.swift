import Foundation

class MSAIContext {
    var osVersion: String?
    var osName: String?
    var instrumentationKey: String?
    var deviceModel: String?
    var deviceType: String?
    var appVersion: String?
    
    init(instrumentationKey: String) {
        self.instrumentationKey = instrumentationKey
        self.deviceModel = msai_devicePlatform()
        self.deviceType = msai_deviceType()
        self.osName = msai_osName()
        self.osVersion = msai_osVersionBuild()
        self.appVersion = msai_appVersion()
    }
}
