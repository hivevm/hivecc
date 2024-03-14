/*
 * Copyright (c) 2006, Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. * Neither the name of the Sun Microsystems, Inc. nor
 * the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
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

package it.smartio.fastcc.parser;

import java.util.Collections;
import java.util.List;

/**
 * Describes expansions of the form "try {...} ...".
 */

public class TryBlock extends Expansion {

  /**
   * The expansion contained within the try block.
   */
  private Expansion               expansion;

  /**
   * The types of each catch block. Each list entry is itself a list which in turn contains tokens
   * as entries.
   */
  private final List<List<Token>> types;

  /**
   * The exception identifiers of each catch block. Each list entry is a token.
   */
  private final List<Token>       ids;

  /**
   * The block part of each catch block. Each list entry is itself a list which in turn contains
   * tokens as entries.
   */
  private final List<List<Token>> catchblks;

  /**
   * The block part of the finally block. Each list entry is a token. If there is no finally block,
   * this is null.
   */
  private final List<Token>       finallyblk;

  /**
   * Constructs an instance of {@link TryBlock}.
   *
   * @param ids
   * @param types
   * @param catchblks
   * @param finallyblk
   */
  public TryBlock(List<Token> ids, List<List<Token>> types, List<List<Token>> catchblks, List<Token> finallyblk) {
    this.ids = ids == null ? Collections.emptyList() : ids;
    this.types = types == null ? Collections.emptyList() : types;
    this.catchblks = catchblks == null ? Collections.emptyList() : catchblks;
    this.finallyblk = finallyblk == null ? Collections.emptyList() : finallyblk;
  }

  /**
   * Gets the {@link #expansion}.
   */
  public final Expansion getExpansion() {
    return expansion;
  }

  /**
   * Gets the {@link #ids}.
   */
  public final List<Token> getIds() {
    return ids;
  }

  /**
   * Gets the {@link #types}.
   */
  public final List<List<Token>> getTypes() {
    return types;
  }

  /**
   * Gets the {@link #catchblks}.
   */
  public final List<List<Token>> getCatchBlocks() {
    return catchblks;
  }

  /**
   * Gets the {@link #finallyblk}.
   */
  public final List<Token> getFinallyBlock() {
    return finallyblk;
  }

  /**
   * Sets the {@link #expansion}.
   */
  public final void setExpansion(Expansion expansion) {
    this.expansion = expansion;
  }
}
