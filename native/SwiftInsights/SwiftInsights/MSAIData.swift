import Foundation

class MSAIData: NSObject, NSCoding {
    
    var baseData: MSAIBaseData?
    
    override init() {
        super.init()
    }
    
    func serializeToDictionary() -> [String: Any] {
        var dict = [String: Any]()
        if let baseDataDict = baseData?.serializeToDictionary(),
           JSONSerialization.isValidJSONObject(baseDataDict) {
            dict["baseData"] = baseDataDict
        } else {
            print("[ApplicationInsights] Some of the telemetry data was not NSJSONSerialization compatible and could not be serialized!")
        }
        return dict
    }
    
    // MARK: - NSCoding
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        self.baseData = coder.decodeObject(forKey: "self.baseData") as? MSAIBaseData
    }
    
    func encode(with coder: NSCoder) {
        super.encode(with: coder)
        coder.encode(baseData, forKey: "self.baseData")
    }
}
