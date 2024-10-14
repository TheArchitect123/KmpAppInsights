import Foundation

class MSAIOrderedDictionary: NSObject {
    
    private var dictionary: NSMutableDictionary
    private var order: NSMutableArray
    
    override init() {
        self.dictionary = NSMutableDictionary()
        self.order = NSMutableArray()
        super.init()
    }
    
    init(capacity numItems: Int) {
        self.dictionary = NSMutableDictionary(capacity: numItems)
        self.order = NSMutableArray()
        super.init()
    }
    
    func setObject(_ object: Any, forKey key: Any) {
        if dictionary[key] == nil {
            order.add(key)
        }
        dictionary[key] = object
    }
    
    func keyEnumerator() -> NSEnumerator {
        return order.objectEnumerator()
    }
    
    func object(forKey key: Any) -> Any? {
        return dictionary[key]
    }
    
    var count: Int {
        return dictionary.count
    }
}
