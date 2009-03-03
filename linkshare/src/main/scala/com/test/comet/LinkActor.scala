package com.test.comet;

import scala.xml.{NodeSeq, Text}
import net.liftweb.http.CometActor
import net.liftweb.http.SHtml.a
import net.liftweb.http.js.JsCmds.Noop
import net.liftweb.util.Full
import com.test.controller._

class LinkActor extends CometActor{
  var topLinks: List[Link] = Nil

  override def defaultPrefix = Full("links")

  def render = {
    def linkView(link: Link): NodeSeq = {
      <li>
        <a href={link.entry.url}>{link.entry.title}</a>
        [{link.entry.score} {if (link.entry.score == 1) "vote" else "votes"}]
        {a(() => {LinkStore ! VoteUp(link.id); Noop}, Text("[up]"))}
        {a(() => {LinkStore ! VoteDown(link.id); Noop}, Text("[down]"))}
      </li>
    }
    
    bind("view" -> <ol>{topLinks.flatMap(linkView _)}</ol>)
  }

  override def localSetup {
    LinkStore !? AddListener(this) match {
      case UpdateLinks(links) => topLinks = links
    }
  }

  override def localShutdown {
    LinkStore ! RemoveListener(this)
  }

  override def lowPriority : PartialFunction[Any, Unit] = {
    case UpdateLinks(newLinks) => topLinks = newLinks; reRender(false)
  }
}
