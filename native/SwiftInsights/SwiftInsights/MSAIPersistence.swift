import Foundation

let kHighPrioString = "highPrio"
let kRegularPrioString = "regularPrio"
let kFileBaseString = "app-insights-bundle-"

let MSAIPersistenceSuccessNotification = Notification.Name("MSAIPersistenceSuccessNotification")
let kPersistenceQueueString = "com.microsoft.ApplicationInsights.persistenceQueue"
let defaultFileCount: UInt = 50

class MSAIPersistence {
    static let sharedInstance: MSAIPersistence = {
        let instance = MSAIPersistence()
        instance.createApplicationSupportDirectoryIfNeeded()
        return instance
    }()
    
    private var maxFileCountReached = true
    private let persistenceQueue = DispatchQueue(label: kPersistenceQueueString)
    private var requestedBundlePaths = [String]()
    private let maxFileCount = defaultFileCount

    func persistBundle(_ bundle: Data, ofType type: MSAIPersistenceType, enableNotifications: Bool = true, completionBlock: ((Bool) -> Void)? = nil) {
        let fileURL = newFileURLForPersistenceType(type)
        if !bundle.isEmpty {
            persistenceQueue.async { [weak self] in
                guard let self = self else { return }
                let success = (try? bundle.write(to: URL(fileURLWithPath: fileURL), options: .atomic)) != nil
                if success {
                    print("Wrote \(fileURL)")
                    if enableNotifications {
                        self.sendBundleSavedNotification()
                    }
                }
                completionBlock?(success)
            }
        } else {
            print("Unable to write \(fileURL)")
            completionBlock?(false)
        }
    }
    
    func isFreeSpaceAvailable() -> Bool {
        return !maxFileCountReached
    }
    
    func requestNextPath() -> String? {
        var path: String?
        persistenceQueue.sync {
            path = nextURL(withPriority: .highPriority) ?? nextURL(withPriority: .regular)
            if let path = path {
                requestedBundlePaths.append(path)
            }
        }
        return path
    }
    
    func bundle(atPath path: String) -> [Any]? {
        guard path.contains(kFileBaseString) else { return nil }
        return NSKeyedUnarchiver.unarchiveObject(withFile: path) as? [Any]
    }

    func data(atPath path: String) -> Data? {
        guard path.contains(kFileBaseString) else { return nil }
        return try? Data(contentsOf: URL(fileURLWithPath: path))
    }
    
    func deleteFile(atPath path: String) {
        persistenceQueue.sync {
            if path.contains(kFileBaseString) {
                do {
                    try FileManager.default.removeItem(atPath: path)
                    print("Successfully deleted file at path \(path)")
                    requestedBundlePaths.removeAll { $0 == path }
                } catch {
                    print("Error deleting file at path \(path)")
                }
            } else {
                print("Empty path, so nothing can be deleted")
            }
        }
    }
    
    func giveBackRequestedPath(_ path: String) {
        persistenceQueue.async { [weak self] in
            self?.requestedBundlePaths.removeAll { $0 == path }
        }
    }

    // MARK: - Private
    
    private func newFileURLForPersistenceType(_ type: MSAIPersistenceType) -> String {
        let fileDir = folderPathForPersistenceType(type)
        let uuid = UUID().uuidString
        let fileName = "\(kFileBaseString)\(uuid)"
        return (fileDir as NSString).appendingPathComponent(fileName)
    }
    
    private func createFolderIfNeeded(atPath path: String) {
        if !FileManager.default.fileExists(atPath: path) {
            try? FileManager.default.createDirectory(atPath: path, withIntermediateDirectories: true)
        }
    }
    
    private func createApplicationSupportDirectoryIfNeeded() {
        let applicationSupportDir = NSSearchPathForDirectoriesInDomains(.applicationSupportDirectory, .userDomainMask, true).last!
        if !FileManager.default.fileExists(atPath: applicationSupportDir) {
            try? FileManager.default.createDirectory(atPath: applicationSupportDir, withIntermediateDirectories: true)
        }
    }

    private func nextURL(withPriority type: MSAIPersistenceType) -> String? {
        let directoryPath = folderPathForPersistenceType(type)
        let fileNames = (try? FileManager.default.contentsOfDirectory(atPath: directoryPath)) ?? []
        
        if type == .regular {
            maxFileCountReached = fileNames.count >= maxFileCount
        }
        
        for fileName in fileNames {
            let absolutePath = (directoryPath as NSString).appendingPathComponent(fileName)
            if !requestedBundlePaths.contains(absolutePath) {
                return absolutePath
            }
        }
        return nil
    }
    
    private func folderPathForPersistenceType(_ type: MSAIPersistenceType) -> String {
        let persistenceFolder = NSSearchPathForDirectoriesInDomains(.applicationSupportDirectory, .userDomainMask, true).last!
        let subfolderPath: String
        switch type {
        case .highPriority:
            subfolderPath = kHighPrioString
        case .regular:
            subfolderPath = kRegularPrioString
        }
        return (persistenceFolder as NSString).appendingPathComponent(subfolderPath)
    }
    
    private func sendBundleSavedNotification() {
        DispatchQueue.main.async {
            NotificationCenter.default.post(name: MSAIPersistenceSuccessNotification, object: nil)
        }
    }
}

enum MSAIPersistenceType {
    case highPriority
    case regular
}
