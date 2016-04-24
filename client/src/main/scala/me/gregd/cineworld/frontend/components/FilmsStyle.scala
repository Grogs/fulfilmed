package me.gregd.cineworld.frontend.components

import scalacss.Defaults._
import scalacss.ValueT.Color
import scalacss.ext.CssReset.webkitAppearance
import scalacss.{Attrs, Length, ValueT}

object FilmsStyle extends StyleSheet.Inline {

  import dsl._

  def textShadow[X, Y, Blur](offsetX: Length[X], offsetY: Length[Y], blurRadius: Length[Blur], color: ValueT[Color]) =
    Attrs.textShadow := List[ {def value: scalacss.Value}](offsetX, offsetY, blurRadius, color).map(_.value).mkString(" ")

  val filmListContainer= style(
    textAlign.center,
    margin(8.px)
  )
  val container = style(
    backgroundColor(Color("#111")),
    margin(0.px)
  )
  val attribution= style(
    color(Color("#999")),
    fontFamily :=! "HelveticaNeue, Verdana, Arial, sans-serif",
    textAlign.center,
    unsafeChild("#attribution > a") {
      color(Color("#bbb"))
    }
  )
  val header = style(
    width(100.%%),
    backgroundColor(Color("#690B0B")),
    textAlign.center
  )
  val menuGroup = style(
    display.inlineBlock,
    paddingLeft(20.px)
  )
  val select = style(
    padding(8.px, 30.px, 8.px, 10.px),
    margin(7.px, 7.px, 7.px, 1.px),
    borderRadius(10.px),
    border(1.px, solid, Color("#7F1717")),
    fontFamily :=! "Calibri, Verdana, Arial, sans-serif",
    fontSize(16.px),
    lineHeight(20.px),
    color.white,
    minWidth(190.px),
    webkitAppearance := "none",
    background := "url(\"data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='50.px' height='50.px'><polyline points='46.139,15.518 25.166,36.49 4.193,15.519' fill='white'/></svg>\") right no-repeat",
    backgroundColor(Color("#640F0F")),
    backgroundPosition := "right 10px top 9px",
    backgroundSize := "18px 18px"
  )
  val `select#filter`= style(
    background := "none",
    direction.rtl,
    paddingRight(10.px),
    minWidth(30.px)
  )

  //TODO
  //@media only screen and (max-device-width(480.px) {)
  //    div.menu-group {
  //        paddingLeft(10.px),
  //    }
  //    select.menu {
  //        fontSize(24.px),
  //        lineHeight(24.px),
  //        paddingRight(0),
  //        marginLeft(2.px),
  //        minWidth(120.px),
  //    }
  //)


  /*-----  FILM CARD CSS ------*/
  //TODO Do we need this?
  val label = style(
    color.white,
    fontFamily :=! "Calibri, Verdana, Arial, sans-serif",
    fontSize(30.px),
    marginTop(12.px)
  )

