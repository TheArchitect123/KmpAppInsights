import Foundation

class MSAISender {
    
    static let sharedSender = MSAISender()
    let senderQueue = DispatchQueue(label: "com.microsoft.ApplicationInsights.senderQueue", attributes: .concurrent)
    var appClient: MSAIAppClient?
    var maxRequestCount: Int = 10
    private var _runningRequestsCount: Int = 0
    let recoverableStatusCodes: [Int] = [429, 408, 500, 503, 511]
    
    // MARK: - Initialization
    
    private init() {
        // Private initializer for singleton pattern
    }
    
    // MARK: - Network status

    func configure(withAppClient appClient: MSAIAppClient) {
        self.appClient = appClient
        self.maxRequestCount = 10
        self.registerObservers()
    }

    // MARK: - Handle persistence events

    private func registerObservers() {
        NotificationCenter.default.addObserver(forName: NSNotification.Name("MSAIPersistenceSuccessNotification"), object: nil, queue: nil) { [weak self] _ in
            self?.sendSavedData()
        }
    }
    
    // MARK: - Sending

    func sendSavedData() {
        synchronized(self) {
            if _runningRequestsCount >= maxRequestCount {
                return
            }
            _runningRequestsCount += 1
        }

        DispatchQueue.global(qos: .default).async {
            let path = MSAIPersistence.sharedInstance().requestNextPath()
            if let path = path, let data = MSAIPersistence.sharedInstance().data(atPath: path) {
                self.sendData(data, withPath: path)
            }
        }
    }
    
    func sendData(_ data: Data, withPath path: String) {
        if data.isEmpty {
            self.runningRequestsCount -= 1
            return
        }

        let contentType = self.contentType(forData: data)
        let gzippedData = data.gzippedData()
        let request = self.request(forData: gzippedData, withContentType: contentType)
        sendRequest(request, path: path)
    }

    private func sendRequest(_ request: URLRequest?, path: String) {
        guard let path = path, let request = request else { return }

        let operation = appClient?.operation(with: request, queue: senderQueue) { [weak self] operation, responseData, error in
            guard let self = self else { return }

            self.runningRequestsCount -= 1
            let statusCode = operation?.response?.statusCode ?? 0

            if let responseData = responseData, self.shouldDeleteData(withStatusCode: statusCode) {
                print("Sent data with status code: \(statusCode)")
                print("Response data:\n\(String(describing: try? JSONSerialization.jsonObject(with: responseData, options: [])))")
                MSAIPersistence.sharedInstance().deleteFile(atPath: path)
                self.sendSavedData()
            } else {
                print("Sending ApplicationInsights data failed")
                print("Error description: \(String(describing: error?.localizedDescription))")
                MSAIPersistence.sharedInstance().giveBackRequestedPath(path)
            }
        }

        appClient?.enqueueHTTPOperation(operation)
    }
    
    // MARK: - Helper

    private func request(forData data: Data, withContentType contentType: String) -> URLRequest? {
        guard let appClient = appClient else { return nil }
        
        var request = appClient.request(withMethod: "POST", path: endpointPath, parameters: nil)
        request.httpBody = data
        request.cachePolicy = .reloadIgnoringLocalCacheData
        
        let headers = [
            "Charset": "UTF-8",
            "Content-Encoding": "gzip",
            "Content-Type": contentType,
            "Accept-Encoding": "gzip"
        ]
        
        request.allHTTPHeaderFields = headers
        return request
    }

    private func shouldDeleteData(withStatusCode statusCode: Int) -> Bool {
        return !recoverableStatusCodes.contains(statusCode)
    }

    private func contentType(forData data: Data) -> String {
        let lastByte = data.last ?? 0
        if lastByte == 0x0a {
            return "application/x-json-stream"
        }
        return "application/json"
    }
    
    // MARK: - Getter/Setter

    var runningRequestsCount: Int {
        get {
            synchronized(self) {
                return _runningRequestsCount
            }
        }
        set {
            synchronized(self) {
                _runningRequestsCount = newValue
            }
        }
    }
    
    private func synchronized(_ lock: AnyObject, closure: () -> Void) {
        objc_sync_enter(lock)
        closure()
        objc_sync_exit(lock)
    }
}
