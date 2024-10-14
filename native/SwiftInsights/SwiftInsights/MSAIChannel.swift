import Foundation

let defaultMaxBatchCount = 50
let defaultBatchInterval = 15

let debugMaxBatchCount = 5
let debugBatchInterval = 3

let MSAIDataItemsOperationsQueue = "com.microsoft.ApplicationInsights.senderQueue"
var MSAISafeJsonEventsString: UnsafeMutablePointer<CChar>? = nil

class MSAIChannel {

    static var _sharedChannel: MSAIChannel? = nil
    static var onceToken = DispatchOnceToken()

    var dataItemCount = 0
    var senderBatchSize: Int
    var senderInterval: Int
    var dataItemsOperations: DispatchQueue
    var timerSource: DispatchSourceTimer?

    // Singleton
    static func sharedChannel() -> MSAIChannel {
        onceToken.performOnce {
            if _sharedChannel == nil {
                _sharedChannel = MSAIChannel()
            }
        }
        return _sharedChannel!
    }

    static func setSharedChannel(_ channel: MSAIChannel) {
        onceToken.reset()
        _sharedChannel = channel
    }

    init() {
        msai_resetSafeJsonStream(&MSAISafeJsonEventsString)
        dataItemCount = 0
        if msai_isDebuggerAttached() {
            senderBatchSize = debugMaxBatchCount
            senderInterval = debugBatchInterval
        } else {
            senderBatchSize = defaultMaxBatchCount
            senderInterval = defaultBatchInterval
        }
        dataItemsOperations = DispatchQueue(label: MSAIDataItemsOperationsQueue, attributes: .concurrent)
    }

    // Queue management
    func isQueueBusy() -> Bool {
        return !MSAIPersistence.sharedInstance.isFreeSpaceAvailable()
    }

    func persistDataItemQueue() {
        invalidateTimer()
        guard let safeJsonString = MSAISafeJsonEventsString, strlen(safeJsonString) > 0 else { return }

        let bundle = Data(bytes: safeJsonString, count: strlen(safeJsonString))
        MSAIPersistence.sharedInstance.persistBundle(bundle, ofType: .regular)

        // Reset the async-signal-safe and item counter
        resetQueue()
    }

    func resetQueue() {
        msai_resetSafeJsonStream(&MSAISafeJsonEventsString)
        dataItemCount = 0
    }

    // Adding to queue
    func enqueueDictionary(_ dictionary: MSAIOrderedDictionary?) {
        guard let dictionary = dictionary else { return }

        dataItemsOperations.async { [weak self] in
            guard let self = self else { return }

            self.appendDictionaryToJsonStream(dictionary)

            if self.dataItemCount >= self.senderBatchSize {
                self.persistDataItemQueue()
            } else if self.dataItemCount == 1 {
                self.startTimer()
            }
        }
    }

    // Serialization Helper
    func serializeDictionaryToJSONString(_ dictionary: MSAIOrderedDictionary) -> String {
        do {
            let data = try JSONSerialization.data(withJSONObject: dictionary, options: [])
            return String(data: data, encoding: .utf8) ?? "{}"
        } catch {
            print("JSONSerialization error: \(error.localizedDescription)")
            return "{}"
        }
    }

    func serializeObjectToJSONData(_ object: Any) -> Data? {
        do {
            let data = try JSONSerialization.data(withJSONObject: object, options: [])
            return data
        } catch {
            print("JSONSerialization error: \(error.localizedDescription)")
            return nil
        }
    }

    // JSON Stream
    func appendDictionaryToJsonStream(_ dictionary: MSAIOrderedDictionary) {
        let jsonString = serializeDictionaryToJSONString(dictionary)
        msai_appendStringToSafeJsonStream(jsonString, &MSAISafeJsonEventsString)
        dataItemCount += 1
    }

    // Batching
    func invalidateTimer() {
        if let timer = timerSource {
            timer.cancel()
            timerSource = nil
        }
    }

    func startTimer() {
        invalidateTimer()

        timerSource = DispatchSource.makeTimerSource(queue: dataItemsOperations)
        timerSource?.schedule(deadline: .now() + .seconds(senderInterval), repeating: .seconds(1))

        timerSource?.setEventHandler { [weak self] in
            self?.persistDataItemQueue()
        }

        timerSource?.resume()
    }
}

// Helper functions
func msai_appendStringToSafeJsonStream(_ string: String, _ jsonString: UnsafeMutablePointer<CChar>?) {
    guard let jsonString = jsonString else { return }
    if string.isEmpty { return }

    var newString: UnsafeMutablePointer<CChar>?
    asprintf(&newString, "%s%.*s\n", jsonString, Int32(string.lengthOfBytes(using: .utf8)), string)
    free(jsonString)
    jsonString = newString
}

func msai_resetSafeJsonStream( _ string: inout UnsafeMutablePointer<CChar>?) {
    free(string)
    string = strdup("")
}
