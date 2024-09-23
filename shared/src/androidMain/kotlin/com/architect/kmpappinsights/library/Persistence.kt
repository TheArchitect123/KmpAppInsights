package com.architect.kmpappinsights.library

import android.content.Context
import com.architect.kmpappinsights.library.Sender
import com.architect.kmpappinsights.logging.InternalLogging
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.ref.WeakReference
import java.util.UUID
import kotlin.concurrent.Volatile

internal class Persistence protected constructor(context: Context?) {
    private val servedFiles: ArrayList<File>

    /**
     * A weak reference to the app context
     */
    private val weakContext: WeakReference<Context?>? =
        WeakReference(context)

    /**
     * Restrict access to the default constructor
     *
     * @param context android Context object
     */
    init {
        createDirectoriesIfNecessary()
        this.servedFiles = ArrayList(51)
    }

    /**
     * Serializes a IJsonSerializable[] and calls:
     *
     * @param data         the data to save to disk
     * @param highPriority the priority to save the data with
     * @see Persistence.writeToDisk
     */
    fun persist(data: Array<String?>, highPriority: Boolean) {
        if (!this.isFreeSpaceAvailable(highPriority)) {
            InternalLogging.warn(TAG, "No free space on disk to flush data.")
            Sender.Companion.getInstance()!!.sendNextFile()
        } else {
            val buffer = StringBuilder()
            val isSuccess: Boolean
            for (aData in data) {
                buffer.append('\n')
                buffer.append(aData)
            }
            val serializedData = buffer.toString()
            isSuccess = this.writeToDisk(serializedData, highPriority)
            if (isSuccess) {
                val sender = Sender.getInstance()
                if (sender != null && !highPriority) {
                    Sender.Companion.getInstance()!!.sendNextFile()
                }
            }
        }
    }

