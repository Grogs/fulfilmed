package me.gregd.cineworld.domain

case class Film(id:String, title:String, poster_url: String) {
  val textToStrip = List(" - Unlimited Screening", " (English subtitles)", ": Movies For Juniors", " - Movies For Juniors", "Take 2 - ", "3D - ", "2D - ", "Autism Friendly Screening: ", " for Juniors", " (English dubbed version)", " (Japanese with English subtitles)", "(Punjabi)", "(Hindi)", " Unlimited Screening")
  val cleanTitle = textToStrip.foldLeft(title)((res, r) => res.replace(r,""))
}
