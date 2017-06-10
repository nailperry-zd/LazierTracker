package com.codelessda.plugin

import com.android.build.gradle.BaseExtension
import com.codelessda.plugin.utils.DataHelper
import com.codelessda.plugin.utils.Log
import org.gradle.api.Plugin
import org.gradle.api.Project

class InjectPluginImpl implements Plugin<Project> {
    @Override
    void apply(Project project) {
        println ":applied Hubble"
        project.extensions.create('codelessdaConfig', InjectPluginParams)
        registerTransform(project)
        initDir(project);
        project.afterEvaluate {
            Log.setQuiet(project.codelessdaConfig.keepQuiet);
            Log.setShowHelp(project.codelessdaConfig.showHelp);
            Log.logHelp();
            if (project.codelessdaConfig.watchTimeConsume) {
                Log.info "watchTimeConsume enabled"
                project.gradle.addListener(new TimeListener())
            } else {
                Log.info "watchTimeConsume disabled"
            }
        }
    }

    def static registerTransform(Project project) {
//        def isApp = project.plugins.hasPlugin("com.android.application")
        BaseExtension android = project.extensions.getByType(BaseExtension)
        InjectTransform transform = new InjectTransform(project)
        android.registerTransform(transform)
    }

    static void initDir(Project project) {
        File pluginTmpDir = new File(project.buildDir, 'Hubble')
        if (!pluginTmpDir.exists()) {
            pluginTmpDir.mkdir()
        }
        DataHelper.ext.pluginTmpDir = pluginTmpDir
    }
}
