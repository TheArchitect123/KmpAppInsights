import Foundation

class MSAIDevice: NSObject, NSCoding {
    
    var deviceId: String?
    var ip: String?
    var language: String?
    var locale: String?
    var model: String?
    var network: String?
    var networkName: String?
    var oemName: String?
    var os: String?
    var osVersion: String?
    var roleInstance: String?
    var roleName: String?
    var screenResolution: String?
    var type: String?
    var machineName: String?
    var vmName: String?
    
    override init() {
        super.init()
    }
    
    func serializeToDictionary() -> [String: Any] {
        var dict = [String: Any]()
        
        if let deviceId = deviceId {
            dict["ai.device.id"] = deviceId
        }
        if let ip = ip {
            dict["ai.device.ip"] = ip
        }
        if let language = language {
            dict["ai.device.language"] = language
        }
        if let locale = locale {
            dict["ai.device.locale"] = locale
        }
        if let model = model {
            dict["ai.device.model"] = model
        }
        if let network = network {
            dict["ai.device.network"] = network
        }
        if let networkName = networkName {
            dict["ai.device.networkName"] = networkName
        }
        if let oemName = oemName {
            dict["ai.device.oemName"] = oemName
        }
        if let os = os {
            dict["ai.device.os"] = os
        }
        if let osVersion = osVersion {
            dict["ai.device.osVersion"] = osVersion
        }
        if let roleInstance = roleInstance {
            dict["ai.device.roleInstance"] = roleInstance
        }
        if let roleName = roleName {
            dict["ai.device.roleName"] = roleName
        }
        if let screenResolution = screenResolution {
            dict["ai.device.screenResolution"] = screenResolution
        }
        if let type = type {
            dict["ai.device.type"] = type
        }
        if let machineName = machineName {
            dict["ai.device.machineName"] = machineName
        }
        if let vmName = vmName {
            dict["ai.device.vmName"] = vmName
        }
        
        return dict
    }
    
    // MARK: - NSCoding
    required init?(coder: NSCoder) {
        super.init()
        self.deviceId = coder.decodeObject(forKey: "self.deviceId") as? String
        self.ip = coder.decodeObject(forKey: "self.ip") as? String
        self.language = coder.decodeObject(forKey: "self.language") as? String
        self.locale = coder.decodeObject(forKey: "self.locale") as? String
        self.model = coder.decodeObject(forKey: "self.model") as? String
        self.network = coder.decodeObject(forKey: "self.network") as? String
        self.networkName = coder.decodeObject(forKey: "self.networkName") as? String
        self.oemName = coder.decodeObject(forKey: "self.oemName") as? String
        self.os = coder.decodeObject(forKey: "self.os") as? String
        self.osVersion = coder.decodeObject(forKey: "self.osVersion") as? String
        self.roleInstance = coder.decodeObject(forKey: "self.roleInstance") as? String
        self.roleName = coder.decodeObject(forKey: "self.roleName") as? String
        self.screenResolution = coder.decodeObject(forKey: "self.screenResolution") as? String
        self.type = coder.decodeObject(forKey: "self.type") as? String
        self.machineName = coder.decodeObject(forKey: "self.machineName") as? String
        self.vmName = coder.decodeObject(forKey: "self.vmName") as? String
    }
    
    func encode(with coder: NSCoder) {
        coder.encode(deviceId, forKey: "self.deviceId")
        coder.encode(ip, forKey: "self.ip")
        coder.encode(language, forKey: "self.language")
        coder.encode(locale, forKey: "self.locale")
        coder.encode(model, forKey: "self.model")
        coder.encode(network, forKey: "self.network")
        coder.encode(networkName, forKey: "self.networkName")
        coder.encode(oemName, forKey: "self.oemName")
        coder.encode(os, forKey: "self.os")
        coder.encode(osVersion, forKey: "self.osVersion")
        coder.encode(roleInstance, forKey: "self.roleInstance")
        coder.encode(roleName, forKey: "self.roleName")
        coder.encode(screenResolution, forKey: "self.screenResolution")
        coder.encode(type, forKey: "self.type")
        coder.encode(machineName, forKey: "self.machineName")
        coder.encode(vmName, forKey: "self.vmName")
    }
}
