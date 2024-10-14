#import "MSAIBase.h"
#import "MSAIOrderedDictionary.h"

/// Data contract class for type Base.
@implementation MSAIBase

///
/// Adds all members of this class to a dictionary
/// @param dictionary to which the members of this class will be added.
///
- (MSAIOrderedDictionary *)serializeToDictionary {
    MSAIOrderedDictionary *dict = [super serializeToDictionary];
    if (self.baseType != nil) {
        [dict setObject:self.baseType forKey:@"baseType"];
    }
    return dict;
}

#pragma mark - NSCoding

- (instancetype)initWithCoder:(NSCoder *)coder {
  self = [super initWithCoder:coder];
  if(self) {
    _baseType = [coder decodeObjectForKey:@"self.baseType"];
  }

  return self;
}

- (void)encodeWithCoder:(NSCoder *)coder {
  [super encodeWithCoder:coder];
  [coder encodeObject:self.baseType forKey:@"self.baseType"];
}

@end
