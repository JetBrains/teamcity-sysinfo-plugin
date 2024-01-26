

package org.jetbrains.teamcity.sysinfo.agent;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.diagnostic.Logger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jetbrains.buildServer.ExecResult;
import org.jetbrains.annotations.NotNull;

public class WinSysInfoProvider implements SysInfoProvider {
  private static final Logger LOG = Logger.getInstance(SysInfoProvider.class.getName());
  private static final Pattern OurCsvPattern = Pattern.compile("\"([^\"]*)\"|(?<=,|^)([^,]*)(?:,|$)");
  private static final String[] OurPostfixes = { "(s)", "(es)" };

  @Override
  @NotNull
  public Map<String, String> getSysInfo() {
    Map<String, String> props = Collections.emptyMap();
    try {
      final GeneralCommandLine cmd = new GeneralCommandLine();
      cmd.setExePath("systeminfo");
      cmd.addParameter("/FO");
      cmd.addParameter("CSV");
      final jetbrains.buildServer.CommandLineExecutor executor = new jetbrains.buildServer.CommandLineExecutor(cmd);
      final ExecResult result = executor.runProcess(60);
      if(result != null && result.getExitCode() == 0) {
        final String[] lines = result.getOutLines();
        if(lines.length != 2)
        {
          return props;
        }

        final List<String> keys = parseCsvLine(lines[0]);
        final List<String> values = parseCsvLine(lines[1]);
        if(keys.size() != values.size())
        {
          return props;
        }

        props = new HashMap<String, String>();
        for(int i = 0; i < keys.size(); i++) {
          final String key = "teamcity.agent.os." + normalizeKey(keys.get(i));
          final String val = normalizeValue(values.get(i));
          props.put(key, val);
        }

        // Handle mul properties
        final HashMap<String, String> mulKeys = new HashMap<String, String>();
        for (Map.Entry<String, String> item: props.entrySet()) {
          for(String postfix: OurPostfixes) {
            if (item.getKey().endsWith(postfix)) {
              mulKeys.put(item.getKey(), item.getKey().substring(0, item.getKey().length() - postfix.length()));
            }
          }
        }

        for(Map.Entry<String, String> item: mulKeys.entrySet())
        {
          final String newKeyPrefix = item.getValue();
          final String val = props.get(item.getKey());
          int pos = 0;
          int index = 1;
          while(pos >=0 && pos < val.length()) {
            final String indexStr = "[" + String.format("%02d", index) + "]: ";
            index++;
            pos = val.indexOf(indexStr, pos);
            if(pos >= 0) {
              pos += indexStr.length();
              int end = val.indexOf(",", pos);
              if (end == -1) {
                end = val.length();
              }

              final String newKey = newKeyPrefix + "." + normalizeKey(val.substring(pos, end));
              props.put(newKey, "");
            }
          }

          props.remove(item.getKey());
        }
      }
    }
    catch (ExecutionException e) {
      LOG.error(e);
    }

    return props;
  }

  @NotNull
  private String normalizeKey(@NotNull final String key) {
    return removeBadChars(key)
      .replace(" ", ".")
      .replace("~", "")
      .replace(":", "").toLowerCase();
  }

  @NotNull
  private String normalizeValue(@NotNull final String val) {

    final String str = removeBadChars(val);
    if(str.endsWith(" MB"))
    {
      return str.substring(0, str.length() - 3);
    }

    return str;
  }

  private static String removeBadChars(@NotNull  String str) {
    return str.replace(Character.toString((char)65533), "");
  }

  @NotNull
  private List<String> parseCsvLine(@NotNull final String csvLine)
  {
    List<String> result = new ArrayList<String>();
    final Matcher matcher = OurCsvPattern.matcher(csvLine);

    String match;
    while (matcher.find()) {
      match = matcher.group(1);
      if (match!=null) {
        result.add(match);
      }
      else {
        result.add(matcher.group(2));
      }
    }

    return result;
  }
}