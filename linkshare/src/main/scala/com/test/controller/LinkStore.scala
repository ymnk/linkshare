package com.test.controller;

import scala.collection.mutable.{HashMap, HashSet}
import scala.actors.Actor
import scala.actors.Actor.{loop, react}
import net.liftweb.util.Helpers.randomString

// Messages
case class AddListener(listener: Actor)
case class RemoveListener(listener: Actor)
case class UpdateLinks(topLinks: List[Link])
case class AddLink(url: String, title: String)
case class VoteUp(linkId: String)
case class VoteDown(linkId: String)

// Data Structures
case class Link(id: String, entry: LinkEntry)
case class LinkEntry(url: String, title: String, var score: Int)

object LinkStore extends Actor {
  val linkMap = new HashMap[String, LinkEntry]
  val listeners = new HashSet[Actor]
  var topLinks: List[Link] = Nil

  def notifyListeners = {
    topLinks = linkMap.toList.map(p => Link(p._1, p._2)).
      sort(_.entry.score > _.entry.score).take(20)

    listeners.foreach(_ ! UpdateLinks(topLinks))
  }

  def act = {
    loop {
      react {
        case AddListener(listener: Actor) =>
          listeners.incl(listener)
          reply(UpdateLinks(topLinks))
        case RemoveListener(listener: Actor) =>
          listeners.excl(listener)
        case AddLink(url: String, title: String) =>
          linkMap += randomString(12) -> LinkEntry(url, title, 1)        
          notifyListeners
        case VoteUp(linkId: String) => try {
          linkMap(linkId).score += 1
          notifyListeners
        }
        case VoteDown(linkId: String) => try {
          linkMap(linkId).score -= 1
          notifyListeners
        }
      }
    }
  }

  start
}
