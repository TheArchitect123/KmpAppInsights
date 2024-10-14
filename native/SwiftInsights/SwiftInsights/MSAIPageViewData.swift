import Foundation

class MSAIPageViewData: MSAIObject {
    
    var envelopeTypeName: String
    var dataTypeName: String
    var url: String?
    var duration: String?
    var referrer: String?
    var referrerData: String?
    
    override init() {
        self.envelopeTypeName = "Microsoft.ApplicationInsights.PageView"
        self.dataTypeName = "PageViewData"
        super.init()
    }
    
    override func serializeToDictionary() -> [String: Any] {
        var dict = super.serializeToDictionary()
        if let url = url {
            dict["url"] = url
        }
        if let duration = duration {
            dict["duration"] = duration
        }
        return dict
    }
    
    // MARK: - NSCoding
    required init?(coder: NSCoder) {
        self.url = coder.decodeObject(forKey: "self.url") as? String
        self.duration = coder.decodeObject(forKey: "self.duration") as? String
        self.referrer = coder.decodeObject(forKey: "self.referrer") as? String
        self.referrerData = coder.decodeObject(forKey: "self.referrerData") as? String
        super.init(coder: coder)
    }
    
    override func encode(with coder: NSCoder) {
        super.encode(with: coder)
        coder.encode(url, forKey: "self.url")
        coder.encode(duration, forKey: "self.duration")
        coder.encode(referrer, forKey: "self.referrer")
        coder.encode(referrerData, forKey: "self.referrerData")
    }
}
