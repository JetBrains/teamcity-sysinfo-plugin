

package org.jetbrains.teamcity.sysinfo.agent;

import java.util.Map;
import org.jetbrains.annotations.NotNull;

public interface SysInfoProvider {
  @NotNull
  Map<String, String> getSysInfo();
}