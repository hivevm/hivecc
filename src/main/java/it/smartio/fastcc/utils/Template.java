/*
 * Copyright (c) 2008, Paul Cager. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package it.smartio.fastcc.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Generates boiler-plate files from templates.
 */
public class Template {

  private static final Pattern PARAMETERS =
      Pattern.compile("\\{\\{([^\\{\\}\\?\\:]+)(?:\\?([^:]*):([^\\}]*))?(?:\\:\\-([^\\}]+))?\\}\\}");

  // COMMAND (ARGUMENT (,ARGUMENT)* )?
  private static final Pattern COMMAND   = Pattern.compile("^@([A-Z]+)\\s*(?:\\(([^\\)]+)\\))?");
  private static final Pattern PARAMETER = Pattern.compile("\\{\\{([^\\}]+)\\}\\}");


  private final byte[]      bytes;
  private final Environment environment;

  /**
   * @param bytes
   * @param environment
   */
  private Template(byte[] bytes, Environment environment) {
    this.bytes = bytes;
    this.environment = environment;
  }

  /**
   * Validates the condition against the properties.
   *
   * @param condition
   */
  private final boolean validate(String condition, Environment environment) {
    if (condition.startsWith("!")) { // negative condition
      return !validate(condition.substring(1), environment);
    }

    if (!environment.isSet(condition)) {
      return false;
    }

    Object value = environment.get(condition);
    if (value == null) {
      return false;
    }
    if ((value instanceof String) && ((String) value).isEmpty()) {
      return false;
    }
    if ((value instanceof Number) && (((Number) value).intValue() == 0)) {
      return false;
    }

    return (!(value instanceof Boolean) || ((Boolean) value));
  }

  /**
   * Gets the iterable for the option
   *
   * @param option
   * @param environment
   */
  private final Iterable<Object> getItems(String option, Environment environment) {
    if (!environment.isSet(option)) {
      return Collections.emptySet();
    }

    Object value = environment.get(option);
    List<Object> list = new ArrayList<>();
    if (value instanceof Integer) {
      for (int i = 0; i < ((Integer) value); i++) {
        list.add(i);
      }
    } else if (value instanceof Iterable) {
      for (Object v : (Iterable<?>) value) {
        list.add(v);
      }
    }
    return list;
  }


