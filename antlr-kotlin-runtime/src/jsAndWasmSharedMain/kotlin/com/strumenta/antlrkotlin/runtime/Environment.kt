package com.strumenta.antlrkotlin.runtime

@Suppress("NOTHING_TO_INLINE")
internal actual inline fun platformGetEnv(name: String): String? {
  // TODO(Edoardo): add support for Node.js
  @Suppress("SpellCheckingInspection")
  System.out.println("getenv: not yet supported for Node.js and browser")
  return null
}
