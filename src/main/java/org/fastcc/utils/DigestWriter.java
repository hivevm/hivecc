/*
 * Copyright (c) 2001-2021 Territorium Online Srl / TOL GmbH. All Rights Reserved.
 *
 * This file contains Original Code and/or Modifications of Original Code as defined in and that are
 * subject to the Territorium Online License Version 1.0. You may not use this file except in
 * compliance with the License. Please obtain a copy of the License at http://www.tol.info/license/
 * and read it before using this file.
 *
 * The Original Code and all software distributed under the License are distributed on an 'AS IS'
 * basis, WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, AND TERRITORIUM ONLINE HEREBY
 * DISCLAIMS ALL SUCH WARRANTIES, INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, QUIET ENJOYMENT OR NON-INFRINGEMENT. Please see the License for
 * the specific language governing rights and limitations under the License.
 */

package org.fastcc.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The {@link DigestWriter} class.
 */
public class DigestWriter extends PrintWriter {

  private static final MessageDigest DIGEST = DigestWriter.createMD5();


  private final OutputStream          stream;
  private final DigestOutputStream    digest;
  private final ByteArrayOutputStream bytes;
  private final DigestOptions         options;

  /**
   * Constructs an instance of {@link DigestWriter}.
   *
   * @param digest
   * @param stream
   * @param bytes
   */
  private DigestWriter(OutputStream stream, DigestOutputStream digest, ByteArrayOutputStream bytes, Version version,
      DigestOptions options) {
    super(digest);
    this.stream = stream;
    this.digest = digest;
    this.bytes = bytes;
    this.options = options;
    printf("// Generated by FastCC v.%s - Do not edit this line!\n\n", version.toString("0.0"));
  }

  /**
   * Get options as wrapper.
   */
  public Map<String, Object> options() {
    return this.options;
  }

  /**
   * Closes the stream and releases any system resources associated with it. Closing a previously
   * closed stream has no effect.
   */
  @Override
  public final void close() {
    super.close();
    String checksum = DigestWriter.toChecksum(this.digest.getMessageDigest().digest());
    try (PrintWriter writer = new PrintWriter(this.stream)) {
      this.stream.write(this.bytes.toByteArray());
      writer.printf("// FastCC Checksum=%s (Do not edit this line!)\n", checksum);
      if (this.options.hasConsumed()) {
        writer.printf("// FastCC Options: %s\n",
            this.options.consumed().filter(e -> !(e.getValue() instanceof Function))
                .map(e -> String.format("%s='%s'", e.getKey(), e.getValue())).collect(Collectors.joining(", ")));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates an instance of MD5 {@link MessageDigest}.
   */
  private static MessageDigest createMD5() {
    try {
      return MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Creates an MD5 based checksum of the bytes.
   *
   * @param bytes
   */
  private static String toChecksum(byte[] bytes) {
    StringBuilder builder = new StringBuilder();
    for (byte b : bytes) {
      builder.append(String.format("%02X", b));
    }
    return builder.toString();
  }

  /**
   * Constructs an instance of {@link DigestWriter}.
   *
   * @param stream
   * @param version
   * @param options
   */
  public static DigestWriter create(OutputStream stream, Version version, DigestOptions options) {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    DigestOutputStream digest = new DigestOutputStream(bytes, DigestWriter.DIGEST);
    return new DigestWriter(stream, digest, bytes, version, options);
  }

  /**
   * Constructs an instance of {@link DigestWriter}.
   *
   * @param file
   * @param version
   * @param options
   */
  public static DigestWriter create(File file, Version version, DigestOptions options) throws FileNotFoundException {
    return DigestWriter.create(new FileOutputStream(file), version, options);
  }
}
