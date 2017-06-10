////////////////////// - Help Content(UTF-8) - /////////////////////////////
//              Our plugin setup case is shown as below
//          You can copy-paste them entirely and de-comment them as an initial setup
//          You can turn Help Content output off by setting the showHelp flag false
//          hi, 这里是插件使用指南，你可以直接把整个内容复制到build.gradle中，然后解除注释作为初始设置
//          如果嫌烦可以在下面配置showHelp = false 来关闭这个帮助内容的输出
//
//codelessdaConfig {
//    //this will determine the name of this plugin transform, no practical use.
//    pluginName = 'myPluginTest'
//    //turn this on to make it print help content, default value is true
//    showHelp = true
//    //this flag will decide whether the log of the modifying process be printed or not, default value is false
//    keepQuiet = false
//    //this is a kit feature of the plugin, set it true to see the time consume of this build
//    watchTimeConsume = false
//
//    //this is the most important part, 3rd party JAR packages that want our plugin to inject;
//    //our plugin will inject package defined in 'AndroidManifest.xml' and 'butterknife.internal.butterknife.internal.DebouncingOnClickListener' by default.
//    //structure is like ['butterknife.internal','com.a.c'], type is HashSet<String>.
//    //You can also specify the name of the class;
//    //example: ['com.xxx.xxx.BaseFragment']
//    targetPackages = []
//}
//
// Our plugin is on the basis of the open source project HiBeaver(https://github.com/BryanSharp/hibeaver)
///////////////////// - Help Content END - /////////////////////////////