    /**
     * Saves a string to disk.
     *
     * @param data         the string to save
     * @param highPriority the priority we want to use for persisting the data
     * @return true if the operation was successful, false otherwise
     */
    protected fun writeToDisk(data: String, highPriority: Boolean): Boolean {
        val uuid = UUID.randomUUID().toString()
        var isSuccess = false
        val context = this.context
        if (context != null) {
            var outputStream: FileOutputStream? = null
            try {
                var filesDir = this.context!!.filesDir
                if (highPriority) {
                    filesDir =
                        File(filesDir.toString() + AI_SDK_DIRECTORY + HIGH_PRIO_DIRECTORY + uuid)
                    outputStream = FileOutputStream(filesDir, true)
                    InternalLogging.warn(TAG, "Saving data" + "HIGH PRIO")
                } else {
                    filesDir =
                        File(filesDir.toString() + AI_SDK_DIRECTORY + REGULAR_PRIO_DIRECTORY + uuid)
                    outputStream = FileOutputStream(filesDir, true)
                    InternalLogging.warn(TAG, "Saving data" + "REGULAR PRIO")
                }
                outputStream.write(data.toByteArray())

                isSuccess = true
                InternalLogging.warn(TAG, "Saved data")
            } catch (e: Exception) {
                //Do nothing
                InternalLogging.warn(TAG, "Failed to save data with exception: $e")
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        return isSuccess
    }

    /**
     * Retrieves the data from a given path.
     *
     * @param file reference to a file on disk
     * @return the next item from disk or empty string if anything goes wrong
     */
    fun load(file: File?): String {
        val buffer = StringBuilder()
        if (file != null) {
            var reader: BufferedReader? = null
            try {
                val inputStream = FileInputStream(file)
                val streamReader = InputStreamReader(inputStream)
                reader = BufferedReader(streamReader)
                //comment: we can't use BufferedReader's readline() as this removes linebreaks that
                //are required for JSON stream
                var c: Int
                while ((reader.read().also { c = it }) != -1) {
                    //Cast c to char. As it's not -1, we won't get a problem
                    buffer.append(c.toChar())
                }
            } catch (e: Exception) {
                InternalLogging.warn(
                    TAG, "Error reading telemetry data from file with exception message "
                            + e.message
                )
            } finally {
                try {
                    reader?.close()
                } catch (e: IOException) {
                    InternalLogging.warn(
                        TAG, "Error closing stream."
                                + e.message
                    )
                }
            }
        }

        return buffer.toString()
    }

    /**
     * Get a reference to the next available file. High priority is served before regular priority.
     *
     * @return the next available file.
     */
    fun nextAvailableFile(): File? {
        synchronized(LOCK) {
            val file = this.nextHighPrioFile()
            if (file != null) {
                return file
            } else {
                InternalLogging.info(
                    TAG,
                    "High prio file was empty",
                    "(That's the default if no crashes present"
                )
                return this.nextRegularPrioFile()
            }
        }
    }


    private fun nextHighPrioFile(): File? {
        val context = context
        if (context != null) {
            val path = context.filesDir.toString() + AI_SDK_DIRECTORY + HIGH_PRIO_DIRECTORY
            val directory = File(path)
            InternalLogging.info(TAG, "Returning High Prio File: ", path)

            return this.nextAvailableFileInDirectory(directory)
        }

        InternalLogging.warn(TAG, "Couldn't provide next file, the context for persistence is null")
        return null
    }


    private fun nextRegularPrioFile(): File? {
        val context = context
        if (context != null) {
            val path = context.filesDir.toString() + AI_SDK_DIRECTORY + REGULAR_PRIO_DIRECTORY
            val directory = File(path)
            InternalLogging.info(TAG, "Returning Regular Prio File: $path")
            return this.nextAvailableFileInDirectory(directory)
        }

        InternalLogging.warn(TAG, "Couldn't provide next file, the context for persistence is null")
        return null
    }

    /**
     * @param directory reference to the directory
     * @return reference to the next available file, null if no file is available
     */
    private fun nextAvailableFileInDirectory(directory: File?): File? {
        synchronized(LOCK) {
            if (directory != null) {
                val files = directory.listFiles()
                var file: File

                if ((files != null) && (files.size > 0)) {
                    for (i in 0..files.size - 1) {
                        InternalLogging.info(
                            TAG,
                            "The directory $directory",
                            " ITERATING over " + files.size + " files"
                        )

                        file = files[i]
                        InternalLogging.info(TAG, "The directory $file", " FOUND")

                        if (!servedFiles.contains(file)) {
                            InternalLogging.info(
                                TAG,
                                "The directory $file",
                                " ADDING TO SERVED AND RETURN"
                            )

                            servedFiles.add(file)
                            return file //we haven't served the file, return it
                        } else {
                            InternalLogging.info(TAG, "The directory $file", " WAS ALREADY SERVED")
                        }
                    }
                }
                InternalLogging.info(TAG, "The directory $directory", " NO FILES")
            }
            if (directory != null) {
                InternalLogging.info(
                    TAG,
                    "The directory $directory",
                    "Did not contain any unserved files"
                )
            }
            return null //no files in directory or no directory
        }
    }

    /**
     * delete a file from disk and remove it from the list of served files if deletion was successful
     *
     * @param file reference to the file we want to delete
     */
    fun deleteFile(file: File?) {
        if (file != null) {
            synchronized(LOCK) {
                // always delete the file
                val deletedFile = file.delete()
                if (!deletedFile) {
                    InternalLogging.warn(TAG, "Error deleting telemetry file $file")
                } else {
                    InternalLogging.info(
                        TAG,
                        "Successfully deleted telemetry file ",
                        file.toString()
                    )
                    servedFiles.remove(file)
                }
            }
        } else {
            InternalLogging.warn(TAG, "Couldn't delete file, the reference to the file was null")
        }
    }

    /**
     * Make a file available to be served again
     *
     * @param file reference to the file that should be made available so it can be sent again later
     */
    fun makeAvailable(file: File?) {
        synchronized(LOCK) {
            if (file != null) {
                servedFiles.remove(file)
            }
        }
    }

    /**
     * Check if we haven't reached MAX_FILE_COUNT yet
     *
     * @param highPriority indicates which directory to check for available files
     */
    fun isFreeSpaceAvailable(highPriority: Boolean): Boolean {
        synchronized(LOCK) {
            val context = context
            if (context != null) {
                val path =
                    if (highPriority) (context.filesDir.toString() + AI_SDK_DIRECTORY + HIGH_PRIO_DIRECTORY) else (this.context!!.filesDir.toString() + AI_SDK_DIRECTORY + REGULAR_PRIO_DIRECTORY)
                if (path != null && (path.length > 0)) {
                    val dir = File(path)
                    if (dir != null) {
                        return (dir.listFiles().size < MAX_FILE_COUNT)
                    }
                }
            }
            return false
        }
    }

    /**
     * create local folders for both priorities if they are not present, yet.
     */
    private fun createDirectoriesIfNecessary() {
        val filesDirPath = context!!.filesDir.path
        //create high prio directory
        var dir = File(filesDirPath + AI_SDK_DIRECTORY + HIGH_PRIO_DIRECTORY)
        val successMessage = "Successfully created directory"
        val errorMessage = "Error creating directory"
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                InternalLogging.info(TAG, successMessage, "high priority")
            } else {
                InternalLogging.info(TAG, errorMessage, "high priority")
            }
        }
        //create regular prio directory
        dir = File(filesDirPath + AI_SDK_DIRECTORY + REGULAR_PRIO_DIRECTORY)
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                InternalLogging.info(TAG, successMessage, "regular priority")
            } else {
                InternalLogging.info(TAG, errorMessage, "regular priority")
            }
        }
    }

    private val context: Context?
        /**
         * Retrieves the weak context reference
         *
         * @return the context object for this instance
         */
        get() {
            var context: Context? = null
            if (weakContext != null) {
                context = weakContext.get()
            }

            return context
        }

    companion object {
        /**
         * Volatile boolean for double checked synchronize block
         */
        @Volatile
        private var isPersistenceLoaded = false

        /**
         * Synchronization LOCK for setting static context
         */
        private val LOCK = Any()

        private const val AI_SDK_DIRECTORY = "/com.architect.kmpappinsights"

        private const val HIGH_PRIO_DIRECTORY = "/highpriority/"

        private const val REGULAR_PRIO_DIRECTORY = "/regularpriority/"

        private const val MAX_FILE_COUNT = 50

        /**
         * The tag for logging
         */
        private const val TAG = "Persistence"

        /**
         * The singleton INSTANCE of this class
         */
        private var instance: Persistence? = null

        /**
         * Initialize the INSTANCE of persistence
         *
         * @param context the app context for the INSTANCE
         */
        fun initialize(context: Context?) {
            // note: isPersistenceLoaded must be volatile for the double-checked LOCK to work
            if (!isPersistenceLoaded) {
                synchronized(LOCK) {
                    if (!isPersistenceLoaded) {
                        isPersistenceLoaded = true
                        instance = Persistence(context)
                    }
                }
            }
        }

        /**
         * @return the INSTANCE of persistence or null if not yet initialized
         */
        fun getInstance(): Persistence? {
            if (instance == null) {
                InternalLogging.error(TAG, "getSharedInstance was called before initialization")
            }

            return instance
        }
    }
}
