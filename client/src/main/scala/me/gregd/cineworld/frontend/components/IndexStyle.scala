package me.gregd.cineworld.frontend.components

import scalacss.DevDefaults._
import scalacss.internal.Attr
//import scalacss.ValueT.Color
//import scalacss.ext.CssReset.webkitAppearance

object IndexStyle extends StyleSheet.Inline {
  import dsl._

  val webkitAppearance = Attr.real("-webkit-appearance")

//  def textShadow[X,Y,Blur](offsetX: Length[X], offsetY: Length[Y], blurRadius: Length[Blur], color: Color) =
//    Attrs.textShadow := List[{def value: scalacss.Value}](offsetX, offsetY, blurRadius, color).map(_.value).mkString(" ")
  val title = style(
    fontFamily :=! "HelveticaNeue, Helvetica Neue, Helvetica, Arial, Lucida Grande, sans-serif",
    letterSpacing(3.px),
    fontSize(82.px),
    color.white,
    lineHeight(95.px),
//    textShadow(0.px, 2.px, 4.px, Color("#225968")),
    paddingTop(100.px)
  )
  val blurb = style(
    fontFamily :=! "HelveticaNeue, Helvetica Neue, Helvetica, Arial, Lucida Grande, sans-serif",
    fontSize(24.px),
    color(Color("#f5f5f5")),
    lineHeight(29.px),
    paddingTop(40.px),
    paddingBottom(50.px)
  )
  val top = style(
    backgroundColor(Color("#690B0B")),
    backgroundImage := "url(\"data:image/svg+xml;utf8,<svg width='354px' height='253px' viewBox='0 0 354 253' version='1.1' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink'><title>Movie Reel</title><defs></defs><g stroke='none' stroke-width='1' fill='none' fill-rule='evenodd'><g id='Desktop' transform='translate(-309.000000, -386.000000)' stroke='#440000' stroke-width='3' fill='#FFFFFF'><path d='M479.075206,408.804381 C537.166927,377.633253 609.860713,397.96805 642.991773,455.35273 C676.772493,513.862653 656.631651,588.733193 598.005944,622.580761 C539.380237,656.428328 464.470027,636.435539 430.689307,577.925616 C407.823735,538.321284 409.663387,491.221014 431.420928,454.421002 C398.23208,467.284491 358.444135,481.858619 349.446702,482.330154 C334.856929,483.094771 304.62183,436.653114 330.727445,443.863841 C349.550653,449.063068 433.630348,423.481447 479.075206,408.804381 Z M576.890345,493.443109 C584.561664,506.730223 601.573146,511.270418 614.886553,503.583919 C628.199961,495.897419 632.773778,478.894946 625.102459,465.607832 C617.43114,452.320718 600.419658,447.780523 587.10625,455.467022 C573.792843,463.153522 569.219026,480.155995 576.890345,493.443109 Z M564.351731,583.023725 C572.02305,596.310839 589.034532,600.851034 602.34794,593.164535 C615.661347,585.478035 620.235164,568.475562 612.563845,555.188448 C604.892526,541.901334 587.881044,537.361139 574.567637,545.047638 C561.254229,552.734138 556.680412,569.736611 564.351731,583.023725 Z M492.220418,457.841394 C499.891737,471.128508 516.903219,475.668703 530.216627,467.982204 C543.530034,460.295704 548.103851,443.293231 540.432532,430.006117 C532.761213,416.719003 515.749731,412.178808 502.436324,419.865307 C489.122916,427.551807 484.549099,444.55428 492.220418,457.841394 Z M519.750629,527.073019 C524.326503,534.998666 534.473703,537.706853 542.415034,533.121923 C550.356365,528.536994 553.084607,518.395168 548.508732,510.469521 C543.932858,502.543874 533.785658,499.835687 525.844327,504.420617 C517.902996,509.005546 515.174755,519.147372 519.750629,527.073019 Z M435.123356,532.599076 C442.794675,545.88619 459.806157,550.426385 473.119564,542.739885 C486.432972,535.053386 491.006789,518.050913 483.33547,504.763799 C475.664151,491.476685 458.652669,486.93649 445.339261,494.622989 C432.025854,502.309489 427.452037,519.311962 435.123356,532.599076 Z M475.088002,601.956854 C482.759321,615.243968 499.770803,619.784163 513.084211,612.097663 C526.397618,604.411164 530.971435,587.408691 523.300116,574.121577 C515.628797,560.834463 498.617315,556.294268 485.303908,563.980767 C471.9905,571.667267 467.416683,588.66974 475.088002,601.956854 Z' id='Movie-Reel' transform='translate(511.009732, 516.639173) rotate(-8.000000) translate(-511.009732, -516.639173) '></path></g></g></svg>\")",
    backgroundRepeat := "no-repeat",
    backgroundPosition := "45% 175%",
    backgroundSize := "350px 400px",
    textAlign.center,
    paddingBottom(175.px)
  )
  val btn = style(
    fontSize(24.px),
    height(1.8.em),
    border(2.px, solid, white),
    borderRadius(0.2.em),
    color(Color("#690B0B")),
    width(490.px),
    backgroundColor(white),
    &.active(
      border(1.px, solid, white)
    )
  )
  val select = style(
    fontSize(24.px),
    height(1.8.em),
    border(2.px, solid, white),
    color.white,
    width(490.px),
    backgroundColor(Color("#690B0B")),
    &.active(
      border(1.px, solid, white)
    )
  )
  val description = style (
    fontFamily :=! "HelveticaNeue, Helvetica Neue, Helvetica, Arial, Lucida Grande, sans-serif",
  	fontSize(24.px),
  	color(Color("#323232")),
  	lineHeight(29.px),
  	maxWidth(600.px),
  	marginRight(auto),
  	marginLeft(auto),
    unsafeChild("h3") (
      marginBottom(0.px)
    ),
    unsafeChild("p") (
      marginTop(0.px)
    )
  )

//  val `#films, #top` = style(
//    textAlign.center,
//    transition := "all 1.0s ease-in-out"
//  )
//  //   val .disabled = style (display.none )
//  val `#films.hidden` = style(
//    opacity(0),
//    transform := "scale(0.001, 0.001)"
//  )
//  val `#top.hidden` = style(
//    opacity(0),
//    marginTop(-9999.px),
//    position.absolute,
//    visibility.hidden
//  )
  val selectWithOffset = select + style(
    marginTop(20.px)
  )

}
