#import <Foundation/Foundation.h>
#import "MSAIHTTPOperation.h"

@class MSAIEnvelope;
@class MSAITelemetryContext;
@class MSAIAppClient;
@class MSAITelemetryData;
@class MSAISender;
@class MSAIOrderedDictionary;

NS_ASSUME_NONNULL_BEGIN

FOUNDATION_EXPORT NSInteger const debugBatchInterval;
FOUNDATION_EXPORT NSInteger const debugMaxBatchCount;

FOUNDATION_EXPORT NSInteger const defaultBatchInterval;
FOUNDATION_EXPORT NSInteger const defaultMaxBatchCount;

@interface MSAIChannel ()

///-----------------------------------------------------------------------------
/// @name Initialisation
///-----------------------------------------------------------------------------

/**
*  Returns a shared MSAIChannel object.
*
*  @return A singleton MSAIChannel instance ready use
*/
+ (instancetype)sharedChannel;

+ (void)setSharedChannel:(MSAIChannel *)channel;

///-----------------------------------------------------------------------------
/// @name Queue management
///-----------------------------------------------------------------------------

/**
 *  A queue which makes array operations thread safe.
 */
@property (nonatomic, strong) dispatch_queue_t dataItemsOperations;

/**
 *  An integer value that keeps tracks of the number of data items added to the JSON Stream string.
 */
@property (nonatomic, assign) NSUInteger dataItemCount;

/**
 *  Enqueue telemetry data (events, metrics, traces) before processing it.
 *
 *  @param dictionary The dictionary object, which should be processed.
 */
- (void)enqueueDictionary:(MSAIOrderedDictionary *)dictionary;

/**
 *  Manually trigger the MSAIChannel to persist all items currently in its data item queue.
 */
- (void)persistDataItemQueue;

///-----------------------------------------------------------------------------
/// @name JSON Stream
///-----------------------------------------------------------------------------

/**
 *  Adds the specified dictionary to the JSON Stream string.
 *
 *  @param dictionary The dictionary object which is to be added to the JSON Stream queue string.
 */
- (void)appendDictionaryToJsonStream:(MSAIOrderedDictionary *)dictionary;

/**
 *  A C function that serializes a given dictionary to JSON and appends it to a char string
 *
 *  @param dictionary A dictionary which will be serialized to JSON and then appended to the string.
 *  @param string The C string which the dictionary's JSON representation will be appended to.
 */
void msai_appendStringToSafeJsonStream(NSString *string, char *__nonnull*__nonnull jsonStream);

/**
 *  Reset MSAISafeJsonEventsString so we can start appending JSON dictionaries.
 *
 *  @param string The string that will be reset.
 */
void msai_resetSafeJsonStream(char *__nonnull*__nonnull jsonStream);

///-----------------------------------------------------------------------------
/// @name Batching
///-----------------------------------------------------------------------------

/*
 * Interval for sending data to the server in seconds.
 *
 * Default: 15
 */
@property (nonatomic, assign) NSUInteger senderInterval;

/*
 * Threshold for sending data to the server. Default batch size for debugging is 150, for release
 * configuration, the batch size is 5.
 *
 * @warning: we advice to not set the batch size below 5 events.
 *
 * Default: 5
 */
@property (nonatomic, assign) NSUInteger senderBatchSize;

/**
 *  A timer source which is used to flush the queue after a cretain time.
 */
@property (nonatomic, strong, null_unspecified) dispatch_source_t timerSource;

/**
 *  Starts the timer.
 */
- (void)startTimer;

/**
 *  Stops the timer if currently running.
 */
- (void)invalidateTimer;

/**
 *  A method which indicates whether the telemetry pipeline is busy and no new data should be enqueued.
 *  Currently, we drop telemetry data if this returns YES.
 *  This depends on defaultMaxBatchCount and defaultBatchInterval.
 *
 *  @see defaultMaxBatchCount
 *  @see defaultBatchInterval
 *  @return Returns yes if currently no new data should be enqueued on the channel.
 */
- (BOOL)isQueueBusy;

@end
NS_ASSUME_NONNULL_END
