package me.gregd.cineworld.frontend.components

import japgolly.scalajs.react.{BackendScope, Callback, ReactComponentB}
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom._

import scalacss.Defaults._
import scalacss.ValueT.Color
import scalacss.{Attrs, Length, StyleA, ValueT}
import scalacss.ScalaCssReact._

object IndexPage {
  val label = "label".reactAttr

  //  class Backend($: BackendScope[Unit, Unit]) {

  //    val element = //  }

  def apply() = ReactComponentB.static("IndexPage", {
    implicit def styleaToTagMod(s: StyleA): TagMod = ^.className := s.htmlClass //TODO I get linking errors if I don't copy this across
    <.div(^.id := "indexPage",
      <.div(IndexStyle.top,
        <.div(IndexStyle.title,
          "Fulfilmed"),
        <.div(IndexStyle.blurb,
          "A better way to find the best films at your local cinema"),
        <.script(^.`type` := "text/javascript",
          """
          function selectCinema(e) {location.href = "films.html#/cinema/"+e.value}
          """),
        for {
          (typ, cinemas) <- cinemas
        } yield
          <.div(
            <.select(IndexStyle.selectWithOffset, ^.id := "cinemas", ^.`class` := ".flat", ^.onChange := "selectCinema(this)",
              <.option(^.value := "?", ^.selected := "selected", ^.disabled := "disabled", typ),
              for {
                (groupName, cinemas) <- cinemas
              } yield
                <.optgroup(label := groupName,
                for (cinema <- cinemas) yield <.option(^.value := cinema.id, cinema.name)
              )
            ))
      ),
      <.div(IndexStyle.description, ^.id := "description",
        <.h3("What?"),
        <.p("Fulfilmed lets you view the movies airing at your local cinema. It adds some features over your Cinema's standard website; inline movie ratings, sorting/filtering by rating, mark films as watched."),

        <.h3("Why?"),
        <.p("I want to easily, at a glance, figure out which film to go see at the cinema. I can't do that with my cinema's website - I have to google/research each film individually. "),

        <.h3("What's next?"),
        <.p("Currently only Cineworld and Odeon cinemas are supported; I may add more. Beyond that, I want to receive notifications about upcoming high rated films.")

      )

    )
  }
  )
    .componentWillMountCB(Callback(document.body.style.background = "white"))
    .componentWillUnmountCB(Callback(document.body.style.background = null))
    .build

  case class Cinema(name: String, id: String)

