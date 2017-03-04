package com.netease.mobidroid.plugin

import com.android.build.gradle.BaseExtension
import com.netease.mobidroid.plugin.utils.DataHelper
import com.netease.mobidroid.plugin.utils.Log
import org.gradle.api.Plugin
import org.gradle.api.Project

class HubblePluginImpl implements Plugin<Project> {
    @Override
    void apply(Project project) {
        println ":applied Hubble"
        project.extensions.create('hubbleConfig', HubblePluginParams)
        registerTransform(project)
        initDir(project);
        project.afterEvaluate {
            Log.setQuiet(project.hubbleConfig.keepQuiet);
            Log.setShowHelp(project.hubbleConfig.showHelp);
            Log.logHelp();
            if (project.hubbleConfig.watchTimeConsume) {
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
