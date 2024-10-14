#import "MSAIData.h"
#import "MSAIOrderedDictionary.h"

/// Data contract class for type Data.
@implementation MSAIData

///
/// Adds all members of this class to a dictionary
/// @param dictionary to which the members of this class will be added.
///
- (MSAIOrderedDictionary *)serializeToDictionary {
    MSAIOrderedDictionary *dict = [super serializeToDictionary];
    MSAIOrderedDictionary *baseDataDict = [self.baseData serializeToDictionary];
    if ([NSJSONSerialization isValidJSONObject:baseDataDict]) {
        [dict setObject:baseDataDict forKey:@"baseData"];
    } else {
        NSLog(@"[ApplicationInsights] Some of the telemetry data was not NSJSONSerialization compatible and could not be serialized!");
    }
    return dict;
}

#pragma mark - NSCoding

- (instancetype)initWithCoder:(NSCoder *)coder {
  self = [super initWithCoder:coder];
  if(self) {
    _baseData = [coder decodeObjectForKey:@"self.baseData"];
  }

  return self;
}

- (void)encodeWithCoder:(NSCoder *)coder {
  [super encodeWithCoder:coder];
  [coder encodeObject:self.baseData forKey:@"self.baseData"];
}


@end
