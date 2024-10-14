import Foundation

class MSAIHTTPOperation: Operation, URLSessionDataDelegate {
    
    private var urlRequest: URLRequest?
    private var connection: URLSessionDataTask?
    private var data: Data?
    private var response: URLResponse?
    private var error: Error?
    
    private var _isExecuting: Bool = false
    private var _isFinished: Bool = false
    
    override var isExecuting: Bool {
        return _isExecuting
    }
    
    override var isFinished: Bool {
        return _isFinished
    }
    
    override var isAsynchronous: Bool {
        return true
    }
    
    static func operation(with request: URLRequest) -> MSAIHTTPOperation {
        let op = MSAIHTTPOperation()
        op.urlRequest = request
        return op
    }
    
    override func start() {
        if isCancelled {
            finish()
            return
        }
        
        if !Thread.isMainThread {
            performSelector(onMainThread: #selector(start), with: nil, waitUntilDone: false)
            return
        }
        
        if isCancelled {
            finish()
            return
        }
        
        willChangeValue(forKey: "isExecuting")
        _isExecuting = true
        didChangeValue(forKey: "isExecuting")
        
        guard let urlRequest = urlRequest else { return }
        
        let session = URLSession(configuration: .default, delegate: self, delegateQueue: nil)
        connection = session.dataTask(with: urlRequest)
        connection?.resume()
    }
    
    override func cancel() {
        connection?.cancel()
        super.cancel()
    }
    
    private func finish() {
        willChangeValue(forKey: "isExecuting")
        willChangeValue(forKey: "isFinished")
        _isExecuting = false
        _isFinished = true
        didChangeValue(forKey: "isExecuting")
        didChangeValue(forKey: "isFinished")
    }
    
    // MARK: - URLSessionDataDelegate
    
    func urlSession(_ session: URLSession, dataTask: URLSessionDataTask, didReceive response: URLResponse, completionHandler: @escaping (URLSession.ResponseDisposition) -> Void) {
        data = Data()
        self.response = response
        completionHandler(.allow)
    }
    
    func urlSession(_ session: URLSession, dataTask: URLSessionDataTask, didReceive data: Data) {
        self.data?.append(data)
    }
    
    func urlSession(_ session: URLSession, task: URLSessionTask, didCompleteWithError error: Error?) {
        if let error = error {
            self.error = error
            self.data = nil
        }
        finish()
    }
    
    // MARK: - Public Interface
    
    func setCompletion(_ completion: ((MSAIHTTPOperation, Data?, Error?) -> Void)?, onQueue queue: DispatchQueue?) {
        if let completion = completion {
            let queueToUse = queue ?? DispatchQueue.main
            let weakSelf = self
            super.completionBlock = {
                if !weakSelf.isCancelled {
                    queueToUse.async {
                        completion(weakSelf, weakSelf.data, weakSelf.error)
                    }
                }
            }
        } else {
            super.completionBlock = nil
        }
    }
}
