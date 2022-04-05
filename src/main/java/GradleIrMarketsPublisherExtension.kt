import org.gradle.api.file.RegularFile

abstract class GradleIrMarketsPublisherExtension {
    var cafeBazaarApiKey: String? = null
    var changeLogFaTextFile: RegularFile? = null
    var changeLogEnTextFile: RegularFile? = null
    var autoPublish: Boolean = false
    var stagedRolloutPercentage: Int = 100
    var developerNoteTextFile: RegularFile? = null
}