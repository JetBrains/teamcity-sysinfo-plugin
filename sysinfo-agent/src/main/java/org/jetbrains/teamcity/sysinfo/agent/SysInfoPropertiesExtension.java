/*
 * Copyright 2000-2021 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.teamcity.sysinfo.agent;

import com.intellij.openapi.diagnostic.Logger;
import java.util.*;
import java.util.regex.Pattern;
import jetbrains.buildServer.agent.AgentLifeCycleAdapter;
import jetbrains.buildServer.agent.AgentLifeCycleListener;
import jetbrains.buildServer.agent.BuildAgent;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import jetbrains.buildServer.agent.impl.OSTypeDetector;
import jetbrains.buildServer.util.EventDispatcher;
import jetbrains.buildServer.util.OSType;
import org.jetbrains.annotations.NotNull;

public class SysInfoPropertiesExtension extends AgentLifeCycleAdapter {
  private static final Logger LOG = Logger.getInstance(SysInfoPropertiesExtension.class.getName());
  private final OSTypeDetector myOsTypeDetector;
  private final SysInfoProvider myWinSysInfoProvider;

  public SysInfoPropertiesExtension(
    @NotNull final EventDispatcher<AgentLifeCycleListener> events,
    @NotNull final OSTypeDetector osTypeDetector,
    @NotNull final SysInfoProvider winSysInfoProvider) {
    myOsTypeDetector = osTypeDetector;
    myWinSysInfoProvider = winSysInfoProvider;
    events.addListener(this);
  }

  @Override
  public void agentInitialized(@NotNull final BuildAgent agent) {
    super.agentInitialized(agent);
    try {
      final OSType osType = myOsTypeDetector.detect();
      Map<String, String> props = Collections.emptyMap();
      switch (osType) {
        case WINDOWS:
          props = myWinSysInfoProvider.getSysInfo();
          break;
      }

      final BuildAgentConfiguration config = agent.getConfiguration();
      for (Map.Entry<String, String> item : props.entrySet()) {
        if (config.getConfigurationParameters().containsKey(item.getKey())) {
          config.getConfigurationParameters().remove(item.getKey());
        }

        config.addConfigurationParameter(item.getKey(), item.getValue());
      }
    }
    catch (Exception e)
    {
      LOG.error(e);
    }
  }
}

