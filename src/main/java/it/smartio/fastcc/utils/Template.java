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
import java.util.List;
import java.util.Stack;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Generates boiler-plate files from templates.
 */
public class Template {

  // COMMAND (ARGUMENT (,ARGUMENT)* )?
  private static final Pattern COMMAND   = Pattern.compile("^@(\\w+)\\s*(?:\\(([^\\)]+)\\))?");
  private static final Pattern PARAMETER = Pattern.compile("\\{\\{([^\\}\\:]+)(?:\\:([^\\}]*))?\\}\\}");


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
   * Use the template.
   *
   * @param writer
   */
  public void render(PrintWriter writer) throws IOException {
    List<String> lines = new ArrayList<>();
    InputStream stream = new ByteArrayInputStream(this.bytes);
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
      String line = null;
      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }
    }
    renderLines(writer, lines, environment, null, null, null);
  }

  /**
   * Use the template.
   *
   * @param writer
   * @param lines
   * @param env
   */
  private void renderLines(PrintWriter writer, List<String> lines, Environment env, Object item, String global,
      String local) {
    int index = 0;
    Stack<Boolean> conditions = new Stack<>();
    while (index < lines.size()) {
      String line = lines.get(index++);

      Matcher matcher = COMMAND.matcher(line);
      if (matcher.matches()) {
        switch (matcher.group(1)) {
          case "if":
          case "elif":
            boolean condition = validate(matcher.group(2), env);
            conditions.push(condition && (conditions.isEmpty() || conditions.peek()));
            break;

          case "else":
            condition = !conditions.pop();
            conditions.push(condition && (conditions.isEmpty() || conditions.peek()));
            break;

          case "fi":
            conditions.pop();
            break;

          case "forEach":
            List<String> subList = new ArrayList<>();
            String subLine = lines.get(index++);
            while (!subLine.startsWith("@end")) {
              subList.add(subLine);
              subLine = lines.get(index++);
            }

            List<String> args =
                Arrays.asList(matcher.group(2).split(":")).stream().map(k -> k.trim()).collect(Collectors.toList());

            Object instance = environment.get(args.get(1));
            Iterable<?> iterable = (instance instanceof Integer)
                ? IntStream.range(0, (Integer) instance).boxed().collect(Collectors.toList())
                : (Iterable<?>) instance;
            Environment e = new TemplateEnvironment(env);
            for (Object value : iterable) {
              e.set(args.get(0), value);
              renderLines(writer, subList, e, value, args.get(1), args.get(0));
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

          String param = matcher.group(1);
          if (param.startsWith(local + ".")) {
            param = global + "." + param.substring(local.length() + 1);
          }

          String option = matcher.group(2);
          Object instance = env.get(param);
          if (option != null) {
            boolean validate = validate(param, env);
            writer.print(validate ? instance : option);
          } else if (instance instanceof Supplier) {
            Supplier<Object> func = (Supplier<Object>) instance;
            writer.print(func.get());
          } else if (instance instanceof Function) {
            Function<Object, Object> func = (Function<Object, Object>) instance;
            writer.print(func.apply(item));
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
