import Foundation

class MSAIEnvelope: NSObject, NSCoding {
    
    var version: NSNumber?
    var name: String?
    var time: String?
    var sampleRate: NSNumber?
    var seq: String?
    var iKey: String?
    var flags: NSNumber?
    var deviceId: String?
    var os: String?
    var osVer: String?
    var appId: String?
    var appVer: String?
    var userId: String?
    var tags: MSAIOrderedDictionary?
    var data: MSAIData?
    
    override init() {
        self.version = 1
        self.sampleRate = 100.0
        self.tags = MSAIOrderedDictionary()
        super.init()
    }
    
    func serializeToDictionary() -> [String: Any] {
        var dict = [String: Any]()
        
        if let version = version {
            dict["ver"] = version
        }
        if let name = name {
            dict["name"] = name
        }
        if let time = time {
            dict["time"] = time
        }
        if let sampleRate = sampleRate {
            dict["sampleRate"] = sampleRate
        }
        if let seq = seq {
            dict["seq"] = seq
        }
        if let iKey = iKey {
            dict["iKey"] = iKey
        }
        if let flags = flags {
            dict["flags"] = flags
        }
        if let deviceId = deviceId {
            dict["deviceId"] = deviceId
        }
        if let os = os {
            dict["os"] = os
        }
        if let osVer = osVer {
            dict["osVer"] = osVer
        }
        if let appId = appId {
            dict["appId"] = appId
        }
        if let appVer = appVer {
            dict["appVer"] = appVer
        }
        if let userId = userId {
            dict["userId"] = userId
        }
        if let tags = tags {
            dict["tags"] = tags
        }
        
        if let dataDict = data?.serializeToDictionary(),
           JSONSerialization.isValidJSONObject(dataDict) {
            dict["data"] = dataDict
        } else {
            print("[ApplicationInsights] Some of the telemetry data was not NSJSONSerialization compatible and could not be serialized!")
        }
        
        return dict
    }
    
    // MARK: - NSCoding
    required init?(coder: NSCoder) {
        super.init()
        self.version = coder.decodeObject(forKey: "self.version") as? NSNumber
        self.name = coder.decodeObject(forKey: "self.name") as? String
        self.time = coder.decodeObject(forKey: "self.time") as? String
        self.sampleRate = coder.decodeObject(forKey: "self.sampleRate") as? NSNumber
        self.seq = coder.decodeObject(forKey: "self.seq") as? String
        self.iKey = coder.decodeObject(forKey: "self.iKey") as? String
        self.flags = coder.decodeObject(forKey: "self.flags") as? NSNumber
        self.deviceId = coder.decodeObject(forKey: "self.deviceId") as? String
        self.os = coder.decodeObject(forKey: "self.os") as? String
        self.osVer = coder.decodeObject(forKey: "self.osVer") as? String
        self.appId = coder.decodeObject(forKey: "self.appId") as? String
        self.appVer = coder.decodeObject(forKey: "self.appVer") as? String
        self.userId = coder.decodeObject(forKey: "self.userId") as? String
        self.tags = coder.decodeObject(forKey: "self.tags") as? MSAIOrderedDictionary
        self.data = coder.decodeObject(forKey: "self.data") as? MSAIData
    }
    
    func encode(with coder: NSCoder) {
        coder.encode(version, forKey: "self.version")
        coder.encode(name, forKey: "self.name")
        coder.encode(time, forKey: "self.time")
        coder.encode(sampleRate, forKey: "self.sampleRate")
        coder.encode(seq, forKey: "self.seq")
        coder.encode(iKey, forKey: "self.iKey")
        coder.encode(flags, forKey: "self.flags")
        coder.encode(deviceId, forKey: "self.deviceId")
        coder.encode(os, forKey: "self.os")
        coder.encode(osVer, forKey: "self.osVer")
        coder.encode(appId, forKey: "self.appId")
        coder.encode(appVer, forKey: "self.appVer")
        coder.encode(userId, forKey: "self.userId")
        coder.encode(tags, forKey: "self.tags")
        coder.encode(data, forKey: "self.data")
    }
}
