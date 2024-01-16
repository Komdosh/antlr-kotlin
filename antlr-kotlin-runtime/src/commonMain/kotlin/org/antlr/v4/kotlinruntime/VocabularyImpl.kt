// Copyright 2017-present Strumenta and contributors, licensed under Apache 2.0.
// Copyright 2024-present Strumenta and contributors, licensed under BSD 3-Clause.
package org.antlr.v4.kotlinruntime

import kotlin.math.max

/**
 * This class provides a default implementation of the [Vocabulary]
 * interface.
 *
 * @param literalNames The literal names assigned to tokens, or `null`
 *   if no literal names are assigned
 * @param symbolicNames The symbolic names assigned to tokens, or
 *   `null` if no symbolic names are assigned
 * @param displayNames The display names assigned to tokens, or `null`
 *   to use the values in [literalNames] and [symbolicNames] as
 *   the source of display names, as described in [getDisplayName]
 *
 * @author Sam Harwell
 */
@Suppress("MemberVisibilityCanBePrivate")
public class VocabularyImpl(
  literalNames: Array<String?>?,
  symbolicNames: Array<String?>?,
  displayNames: Array<String?>? = null,
) : Vocabulary {
  public companion object {
    private val EMPTY_NAMES = arrayOfNulls<String>(0)

    /**
     * An empty [Vocabulary] instance.
     *
     * No literal or symbol names are assigned to token types, so
     * [getDisplayName] returns the numeric value for all tokens
     * except [Token.EOF].
     */
    public val EMPTY_VOCABULARY: Vocabulary = VocabularyImpl(EMPTY_NAMES, EMPTY_NAMES, EMPTY_NAMES)

    /**
     * Returns a [VocabularyImpl] instance from the specified set of token
     * names. This method acts as a compatibility layer for the single
     * [tokenNames] array generated by previous releases of ANTLR.
     *
     * The resulting vocabulary instance returns `null` for
     * [getLiteralName] and [getSymbolicName], and the
     * value from [tokenNames] for the display names.
     *
     * @param tokenNames The token names, or `null` if no token names are available
     * @return A [Vocabulary] instance which uses [tokenNames] for the display names of tokens
     */
    public fun fromTokenNames(tokenNames: Array<String>?): Vocabulary {
      if (tokenNames.isNullOrEmpty()) {
        return EMPTY_VOCABULARY
      }

      val literalNames = tokenNames.copyOf(tokenNames.size)
      val symbolicNames = tokenNames.copyOf(tokenNames.size)

      for (i in tokenNames.indices) {
        val tokenName = tokenNames[i]

        if (tokenName.isNotEmpty()) {
          val firstChar = tokenName[0]

          if (firstChar == '\'') {
            symbolicNames[i] = null
            continue
          } else if (firstChar.isUpperCase()) {
            literalNames[i] = null
            continue
          }
        }

        // Wasn't a literal or symbolic name
        literalNames[i] = null
        symbolicNames[i] = null
      }

      @Suppress("UNCHECKED_CAST")
      return VocabularyImpl(literalNames, symbolicNames, tokenNames as Array<String?>)
    }
  }

  public val literalNames: Array<String?> = literalNames ?: EMPTY_NAMES
  public val symbolicNames: Array<String?> = symbolicNames ?: EMPTY_NAMES
  public val displayNames: Array<String?> = displayNames ?: EMPTY_NAMES

  // See note here on -1 part: https://github.com/antlr/antlr4/pull/1146
  override val maxTokenType: Int =
    max(this.displayNames.size, max(this.literalNames.size, this.symbolicNames.size)) - 1

  override fun getLiteralName(tokenType: Int): String? =
    if (tokenType in literalNames.indices) {
      literalNames[tokenType]
    } else {
      null
    }

  override fun getSymbolicName(tokenType: Int): String? {
    if (tokenType in symbolicNames.indices) {
      return symbolicNames[tokenType]
    }

    return if (tokenType == Token.EOF) "EOF" else null
  }

  override fun getDisplayName(tokenType: Int): String {
    if (tokenType in displayNames.indices) {
      val displayName = displayNames[tokenType]

      if (displayName != null) {
        return displayName
      }
    }

    val literalName = getLiteralName(tokenType)

    if (literalName != null) {
      return literalName
    }

    val symbolicName = getSymbolicName(tokenType)
    return symbolicName ?: tokenType.toString()
  }
}
