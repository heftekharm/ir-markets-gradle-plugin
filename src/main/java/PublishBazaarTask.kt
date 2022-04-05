import com.android.build.api.variant.BuiltArtifactsLoader
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.lang.RuntimeException

abstract class PublishBazaarTask() : DefaultTask() {

    @get:Input
    abstract val bazaarKey: Property<String>

    @get:InputFiles
    abstract val apkFolder: DirectoryProperty

    @get:Internal
    abstract val builtArtifactsLoader: Property<BuiltArtifactsLoader>

    @TaskAction
    fun publish() {
        val bazaarKey=this.bazaarKey.get()
        val builtArtifacts = builtArtifactsLoader.get().load(apkFolder.get()) ?: throw RuntimeException()

        val cafeBazaarApiService=CafeBazaarApiService(bazaarKey)

        builtArtifacts.elements.single().let { buildArtifact ->
            val isReleaseCreated = cafeBazaarApiService.requestUploadingRelease()
            if(!isReleaseCreated){
                println("Failed to create release")
                return
            }
            println("Release is created")
            val isUploaded=cafeBazaarApiService.startUploadingOnBazaar(buildArtifact.outputFile)
            if(!isUploaded){
                println("Failed to upload app")
                return
            }
            println("Uploaded on Bazaar Successfully")
        }
    }
}