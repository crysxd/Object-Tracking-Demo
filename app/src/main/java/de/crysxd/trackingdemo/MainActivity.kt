package de.crysxd.trackingdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.crysxd.cameraXTracker.CameraFragment
import de.crysxd.cameraXTracker.ar.BoundingBoxArOverlay
import de.crysxd.cameraXTracker.ar.PathInterpolator
import de.crysxd.cameraXTracker.ar.PositionTranslator
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var imageAnalyzer: ClassifySneakerImageAnalyzer

    private val camera
        get() = supportFragmentManager.findFragmentById(R.id.cameraFragment) as CameraFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup logging
        if (Timber.treeCount() == 0) {
            Timber.plant(Timber.DebugTree())
        }

        val boundingBoxArOverlay = BoundingBoxArOverlay(this, BuildConfig.DEBUG)
        imageAnalyzer = ViewModelProviders.of(this).get(ClassifySneakerImageAnalyzer::class.java)

        camera.imageAnalyzer = imageAnalyzer
        camera.arOverlayView.observe(camera, Observer {
            it.doOnLayout { view ->
                imageAnalyzer.arObjectTracker
                    .pipe(PositionTranslator(view.width, view.height))
                    .pipe(PathInterpolator())
                    .addTrackingListener(boundingBoxArOverlay)
            }

            it.add(boundingBoxArOverlay)
        })
    }
}
