import com.android.build.api.variant.BuiltArtifactsLoader
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.impl.client.HttpClients
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.lang.RuntimeException

private const val API_KEY_HEADER="CAFEBAZAAR-PISHKHAN-API-SECRET"
abstract class PublishBazaarTask() : DefaultTask() {

    @get:Input
    abstract val bazaarKey: Property<String>

    @get:InputFiles
    abstract val apkFolder: DirectoryProperty

    @get:Internal
    abstract val builtArtifactsLoader: Property<BuiltArtifactsLoader>

    @TaskAction
    fun publish() {
        val bazaarKeyValue=bazaarKey.get()
        println("bazaarApiKey is : $bazaarKeyValue")
        val builtArtifacts = builtArtifactsLoader.get().load(apkFolder.get()) ?: throw RuntimeException()
        builtArtifacts.elements.single().let { buildArtifact ->
            val apk = File(buildArtifact.outputFile)
            println("publishing ${apk.name} on Bazaar Market")
            val httpClient = HttpClients.createDefault()
            HttpPost("https://api.pishkhan.cafebazaar.ir/v1/apps/releases").apply {
                addHeader(API_KEY_HEADER, bazaarKeyValue)
            }.let { portReq ->
                httpClient.execute(portReq)
            }.takeIf {
                it.statusLine.statusCode == 201
            }?.let {
                HttpPost("https://api.pishkhan.cafebazaar.ir/v1/apps/releases/upload")
            }?.apply {
                addHeader(API_KEY_HEADER, bazaarKeyValue)
                val contentEntity = MultipartEntityBuilder.create()
                    .addBinaryBody("apk", apk)
                    .addTextBody("architecture", "all")
                    .build()
                entity = contentEntity
            }.let {
                httpClient.execute(it)
            }.let {
                println(it.statusLine)
            }
        }
        println("SUCCESS")
    }
}