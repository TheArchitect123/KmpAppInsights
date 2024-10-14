import Foundation

class MSAIPageViewPerfData: MSAIPageViewData {
    
    var perfTotal: String?
    var networkConnect: String?
    var sentRequest: String?
    var receivedResponse: String?
    var domProcessing: String?
    
    override init() {
        super.init()
        self.envelopeTypeName = "Microsoft.ApplicationInsights.PageViewPerf"
        self.dataTypeName = "PageViewPerfData"
    }
    
    override func serializeToDictionary() -> [String: Any] {
        var dict = super.serializeToDictionary()
        if let perfTotal = perfTotal {
            dict["perfTotal"] = perfTotal
        }
        if let networkConnect = networkConnect {
            dict["networkConnect"] = networkConnect
        }
        if let sentRequest = sentRequest {
            dict["sentRequest"] = sentRequest
        }
        if let receivedResponse = receivedResponse {
            dict["receivedResponse"] = receivedResponse
        }
        if let domProcessing = domProcessing {
            dict["domProcessing"] = domProcessing
        }
        return dict
    }
    
    // MARK: - NSCoding
    required init?(coder: NSCoder) {
        self.perfTotal = coder.decodeObject(forKey: "self.perfTotal") as? String
        self.networkConnect = coder.decodeObject(forKey: "self.networkConnect") as? String
        self.sentRequest = coder.decodeObject(forKey: "self.sentRequest") as? String
        self.receivedResponse = coder.decodeObject(forKey: "self.receivedResponse") as? String
        self.domProcessing = coder.decodeObject(forKey: "self.domProcessing") as? String
        super.init(coder: coder)
    }
    
    override func encode(with coder: NSCoder) {
        super.encode(with: coder)
        coder.encode(perfTotal, forKey: "self.perfTotal")
        coder.encode(networkConnect, forKey: "self.networkConnect")
        coder.encode(sentRequest, forKey: "self.sentRequest")
        coder.encode(receivedResponse, forKey: "self.receivedResponse")
        coder.encode(domProcessing, forKey: "self.domProcessing")
    }
}
