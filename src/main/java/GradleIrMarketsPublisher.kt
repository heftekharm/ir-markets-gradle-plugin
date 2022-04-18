import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ApplicationVariant
import org.gradle.api.Plugin
import org.gradle.api.Project

class GradleIrMarketsPublisher : Plugin<Project> {
    override fun apply(project: Project) {
        val gradleIrMarketsPublisherExtension=project.extensions.create(Constants.EXTENSION_NAME, GradleIrMarketsPublisherExtension::class.java)
        val androidCompExt = project.extensions.getByType(AndroidComponentsExtension::class.java)

        androidCompExt.apply {
            onVariants(selector().withBuildType("release")) { variant ->
                (variant as? ApplicationVariant)?.let {
                        onReleaseVariant(project,variant,gradleIrMarketsPublisherExtension)
                }
            }
        }
    }


    private fun onReleaseVariant(project: Project, variant: ApplicationVariant, gradleIrMarketsPublisherExtension:GradleIrMarketsPublisherExtension) {

        gradleIrMarketsPublisherExtension.cafeBazaarApiKey?.let {
            project.tasks.register("${Constants.TASKS_NAME_PREFIX}${variant.name}OnCafeBazaar", PublishBazaarTask::class.java) { task ->
                task.apply {
                    this.bazaarKey.set(bazaarKey)
                    this.apkFolder.set(variant.artifacts.get(SingleArtifact.APK))
                    this.builtArtifactsLoader.set(variant.artifacts.getBuiltArtifactsLoader())
                    group=Constants.TASKS_GROUP_NAME
                }
            }
        }


    }
}