  val cinemas = Map(
    "Cineworld" -> Map(
      "London cinemas" -> Seq(
        Cinema("London - Bexleyheath", "45"),
        Cinema("London - Chelsea", "10"),
        Cinema("London - Enfield", "22"),
        Cinema("London - Feltham", "25"),
        Cinema("London - Fulham Road", "26"),
        Cinema("London - Hammersmith", "30"),
        Cinema("London - Haymarket", "32"),
        Cinema("London - Ilford", "37"),
        Cinema("London - Shaftesbury Avenue", "53"),
        Cinema("London - Staples Corner", "60"),
        Cinema("London - The O2, Greenwich", "79"),
        Cinema("London - Wandsworth", "65"),
        Cinema("London - Wembley", "89"),
        Cinema("London - West India Quay", "66")
      ),
      "All other cinemas" -> Seq(
        Cinema("Aberdeen - Queens Links", "1"),
        Cinema("Aberdeen - Union Square", "78"),
        Cinema("Aldershot", "87"),
        Cinema("Ashford", "12"),
        Cinema("Bedford", "34"),
        Cinema("Birmingham", "56"),
        Cinema("Boldon Tyne and Wear", "67"),
        Cinema("Bolton", "72"),
        Cinema("Bradford", "73"),
        Cinema("Braintree", "2"),
        Cinema("Brighton", "3"),
        Cinema("Bristol", "4"),
        Cinema("Burton upon Trent", "5"),
        Cinema("Bury St. Edmunds", "6"),
        Cinema("Cambridge", "7"),
        Cinema("Cardiff", "8"),
        Cinema("Castleford", "9"),
        Cinema("Cheltenham", "11"),
        Cinema("Chesterfield", "14"),
        Cinema("Chichester", "15"),
        Cinema("Crawley", "16"),
        Cinema("Didcot", "17"),
        Cinema("Didsbury", "18"),
        Cinema("Dundee", "19"),
        Cinema("Eastbourne", "20"),
        Cinema("Edinburgh", "21"),
        Cinema("Falkirk", "24"),
        Cinema("Glasgow - IMAX at GSC", "88"),
        Cinema("Glasgow - Parkhead", "27"),
        Cinema("Glasgow - Renfrew Street", "28"),
        Cinema("Gloucester Quays", "90"),
        Cinema("Harlow", "31"),
        Cinema("Haverhill", "76"),
        Cinema("High Wycombe", "33"),
        Cinema("Hull", "35"),
        Cinema("Huntingdon", "36"),
        Cinema("Ipswich", "38"),
        Cinema("Isle Of Wight", "39"),
        Cinema("Jersey", "40"),
        Cinema("Leigh", "83"),
        Cinema("Liverpool", "41"),
        Cinema("Llandudno", "42"),
        Cinema("Luton", "43"),
        Cinema("Middlesbrough", "44"),
        Cinema("Milton Keynes", "46"),
        Cinema("Newport Wales", "47"),
        Cinema("Northampton", "48"),
        Cinema("Nottingham", "49"),
        Cinema("Rochester", "50"),
        Cinema("Rugby", "51"),
        Cinema("Runcorn", "52"),
        Cinema("Sheffield", "54"),
        Cinema("Shrewsbury", "55"),
        Cinema("Solihull", "57"),
        Cinema("Southampton", "58"),
        Cinema("St Helens", "59"),
        Cinema("St Neots", "91"),
        Cinema("Stevenage", "61"),
        Cinema("Stockport", "62"),
        Cinema("Swindon", "63"),
        Cinema("Telford", "92"),
        Cinema("Wakefield", "64"),
        Cinema("Weymouth", "68"),
        Cinema("Witney", "77"),
        Cinema("Wolverhampton", "69"),
        Cinema("Yeovil", "71")
      )
    ),
    "Odeon cinemas" -> Map(
      "London cinemas" -> Seq(
        Cinema("Odeon BFI IMAX", "6883"),
        Cinema("Odeon Camden", "6906"),
        Cinema("Odeon Covent Garden", "1503"),
        Cinema("Odeon Greenwich", "6897"),
        Cinema("Odeon Holloway", "6900"),
        Cinema("Odeon Kensington", "1516"),
        Cinema("Odeon Lee Valley", "6889"),
        Cinema("Odeon Leicester Square", "1517"),
        Cinema("Odeon Marble Arch", "1518"),
        Cinema("Odeon Muswell Hill", "6904"),
        Cinema("Odeon Panton Street", "1520"),
        Cinema("Odeon Putney", "6909"),
        Cinema("Odeon South Woodford", "6927"),
        Cinema("Odeon Streatham", "6916"),
        Cinema("Odeon Studios", "6777"),
        Cinema("Odeon Surrey Quays", "6907"),
        Cinema("Odeon Swiss Cottage", "5208"),
        Cinema("Odeon Tottenham Court Road", "1522"),
        Cinema("Odeon West End", "1523"),
        Cinema("Odeon Whiteleys", "6155"),
        Cinema("Odeon Whiteleys - The Lounge", "6850"),
        Cinema("Odeon Wimbledon", "6925")
      ),
      "All other cinemas" -> Seq(
        Cinema("Odeon Andover", "7165"),
        Cinema("Odeon Aylesbury", "7167"),
        Cinema("Odeon Ayr", "7235"),
        Cinema("Odeon Banbury", "7168"),
        Cinema("Odeon Barnet", "6879"),
        Cinema("Odeon Basingstoke", "7169"),
        Cinema("Odeon Bath", "7236"),
        Cinema("Odeon Beckenham", "6881"),
        Cinema("Odeon Belfast", "7170"),
        Cinema("Odeon Birmingham Broadway Plaza", "7171"),
        Cinema("Odeon Birmingham New Street", "7172"),
        Cinema("Odeon Blackpool", "7173"),
        Cinema("Odeon Blanchardstown", "6795"),
        Cinema("Odeon Bournemouth", "7175"),
        Cinema("Odeon Bracknell", "7177"),
        Cinema("Odeon Braehead", "4826"),
        Cinema("Odeon Bridgend", "7180"),
        Cinema("Odeon Brighton", "7179"),
        Cinema("Odeon Bristol", "7178"),
        Cinema("Odeon Bromborough", "7181"),
        Cinema("Odeon Canterbury", "7182"),
        Cinema("Odeon Cardiff", "7183"),
        Cinema("Odeon Cavan", "7184"),
        Cinema("Odeon Chatham", "7185"),
        Cinema("Odeon Chelmsford", "7186"),
        Cinema("Odeon Colchester", "7187"),
        Cinema("Odeon Coolock", "6796"),
        Cinema("Odeon Coventry", "7188"),
        Cinema("Odeon Crewe", "7189"),
        Cinema("Odeon Darlington", "7190"),
        Cinema("Odeon Derby", "7191"),
        Cinema("Odeon Dorchester", "7192"),
        Cinema("Odeon Dudley Merry Hill", "7194"),
        Cinema("Odeon Dumfries", "7238"),
        Cinema("Odeon Dundee", "7237"),
        Cinema("Odeon Dunfermline", "7195"),
        Cinema("Odeon East Kilbride", "7196"),
        Cinema("Odeon Edinburgh Lothian Road", "7239"),
        Cinema("Odeon Edinburgh Wester Hailes", "7242"),
        Cinema("Odeon Epsom", "7197"),
        Cinema("Odeon Esher", "6955"),
        Cinema("Odeon Exeter", "7198"),
        Cinema("Odeon Gerrards Cross", "7199"),
        Cinema("Odeon Glasgow Springfield Quay", "7240"),
        Cinema("Odeon Guildford", "7200"),
        Cinema("Odeon Harrogate", "7201"),
        Cinema("Odeon Hastings", "7202"),
        Cinema("Odeon Hatfield", "7203"),
        Cinema("Odeon Hereford", "7204"),
        Cinema("Odeon Hereford", "7969"),
        Cinema("Odeon Huddersfield", "7205"),
        Cinema("Odeon Hull", "7206"),
        Cinema("Odeon Kettering", "7207"),
        Cinema("Odeon Kilmarnock", "7241"),
        Cinema("Odeon Kingston", "6903"),
        Cinema("Odeon Leeds-Bradford", "7176"),
        Cinema("Odeon Leicester", "7211"),
        Cinema("Odeon Limerick", "7156"),
        Cinema("Odeon Lincoln", "7213"),
        Cinema("Odeon Liverpool ONE", "7215"),
        Cinema("Odeon Liverpool Switch Island", "7214"),
        Cinema("Odeon Llanelli", "7217"),
        Cinema("Odeon Loughborough", "7218"),
        Cinema("Odeon Maidenhead", "7221"),
        Cinema("Odeon Maidstone", "7220"),
        Cinema("Odeon Manchester", "7222"),
        Cinema("Odeon Mansfield", "7223"),
        Cinema("Odeon Metrocentre", "4841"),
        Cinema("Odeon Milton Keynes", "7227"),
        Cinema("Odeon Naas", "7228"),
        Cinema("Odeon Newark", "7229"),
        Cinema("Odeon Newbridge", "6645"),
        Cinema("Odeon Norwich", "7231"),
        Cinema("Odeon Nuneaton", "7234"),
        Cinema("Odeon Oxford George Street", "7243"),
        Cinema("Odeon Oxford Magdalen Street", "7244"),
        Cinema("Odeon Point Village", "7193"),
        Cinema("Odeon Port Solent", "4869"),
        Cinema("Odeon Portlaoise", "7247"),
        Cinema("Odeon Preston", "7249"),
        Cinema("Odeon Richmond", "6910"),
        Cinema("Odeon Richmond Studio", "6911"),
        Cinema("Odeon Rochdale", "7254"),
        Cinema("Odeon Salisbury", "7255"),
        Cinema("Odeon Sheffield", "7257"),
        Cinema("Odeon Silverlink", "4858"),
        Cinema("Odeon Southampton", "7260"),
        Cinema("Odeon Southend", "7259"),
        Cinema("Odeon Stillorgan", "7262"),
        Cinema("Odeon Stoke", "7264"),
        Cinema("Odeon Swadlincote", "7161"),
        Cinema("Odeon Swansea", "7266"),
        Cinema("Odeon Tamworth", "7268"),
        Cinema("Odeon Taunton", "7269"),
        Cinema("Odeon Telford", "7270"),
        Cinema("Odeon Thurrock", "4881"),
        Cinema("Odeon Trafford Centre", "7224"),
        Cinema("Odeon Trowbridge", "7763"),
        Cinema("Odeon Tunbridge Wells", "7273"),
        Cinema("Odeon Uxbridge", "7274"),
        Cinema("Odeon Warrington", "7275"),
        Cinema("Odeon Waterford", "7276"),
        Cinema("Odeon West Bromwich", "7457"),
        Cinema("Odeon Weston-Super-Mare", "7277"),
        Cinema("Odeon Worcester", "7278"),
        Cinema("Odeon Wrexham", "4887")
      )
    ),
    "Cinema City (Hungary) cinemas..." -> Map(
      "Budapest cinemas" -> Seq(
        Cinema("Cinema City Allee", "1010202"),
        Cinema("Cinema City Aréna", "1010203"),
        Cinema("Cinema City Duna Pláza", "1010206"),
        Cinema("Cinema City WestEnd", "1010217"),
        Cinema("Cinema City MOM Park", "1010218"),
        Cinema("Palace Mammut", "1010219"),
        Cinema("Cinema City Campona", "1010220")
      ),
      "All other cinemas" -> Seq(
        Cinema("Cinema City Alba", "1010201"),
        Cinema("Cinema City Balaton", "1010204"),
        Cinema("Cinema City Debrecen", "1010205"),
        Cinema("Cinema City Győr", "1010207"),
        Cinema("Cinema City Kaposvár", "1010208"),
        Cinema("Cinema City Miskolc", "1010209"),
        Cinema("Cinema City Nyíregyháza", "1010210"),
        Cinema("Cinema City Pécs", "1010211"),
        Cinema("Cinema City Savaria", "1010212"),
        Cinema("Cinema City Sopron", "1010213"),
        Cinema("Cinema City Szeged", "1010214"),
        Cinema("Cinema City Szolnok", "1010215"),
        Cinema("Cinema City Zalaegerszeg", "1010216")
      )
    )
  )

}