  /**
   * Use the template.
   *
   * @param writer
   */
  public void write(PrintWriter writer) throws IOException {
    List<String> lines = new ArrayList<>();
    InputStream stream = new ByteArrayInputStream(this.bytes);
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
      String line = null;
      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }
    }
    write(writer, lines, environment);
  }

  /**
   * Use the template.
   *
   * @param writer
   * @param lines
   * @param env
   */
  private void write(PrintWriter writer, List<String> lines, Environment env) {
    int index = 0;
    Stack<Boolean> conditions = new Stack<>();
    while (index < lines.size()) {
      String line = lines.get(index++);
      String cmd = line.toLowerCase();

      Matcher matcher = COMMAND.matcher(line);
      if (matcher.matches()) {
        switch (matcher.group(1).toUpperCase()) {
          case "FOREACH":
            List<String> args =
                Arrays.asList(matcher.group(2).split(",")).stream().map(k -> k.trim()).collect(Collectors.toList());

            List<String> subList = new ArrayList<>();
            String subLine = lines.get(index++);
            while (!subLine.toLowerCase().startsWith("@end")) {
              subList.add(subLine);
              subLine = lines.get(index++);
            }

            TemplateFunction<?> function = (TemplateFunction<?>) environment.get(args.get(0));

            Environment e = new TemplateEnvironment(env);
            for (Object value : function.values) {
              for (int i = 1; i < args.size(); i++) {
                e.set(args.get(i), function.functions.get(i - 1));
              }
              forEach(writer, subList, e, value);
            }
            break;

          default:
            break;
        }
      } else if (cmd.startsWith("@if ")) {
        boolean condition = validate(line.substring(4).trim(), env);
        conditions.push(condition && (conditions.isEmpty() || conditions.peek()));
      } else if (cmd.startsWith("@elfi")) {
        boolean condition = validate(line.substring(4).trim(), env);
        conditions.push(condition && (conditions.isEmpty() || conditions.peek()));
      } else if (cmd.startsWith("@else")) {
        boolean condition = !conditions.pop();
        conditions.push(condition && (conditions.isEmpty() || conditions.peek()));
      } else if (cmd.startsWith("@fi")) {
        conditions.pop();
      } else if (cmd.startsWith("@foreach ")) {
        List<String> subList = new ArrayList<>();
        String subLine = lines.get(index++);
        while (!subLine.toLowerCase().startsWith("@end")) {
          subList.add(subLine);
          subLine = lines.get(index++);
        }

        for (Object value : getItems(line.substring(9).trim(), env)) {
          env.set("$", value);
          write(writer, subList, env);
        }
      } else if (conditions.isEmpty() || conditions.peek()) {
        int offset = 0;
        matcher = Template.PARAMETERS.matcher(line);
        while (matcher.find()) {
          writer.print(line.substring(offset, matcher.start()));
          if (matcher.group(2) != null) {
            boolean validate = validate(matcher.group(1), env);
            writer.print(matcher.group(validate ? 2 : 3));
          } else if (matcher.group(4) != null) {
            boolean validate = validate(matcher.group(1), env);
            writer.print(validate ? env.get(matcher.group(1)) : matcher.group(4));
          } else if (matcher.group(1).endsWith("()")) {
            String name = matcher.group(1);
            String funcName = name.substring(0, name.length() - 2);
            Object instance = env.get(funcName);
            if (instance instanceof Function) {
              Function<Object, String> func = (Function<Object, String>) instance;
              writer.print(func.apply(env.get("$")));
            } else if (instance instanceof BiConsumer) {
              BiConsumer<PrintWriter, Object> func = (BiConsumer<PrintWriter, Object>) instance;
              func.accept(writer, env.get("$"));
            }
          } else {
            writer.print(env.get(matcher.group(1)));
          }
          offset = matcher.end();
        }
        writer.print(line.substring(offset));
        writer.println();
      }
    }
  }


  /**
   * Use the template.
   *
   * @param writer
   * @param lines
   * @param env
   */
  private void forEach(PrintWriter writer, List<String> lines, Environment env, Object item) {
    int index = 0;
    Stack<Boolean> conditions = new Stack<>();
    while (index < lines.size()) {
      String line = lines.get(index++);

      Matcher matcher = COMMAND.matcher(line);
      if (matcher.matches()) {
        switch (matcher.group(1).toUpperCase()) {
          case "FOREACH":
            List<String> args =
                Arrays.asList(matcher.group(2).split(",")).stream().map(k -> k.trim()).collect(Collectors.toList());

            List<String> subList = new ArrayList<>();
            String subLine = lines.get(index++);
            while (!subLine.toLowerCase().startsWith("@end")) {
              subList.add(subLine);
              subLine = lines.get(index++);
            }

            TemplateFunction<?> func = (TemplateFunction<?>) environment.get(args.get(0));

            Environment e = new TemplateEnvironment(env);
            for (Object value : func.values) {
              for (int i = 2; i < args.size(); i++) {
                e.set(args.get(i), func.functions.get(i - 2));
              }
              forEach(writer, subList, e, value);
            }
            break;

          default:
            break;
        }
      } else if (conditions.isEmpty() || conditions.peek()) {
        int offset = 0;
        matcher = Template.PARAMETER.matcher(line);
        while (matcher.find()) {
          writer.print(line.substring(offset, matcher.start()));
          Object instance = env.get(matcher.group(1));
          if (instance instanceof Function) {
            Function<Object, String> func = (Function<Object, String>) instance;
            writer.print(func.apply(item));
          } else if (instance instanceof BiConsumer) {
            BiConsumer<PrintWriter, Object> func = (BiConsumer<PrintWriter, Object>) instance;
            func.accept(writer, item);
          } else {
            writer.print(instance);
          }
          offset = matcher.end();
        }
        writer.print(line.substring(offset));
        writer.println();
      }
    }
  }

  /**
   * Creates a new {@link Template}.
   *
   * @param template
   * @param environment
   */
  public static Template of(String template, Environment environment) throws IOException {
    InputStream stream = Template.class.getResourceAsStream(template);
    if (stream == null) {
      throw new IOException("Invalid template name: " + template);
    }
    return new Template(stream.readAllBytes(), environment);
  }
}
