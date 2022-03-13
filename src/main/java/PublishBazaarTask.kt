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
        builtArtifacts.elements.single().let { buildArtifact ->
            val isSuccessful = ApiService.requestUploadingRelease(bazaarKey)
            if(!isSuccessful){
                println("Failed to create release")
            }
            println("Release is created")
            ApiService.startUploadingOnBazaar(bazaarKey, buildArtifact.outputFile)
            println("Uploaded on Bazaar Successfully")
        }
    }
}