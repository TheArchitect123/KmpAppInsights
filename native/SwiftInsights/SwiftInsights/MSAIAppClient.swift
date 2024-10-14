import Foundation

class MSAIAppClient {

    var baseURL: URL
    private var operationQueue: OperationQueue = {
        let queue = OperationQueue()
        queue.maxConcurrentOperationCount = 1
        return queue
    }()

    init(baseURL: URL) {
        self.baseURL = baseURL
    }

    deinit {
        cancelAllOperations()
    }

    func request(method: String, path: String?, parameters: [String: String]?) -> URLRequest? {
        let fullPath = path ?? ""
        guard let endpoint = URL(string: fullPath, relativeTo: baseURL) else { return nil }

        var request = URLRequest(url: endpoint)
        request.httpMethod = method

        if let params = parameters {
            if method == "GET" {
                var urlComponents = URLComponents(url: endpoint, resolvingAgainstBaseURL: false)
                urlComponents?.query = params.map { "\($0)=\($1)" }.joined(separator: "&")
                request.url = urlComponents?.url
            } else {
                let boundary = "----FOO"
                let contentType = "multipart/form-data; boundary=\(boundary)"
                request.setValue(contentType, forHTTPHeaderField: "Content-type")

                var postBody = Data()
                params.forEach { key, value in
                    postBody.append(dataWithPostValue(value: value, forKey: key, boundary: boundary))
                }
                postBody.append("--\(boundary)--\r\n".data(using: .utf8)!)
                request.httpBody = postBody
            }
        }
        return request
    }

    private func dataWithPostValue(value: String, forKey key: String, boundary: String) -> Data {
        var data = Data()
        data.append("--\(boundary)\r\n".data(using: .utf8)!)
        data.append("Content-Disposition: form-data; name=\"\(key)\"\r\n".data(using: .utf8)!)
        data.append("\r\n".data(using: .utf8)!)
        data.append(value.data(using: .utf8)!)
        data.append("\r\n".data(using: .utf8)!)
        return data
    }

    func operation(request: URLRequest, queue: DispatchQueue, completion: @escaping (Data?, URLResponse?, Error?) -> Void) -> URLSessionDataTask {
        let task = URLSession.shared.dataTask(with: request, completionHandler: completion)
        queue.async {
            task.resume()
        }
        return task
    }

    func getPath(path: String, parameters: [String: String]?, completion: @escaping (Data?, URLResponse?, Error?) -> Void) {
        guard let request = request(method: "GET", path: path, parameters: parameters) else { return }
        let task = operation(request: request, queue: DispatchQueue.main, completion: completion)
        enqueueHTTPOperation(task)
    }

    func postPath(path: String, parameters: [String: String]?, completion: @escaping (Data?, URLResponse?, Error?) -> Void) {
        guard let request = request(method: "POST", path: path, parameters: parameters) else { return }
        let task = operation(request: request, queue: DispatchQueue.main, completion: completion)
        enqueueHTTPOperation(task)
    }

    private func enqueueHTTPOperation(_ task: URLSessionDataTask) {
        operationQueue.addOperation {
            task.resume()
        }
    }

    func cancelOperations(path: String?, method: String?) -> Int {
        var cancelledOperations = 0
        operationQueue.operations.forEach { operation in
            guard let task = operation as? URLSessionDataTask else { return }
            if let requestMethod = task.originalRequest?.httpMethod, requestMethod == method {
                task.cancel()
                cancelledOperations += 1
            }
            if let url = task.originalRequest?.url?.absoluteString, url.contains(path ?? "") {
                task.cancel()
                cancelledOperations += 1
            }
        }
        return cancelledOperations
    }

    func cancelAllOperations() -> Int {
        var cancelledOperations = 0
        operationQueue.operations.forEach { operation in
            guard let task = operation as? URLSessionDataTask else { return }
            task.cancel()
            cancelledOperations += 1
        }
        return cancelledOperations
    }
}
