## [<img src="http://jb.gg/badges/official.svg" height="20" align="center"/>](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub) sysinfo plugin for [<img src="https://cdn.worldvectorlogo.com/logos/teamcity.svg" height="20" align="center"/>](https://www.jetbrains.com/teamcity/)

This plugin provides an ability to extract Windows system information and to publish it to agents' configuration parameters during the agents' initialization.
Extracts detailed configuration information about a computer and its operating system, including operating system configuration, security information, product ID, and hardware properties, such as RAM, disk space, and network cards.
For an example, see the list of [configuration parameters](https://github.com/JetBrains/teamcity-sysinfo-plugin/blob/master/docs/configurationParametersSample.tsv).

## Install ##

To install the plugin, put the [sysinfo-agent.zip](https://teamcity.jetbrains.com/httpAuth/app/rest/builds/buildType:TeamCityPluginsByJetBrains_SysInfoTeamCityPlugin_Build,pinned:true,status:SUCCESS,branch:master,tags:deploy/artifacts/content/sysinfo-agent.zip) to the 'plugins' directory under the TeamCity data directory. Restart the server.

## Implemention ##

This project contains 3 modules: 'sysinfo-server', 'sysinfo-agent', and 'sysinfo-common'. They contain code for server and agent parts, and a common part, available to both (agent and server). When implementing components for the server and agent parts, remember to update spring context files under 'main/resources/META-INF'. See [TeamCity documentation](https://confluence.jetbrains.com/display/TCDL/Developing+Plugins+Using+Maven) for details on plugin development.

## Build <img src="https://teamcity.jetbrains.com/app/rest/builds/buildType:TeamCityPluginsByJetBrains_SysInfoTeamCityPlugin_Build,pinned:true,branch:master,tags:deploy/statusIcon"/> ##

Use the 'mvn package' command from the root project to build your plugin. The resulting 'sysinfo-agent.zip' package  will be placed in the 'target' directory. The build is configured on the [JetBrains TeamCity build server](https://teamcity.jetbrains.com/project.html?projectId=TeamCityPluginsByJetBrains_SysInfoTeamCityPlugin).

## License ##

JetBrains sysinfo plugin for TeamCity is under the [Apache License](https://github.com/JetBrains/teamcity-dottrace/blob/master/LICENSE).

## Contributors ##

- [Nikolay Pianikov](https://github.com/NikolayPianikov)

## Resources ##

- [Systeminfo tool](https://technet.microsoft.com/ru-ru/library/bb491007.aspx)
