import Foundation

class MSAIMetricData: NSObject, NSCoding {
    
    var envelopeTypeName: String
    var dataTypeName: String
    var version: NSNumber
    var metrics: [MSAIDataPoint]
    var properties: [String: Any]
    
    override init() {
        self.envelopeTypeName = "Microsoft.ApplicationInsights.Metric"
        self.dataTypeName = "MetricData"
        self.version = 2
        self.metrics = []
        self.properties = [:]
        super.init()
    }
    
    func serializeToDictionary() -> [String: Any] {
        var dict = [String: Any]()
        var metricsArray: [[String: Any]] = []
        
        for metricsElement in metrics {
            metricsArray.append(metricsElement.serializeToDictionary())
        }
        
        dict["metrics"] = metricsArray
        dict["properties"] = properties
        return dict
    }
    
    // MARK: - NSCoding
    required init?(coder: NSCoder) {
        self.metrics = coder.decodeObject(forKey: "self.metrics") as? [MSAIDataPoint] ?? []
        super.init()
    }
    
    func encode(with coder: NSCoder) {
        coder.encode(metrics, forKey: "self.metrics")
    }
}
