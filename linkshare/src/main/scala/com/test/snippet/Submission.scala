package com.test.snippet;

import com.test.controller.{LinkStore, AddLink}
import net.liftweb.http.SHtml.{text, submit}

class Submission {
  def form = {
    var url = ""
    var title = ""

    <span>
      { text("URL", u => url = u) }
      { text("Title", t => title = t) }
      { submit("Submit", () => LinkStore ! AddLink(url, title)) }
    </span>
  }
}