  /* Film card styling */


//  a { color(white) } TODO
//  val `.film-container:hover`= style( TODO
//    -webkit-transform(scale(1.05,1.07))
//  )
  val filmCard = style(
    width(296.px),
    height(434.px),
    position.relative,
    border(solid, 4.px, Color("#690B0B")),
    overflow.hidden,
    margin(5.px),
    display.inlineBlock,
    transition := "all ease 0.2s"
  )
  val filmTitle= style(
    fontFamily :=! "\"HelveticaNeue\", \"Helvetica Neue\", Helvetica, Arial, \"Lucida Grande\", sans-serif",
    fontWeight.bold,
    fontSize(36.px),
    color(Color("#E5E5E5")),
    lineHeight(44.px),
    textShadow(0.px, 1.px, 9.px, rgba(0,0,0,1)),
    textAlign.center,
    marginTop(20.px)
  )
  val filmPosition = style(
    width(100.%%),
    height(100.%%),
    position.absolute,
    top(0.px),
    left(0.px)
  )
  val filmInfo = filmPosition + style(
    backgroundColor(rgba(0,0,0,0.5)),
    zIndex(1)
  )
//  val `.film`= style(
  //    backgroundColor(rgba(0,0,0,0.5)),
  //    zIndex(-9)
  //  )
  val `.threedee.film`= style(
    background := "url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADYAAAA2CAYAAACMRWrdAAAFOUlEQVRoBd2YX0xbdRTHvzR1oxIQJgwLwrBDZBpA2QxqNiLzyZh4iWHZgjXRB9k0i+CLBBP2UJOZYQyMB4M8LdqZmDXL8MEYF7dk7AEisFCiNEyYLAGyMkCB0NYx6v3d3nv7621/7e2fu/75JfT379zzOx/Ouefce7O+A97JAn4EoOf/Mqbp3vN6L3uB4zzRdsZQ8SA6ApOJcAJYJsJleb18IFLt+6yskPdcIS+TS8nFM9SZu1DMNSKneBc8s2O439sJlz0ejcAdxeVBYGRfM7haM8qtX6OsZq/CDDeWL3Rh5oM+xbr6qRJMDkVaBeueu88LbdCC0Y6fOYzCICiiJBtF7/fihYsd0WpkyocEI9KawA2dwkRzP1y8/s3hH2A/eRJ/XhjGA9G8/FYLjGZxEmcXMhRpnZqFJXWIznIDr3YfEVY8o/0Ye6Wd2lU3VBWKtKp4PJdjsaiq+ju2W/CIh+5+vgG7aANiHDNDkdYXE1zHEGq7u3Fo+pKvWMoKzahcWECFpUle0b1VhcfkWWIGEUORPkZ9WNai4t4kSsXk5xnuwVhjp6DKOO2CqTpbGLsco1hdfBx7j9bIYLQsETJ0tMHVNyjIh/uJOhRpZao91/QxCuWMvoGFQR8U0bV0/Ctsig9vhuoGlFJQwDxmT/tlcy7aUd/7LWqGumgzVI1VhSKtSRVcnvisxl+4s3gd96yUBvsZzFinxIVtuBadeOjewJbjGux1FVgTCzWBerG1RpDLe/ts1HBRg5GTIsLd+VdO4brcp4MSiGdmWQTTY9N6AiOGPNw68AY2QkCJgogWLiawiHD2Tjhn3T6bcutRGVB4OZSffk2yF7tNdfKYDGhPBWzwk2jgYgYjh4bznOPDQaEQE7mC1l4c+tsO028jeGn9CkpLfMmD7LmmJ0kntFBQ/9h6MDfqlERUw0WVFWXtigErWxZ3XcLrZ1vkjKe4DFgbxu97GvEfvxEKav2nzzHFfSlcZhxfham+QFax9MVRzJ25Ls+VWTEhYEQ7C66QO4eXv/kIT5YEvht4HL/gjwNvCl6NBCVZX/aXC+X7RW+7JzBhOChHhWZgYeH4zQJzBwoO1/GJZB2bN69gw+r7b6uFEuCazqP+2icwiKQr/c1wtA8JM03ByAlMz/F7gT6LHH6CxQE/HKr4e7RIVLRu+xRTx3yvOkqwuJJHwJniJFxCoV95ovJUqIP4NcNz1Ywdfx1lCsSyEQnOMDAuF19J/5rNnyiktaCea0Y+5fbN8ZtBItJCwj0mKQ4H5zzVi6VF+qOYG1vTI9KljL4W5efNVIbdxtYk+3uCZmDEOjacFZOlLRRcNkq7f4XpHMeAAopuXEXZPv+nz535n3G3L0lg4eGGFHB6GD+zoXKgTQHHocy+iqoj8lM1v7+GuXYOOwpJepqwOkYrDTVmZ0sOdQs2GEv83niwOItVxzx0RfuQX7OfCj+ieRtLPS2Y6/SleeksZVZ8ZGDEgGjgJIMDezcPdSIIisgkFYwYwIZrQtWIFaaGEiIW1B46J3C77SBWAh0lyyUdjFjChgOe4D+mGt/lkP/sU9Drt+G5exsrlwew3McgEtFSAiwSHFWqRLMjd0owTdN9OHPYpSDOj7LioUkDI+drCZdUMC3hkg6mFVxKgGkBlzJgiYZLKbBEwqUcWKLgUhIsEXApCxYvXEqDxQOX8mCxwqUFWCxwj/RFkxgYb2O98ij1po3HJMNZD87SvtSnHRgxXA1cWoKpgUtbsEhwaQ0WDi7twVhwGQEWCi7t6hiBCNekOpdxYASawP0PmNyMBd3c8EIAAAAASUVORK5CYII=') no-repeat 100% 0,  rgba(0,0,0,0.5)",
    right(0.px)
  )
  val filmBackground = filmPosition + style(
    filter := "blur(1.4px)",
    transform := "translate3d(0, 0, 0)"
    //TODO
    /* blur caused performance issues - found a source for higher-res images instead
    -webkit-filter(blur(3.px)),
    -webkit-transform(translate3d(0, 0, 0)),
    */
//    margin(0.px, -1.px, 0.px, 0.px) TODO verify if we want this
  )
  val ratings= style(
    marginTop(30.px),
    textAlign.center
  )
  val rating = style(
    fontFamily :=! "\"HelveticaNeue\", \"Helvetica Neue\", Helvetica, Arial, \"Lucida Grande\", sans-serif",
    fontWeight.bold,
    fontSize(26.px),
    color(Color("#FFFFFF")),
    lineHeight(32.px),
    /* imdb rating(*/
    margin(10.px),
    display.inlineBlock,
    unsafeChild("a") (
      color.white
    )
  )
  val rt = rating + style(
    backgroundImage := "url(\"data:image/svg+xml;utf8,<svg width='31.px' height='30.px' viewBox='0 0 31 30' version='1.1' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:sketch='http://www.bohemiancoding.com/sketch/ns'><g id='Movie-Screen' stroke='none' stroke-width='1' fill='none' fill-rule='evenodd' sketch:type='MSPage'><g id='Desktop' sketch:type='MSArtboardGroup' transform='translate(-225.000000, -184.000000)'><g id='Spiderman' sketch:type='MSLayerGroup' transform='translate(58.000000, 64.000000)'><g id='ratings' transform='translate(39.000000, 116.000000)' sketch:type='MSShapeGroup'><g id='ciritcs-rating-+-rotten-tomatoes' transform='translate(130.000000, 3.000000)'><g id='rotten-tomatoes' transform='translate(0.000000, 1.000000)'><path d='M26.9123521,17 C27.8034735,10.9248678 21.7985589,6 13.5,6 C5.20144106,6 -0.803473516,10.9248678 0.0876479024,17 C0.978769321,23.0751322 6.9836839,28 13.5,28 C20.0163161,28 26.0212307,23.0751322 26.9123521,17 Z' id='Oval-5' stroke='Color('#F81C1C')' stroke-width='4' fill='Color('#FF3E3E')'></path><path d='M4.78245854,14.6173982 C7.27434261,12.7111678 7.78955324,8.75175877 7.78955324,8.75175877 C7.78955324,8.75175877 2.87089624,2.98993617 4.24100609,3.84028547 C5.61111594,4.69063477 11.5372058,4.69063493 11.5372058,4.69063493 C11.5372058,4.69063493 16.9657391,-2.02047676 16.5967268,0.612136057 C16.2277144,3.24474887 17.7855412,6.14013104 17.7855412,6.14013104 C17.7855412,6.14013104 27.3339459,8.53095351 24.4610443,8.53095341 C21.5881427,8.53095332 17.8995723,11.0970927 17.8995723,11.0970927 C17.8995723,11.0970927 16.9657396,18.689004 16.9657392,16.6532011 C16.9657387,14.6173982 11.721712,12.7111674 11.721712,12.7111674 C11.721712,12.7111674 2.29057446,16.5236287 4.78245854,14.6173982 Z' id='Star-1' stroke='Color('#59B12D')' stroke-width='3' fill='Color('#3A851B')'></path><path d='M13,7 C12.3109841,4.64124868 10.0614994,3.0223279 9,1' id='Path' stroke='Color('#3B851C')' stroke-width='4' transform='translate(11.000000, 4.000000) rotate(26.000000) translate(-11.000000, -4.000000) '></path></g></g></g></g></g></g></svg>\")",
    backgroundRepeat := "no-repeat",
    paddingLeft(37.px)
  )
  val rtAudience = rating + style(
    backgroundImage := "url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABoAAAAeCAYAAAAy2w7YAAAFSUlEQVRIDbVWS28URxD+emZ2dhcb29gGg9lgIuIcEEiJcsgTidxQxAHyEPkROfEPckDKLQq/gUhJhCIlUg5cEHIigYAARjwc3ots2V7ZzhrveufZleqe7XYvOAk5pKT19FRX1zf11dfdFkSEl7KpTychgnsEyiBEXWTZURw+O/NSazlIbAo0dewVUHiY51to4GeMymMIgrPp9hKybQH81QzhYgrk69vhV98C0QSEdxWHvrv2d8AvAl34/B34uCj7vVUk8LxUChD65YAv4/GyZxKFTyN463kuIPx8wG/6z/IheHQS73//tYlxn71AU599DM//Nq6VK7LP13HhXEzIpBASiF+t2rUaKJKIJtknBJARqg86/EzewIc/TtvA7qAAEhz524lpSBykABS9toVXFua1cwRLCfwO4231kQ0zdc/Y18yQjQTIRkMTirAewY9kwn28KDLiHv7QMpMFFRdOfEEeXu9MViC41V4rN/MMkoKYsY76crby0xgeV5LuKKG0lIH7U8SmUvujPeWQyv4H8L21YqL4WwD5dDAdKZV5EvGeMsqzMUpzMSp/rHMlEn4zR9hIKFjLkVc97aNAIC8LRReFcxFVH0YanKo+4r0VHwIZzn8yYcACPcjFDaWizlAAucUHMXFMJiXjoZD9vk4mS0KkE2WoRLKZIlxghnyBZLgk/DWmccinfFupoJyr5BypgJ99KYS3X6WL9u37RfT1vSeGYoGd1T6MVTJvMKxgaymhLV4qQi7TF1UaCJJ0bzVE6KFU70gl8Wis5CFhlbRZ7tyfXHV8OeFdtp6LentZLhGJOB6VabovwODgfWo0PqIVru1RWxUY8FJlqssbneax0TZ30FNdNO8qmLulTFfEXfP5t0M5qFJpVer1upcvLJxXjv/NOp1fVe4ASXIFlYrF8U+eBC0uAs0m5DXe6PPzxdyuXRDVKjcugQi5UDW+exeQRf3iyBGI4WGI8XHIqSnQ5ct6HQlxpRhw4zq12mxUq5H65TMzTHRh6alT2qf86enTxq2fcnXVzkWTkz1z8fHjdq69e/dRnixoZmKvmpJo2tnUo6PGDSwtbYx5RAsL9l3s3GnHlOegW7c23pNE59b9ZMSiPJ6WN2/aIDEyYse0otTimKLX2NiYGYHu3wc6fBSxcd7ZvkZDf5EGYlVaILeiHqDnK3KAeipyGOGTzebVQHGrtUHdnTuglK8AZU5FWF4ufN2/WjDdsXAqchnhimxeDTSwurrCun+k17GqaKa4z4TTI/oHIDhAPYw4TGkgDeA4bTDL1drzPXLF0AVS0teS7y6K2u3eipSfK7J8yi7PolwG+vuLZVkG4r1lbDPqNAiDKeN8DwebzT9NvK1I5LlFtxWpKKdPrvI2BXIUy5KzH67SWKC1PP+dJ/U2p3v3QF2Jusoze0ltQDQaan1hXeoME8rpMqTeLdD2RqPFh3mhArXpbt9W83CBrCBUv4wyBwf10aRiXSaElJYhNWeBdKAjRytTR3lG4pvSxgwoJrQxM2tZxgflhvUA9WzcGzd0lDoojZmKXCAjbX3sdA9YZuauZsgs5GcPkPQ820BiIN0LpyIDhE2kLa9ft2ndjWqcxVXefVucnZ3eVaupY6FEjx8jOXAA4OvBGF26hPzMGeTnzhkXJH9QcugQ6MkT63OZMU51+Zqxfsa12jUu/c0e5398kVn2dnV+vriQumt7qFM+1vdX/PiJ4TfugW7wvzxi/uhLzPc3cwsLzl1TrHqhIjdZND4+wTfku3xfHWG/Kp242oTf+d9JrPDiFf6wFZLywRNOvp+oOBbcJN3xX4Md3ucfgyQcAAAAAElFTkSuQmCC')",
    backgroundRepeat := "no-repeat",
    paddingLeft(37.px)
  )
  val imdb = rating + style(
    backgroundImage := "url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACYAAAAaCAYAAADbhS54AAAEL0lEQVRIDb1WzW9bRRDfWcfJc0LtfonWtUMKpAktceIqAiQ+BEIFwYlTJc5IICFxQJU4IFpVlZAQElzgRv8EzoAQEuqBS6kiG78KcFWJhtokpQo4TVL74XiH3zxnnecXO1E+3L3MzuzszG9nd2aHmFk9yPHr19R/KHEkeuTV+ZXN/FIvgdVyx0YM64zWKqMUZYhAWY0zqR+dqduv9RzY4k/7DwwMDk6QpgwzTSoAIEUToPFuzg2bD2LZ8mfd1rcVMbmGkdHUyQgAEEsUOIPTC4h0NwebyXFbXynV+MTJzt26dIn0xYtsrH5HYIRRzR0dMUaugTJ+BBhU8bgi6rOb95B6rPiaM1V6wdpsA1bLpz/Gwst4CxN4E/us0o4pqwoTX8e7chHhApNZUhT5gpQ6uNEmLzvZchxR9LOxBUyu6fGx9BI29G/ctKXkP2gUYRLOlQvqUt24safKt8M7l35Jj0WZvsHhR8Nr7NUfc56e/0PkrWt59ETqDfBbgsKBZmHUd46522g03CLdKU5Pcz3sqBO/b6p049YVyiQTx95m0h/CVtLqmWgEz0X5wPyI/XP1UHzIiV3F9T1hlRD+RZy+IM41ImCI3apXcw8+s3CvpbPLSTU3/CVKyXvWDN7ZBbwzeU6qbzmXPDU4ELscAlVnbkw52b9m7SahsSCzB3OActvM+JnelOiojnyKJHw2qGAUX3ZOt4MKru/V3BhuA4akkKv0h5aHahmh4H8uz5XPBWW9mq/c9677HtcdnLj5HQ0IC2Bh1Lww+jp767q9mx1+7u4SArH+XFAjk8nUSR+YZFXQtVTyIN/LuUQHv0bbZx5Ze2daUh0Z2Ep1+V4q+QP7ewlIbN+98vBD6WT6e/wqTwZ94b37gdF+/SH1e3DRMbGeRy2eiJ5HDXsx6FfmKBmHhWqfCSWAdAki79Wo5ZPHldbvB+1L0q02FEpU6S2RNyu/AEOu2sFr4bT8bqgU79iAM6Fh06ARQJQmSfVNwqaffU3bXFxe8c5IMlhfPjCDCh+xEqFM0+E2JLjcaT4zQ9HxvqNjEXQkeCeTAIDeTGWGnMERq6/t4S1dWzCsLgRBidj/kqrXUsO6X/9pDTQpF1dZvTmULeXb5UqJPkcFAHoxOCf0ZWiH5Dvb8q8N25LEm6uU4sdf4lpwrdVdePnhCq4zEVyU/xKd5rtaUQJRlUg0G0RSe5C1LDUMBRZtdrZ0vs0vmBYw9GI/wPGZsMKueeZVuLmBvszFQV18QwWt0RKdnp9Fcedu9lvARKE2k3pERegjAHyn24bN5HBTxrUWAMSVvqyBv3D2Zvm3U2dZ+rVtjTZgdmc1lz6Hlvpzy2+grO6h3jQ7U0RCvjVv5b6beL7y7wbdHQo6AhNbXj79LcgrOH0xcA2uJi48iM6jWcc6nKry9+rZhcU79Z1cQwdz2xb9D3d6wkC9YctQAAAAAElFTkSuQmCC')",
    backgroundRepeat := "no-repeat",
    paddingLeft(46.px)
  )
  val times = style(
    margin(30.px, 20.px, 0.px, 20.px),
    textAlign.left
  )
  val time = style(
    fontFamily :=! "\"HelveticaNeue\", \"Helvetica Neue\", Helvetica, Arial, \"Lucida Grande\", sans-serif",
    fontWeight.bold,
    fontSize(20.px),
    color(Color("#E5E5E5")),
    lineHeight(25.px),
    padding(4.px),
    borderRadius(10.px),
    backgroundColor(Color("#2A353F")),
    border(2.px, solid, Color("#00ADFF")),
    display.inlineBlock,
    marginRight(10.px),
    marginBottom(8.px)
  )



}
