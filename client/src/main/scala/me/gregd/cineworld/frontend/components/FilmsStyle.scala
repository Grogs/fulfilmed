package me.gregd.cineworld.frontend.components

import scalacss.Defaults._

object FilmsStyle extends StyleSheet.Inline {

  import dsl._

//  def textShadow[X, Y, Blur](offsetX: Length[X], offsetY: Length[Y], blurRadius: Length[Blur], color: Color) =
//    Attrs.textShadow := List[ {def value: scalacss.Value}](offsetX, offsetY, blurRadius, color).map(_.value).mkString(" ")

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
    margin(7.px, 7.px, 7.px, 1.px),
    border(1.px, solid, Color("#7F1717")),
    fontFamily :=! "Calibri, Verdana, Arial, sans-serif",
    fontSize(16.px),
    height(1.8.em),
    color.white,
    minWidth(190.px),
    backgroundColor(Color("#640F0F"))
  )


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
//    textShadow(0.px, 1.px, 9.px, rgba(0,0,0,1)),
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
  val tmdb = rating + style(
    backgroundImage := "url('data:image/svg+xml;utf8,<svg id=\"Layer_1\" data-name=\"Layer 1\" xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 251.34 222.81\"><defs><style>.cls-1{fill:#01d277;}</style></defs><title>PrimaryLogo_Green</title><path class=\"cls-1\" d=\"M2944.56,2236.34c14.6,0,24.48-9.88,24.48-24.48V2052.58c0-14.6-9.88-24.48-24.48-24.48H2742.19c-14.6,0-24.48,9.88-24.48,24.48v198.33l12.56-14.56h0V2052.58a11.94,11.94,0,0,1,11.92-11.92h202.37a11.94,11.94,0,0,1,11.92,11.92v159.28a11.94,11.94,0,0,1-11.92,11.92H2760.82l-12.56,12.56-0.08-.1\" transform=\"translate(-2717.71 -2028.09)\"/><polygon class=\"cls-1\" points=\"61.38 77.29 68.32 77.29 68.32 49.52 76.96 49.52 76.96 42.63 52.74 42.63 52.74 49.52 61.38 49.52 61.38 77.29\"/><polygon class=\"cls-1\" points=\"99.53 77.29 106.47 77.29 106.47 42.58 99.53 42.58 99.53 56.46 89.14 56.46 89.14 42.58 82.2 42.58 82.2 77.29 89.14 77.29 89.14 63.4 99.53 63.4 99.53 77.29\"/><polygon class=\"cls-1\" points=\"132.25 70.34 119.23 70.34 119.23 63.4 130.35 63.4 130.35 56.46 119.23 56.46 119.23 49.52 131.66 49.52 131.66 42.58 112.28 42.58 112.28 77.29 132.25 77.29 132.25 70.34\"/><polygon class=\"cls-1\" points=\"68.66 101.35 54.97 86.11 52.74 86.11 52.74 121.35 59.78 121.35 59.78 101.98 68.66 111.3 77.54 101.98 77.5 121.35 84.53 121.35 84.53 86.11 82.35 86.11 68.66 101.35\"/><path class=\"cls-1\" d=\"M2825.75,2114.16c-23.88,0-23.88,35.77,0,35.77S2849.63,2114.16,2825.75,2114.16Zm0,28.59c-13.88,0-13.88-21.45,0-21.45S2839.63,2142.75,2825.75,2142.75Z\" transform=\"translate(-2717.71 -2028.09)\"/><rect class=\"cls-1\" x=\"165.37\" y=\"86.65\" width=\"6.94\" height=\"34.7\"/><polygon class=\"cls-1\" points=\"185.07 114.41 185.07 107.47 196.19 107.47 196.19 100.53 185.07 100.53 185.07 93.59 197.5 93.59 197.5 86.65 178.13 86.65 178.13 121.35 198.09 121.35 198.09 114.41 185.07 114.41\"/><path class=\"cls-1\" d=\"M2780.79,2158.81h-10.34v34.7h10.34C2803.89,2193.51,2803.89,2158.81,2780.79,2158.81Zm0,27.76h-3.4v-20.82h3.4C2794.28,2165.75,2794.28,2186.57,2780.79,2186.57Z\" transform=\"translate(-2717.71 -2028.09)\"/><path class=\"cls-1\" d=\"M2824,2176.13c2.18-1.5,3.11-4.22,3.2-6.84,0.15-6.12-3.69-10.53-9.85-10.53h-13.74v34.75h13.74a10.32,10.32,0,0,0,10.24-10.44A8.43,8.43,0,0,0,2824,2176.13Zm-13.4-10.44h6.17a3.51,3.51,0,0,1,0,7h-6.17v-7Zm6.17,20.87h-6.17v-6.94h6.17a3.41,3.41,0,0,1,3.49,3.45A3.45,3.45,0,0,1,2816.77,2186.57Z\" transform=\"translate(-2717.71 -2028.09)\"/><polygon class=\"cls-1\" points=\"144.01 105.38 134.87 86.65 126.86 86.65 143.23 122.08 144.79 122.08 161.15 86.65 153.14 86.65 144.01 105.38\"/></svg>')",
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
