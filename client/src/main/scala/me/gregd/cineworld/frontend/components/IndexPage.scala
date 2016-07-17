package me.gregd.cineworld.frontend.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import me.gregd.cineworld.frontend.{Films, Page}
import org.scalajs.dom._
import org.scalajs.dom.html.Select

import scala.scalajs.js.debugger
import scalacss.Defaults._
import scalacss.ValueT.Color
import scalacss.{Attrs, Length, StyleA, ValueT}
import scalacss.ScalaCssReact._

object IndexPage {
  val label = "label".reactAttr

  //  class Backend($: BackendScope[Unit, Unit]) {

  //    val element = //  }


  def apply(router: RouterCtl[Page]) = ReactComponentB.static("IndexPage", {
    def selectCinema(e: SyntheticEvent[Select]): Callback = {
      val cinemaId = e.target.value
      router.set(Films(cinemaId))
    }
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
            <.select(IndexStyle.selectWithOffset, ^.id := "cinemas", ^.`class` := ".flat", ^.onChange ==> selectCinema,
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
        Cinema("London - Bexleyheath", "1010810"),
          Cinema("London - Chelsea", "1010825"),
          Cinema("London - Enfield", "1010835"),
          Cinema("London - Feltham", "1010837"),
          Cinema("London - Fulham Road", "1010838"),
          Cinema("London - Haymarket", "1010848"),
          Cinema("London - Ilford", "1010852"),
          Cinema("London - Staples Corner", "1010873"),
          Cinema("London - The O2 Greenwich", "1010842"),
          Cinema("London - Wandsworth", "1010879"),
          Cinema("London - Wembley", "1010880"),
          Cinema("London - West India Quay", "1010882"),
          Cinema("London - Wood Green", "1010884")
      ),
      "All other cinemas" -> Seq(
        Cinema("Aberdeen - Queens Links", "1010804"),
          Cinema("Aberdeen - Union Square", "1010808"),
          Cinema("Aldershot", "1010805"),
          Cinema("Ashford", "1010806"),
          Cinema("Ashton-under-Lyne", "1010807"),
          Cinema("Bedford", "1010809"),
          Cinema("Birmingham - Broad Street", "1010812"),
          Cinema("Birmingham - NEC", "1010892"),
          Cinema("Boldon Tyne and Wear", "1010819"),
          Cinema("Bolton", "1010813"),
          Cinema("Bradford ", "1010811"),
          Cinema("Braintree", "1010815"),
          Cinema("Brighton", "1010816"),
          Cinema("Bristol", "1010818"),
          Cinema("Broughton", "1010889"),
          Cinema("Burton upon Trent", "1010814"),
          Cinema("Bury St Edmunds", "1010817"),
          Cinema("Cardiff", "1010821"),
          Cinema("Castleford", "1010822"),
          Cinema("Cheltenham", "1010824"),
          Cinema("Cheltenham - The Screening Rooms", "1010828"),
          Cinema("Chesterfield", "1010829"),
          Cinema("Chichester", "1010823"),
          Cinema("Crawley", "1010827"),
          Cinema("Didcot", "1010830"),
          Cinema("Didsbury", "1010831"),
          Cinema("Dundee", "1010832"),
          Cinema("Eastbourne", "1010833"),
          Cinema("Edinburgh", "1010834"),
          Cinema("Falkirk", "1010836"),
          Cinema("Glasgow - IMAX at GSC", "1010844"),
          Cinema("Glasgow - Parkhead", "1010839"),
          Cinema("Glasgow - Renfrew Street", "1010843"),
          Cinema("Glasgow - Silverburn", "1010891"),
          Cinema("Gloucester Quays", "1010841"),
          Cinema("Harlow", "1010846"),
          Cinema("Haverhill", "1010847"),
          Cinema("High Wycombe", "1010851"),
          Cinema("Hinckley", "1010895"),
          Cinema("Hull", "1010849"),
          Cinema("Huntingdon", "1010850"),
          Cinema("Ipswich", "1010854"),
          Cinema("Isle Of Wight", "1010853"),
          Cinema("Jersey", "1010855"),
          Cinema("Leigh", "1010856"),
          Cinema("Liverpool - CLOSED", "1010857"),
          Cinema("Llandudno", "1010858"),
          Cinema("Loughborough", "1010898"),
          Cinema("Luton", "1010859"),
          Cinema("Middlesbrough", "1010860"),
          Cinema("Milton Keynes", "1010861"),
          Cinema("Newport - Friars Walk", "1010893"),
          Cinema("Newport - Spytty Park", "1010862"),
          Cinema("Northampton", "1010863"),
          Cinema("Nottingham", "1010864"),
          Cinema("Rochester", "1010865"),
          Cinema("Rugby", "1010866"),
          Cinema("Runcorn", "1010867"),
          Cinema("Sheffield", "1010869"),
          Cinema("Shrewsbury", "1010870"),
          Cinema("Solihull", "1010871"),
          Cinema("Southampton", "1010872"),
          Cinema("St Helens", "1010875"),
          Cinema("St Neots", "1010887"),
          Cinema("Stevenage", "1010874"),
          Cinema("Stockport", "1010876"),
          Cinema("Stoke-on-Trent", "1010896"),
          Cinema("Swindon - Regent Circus", "1010888"),
          Cinema("Swindon - Shaw Ridge", "1010877"),
          Cinema("Telford", "1010802"),
          Cinema("Wakefield", "1010878"),
          Cinema("Weymouth", "1010881"),
          Cinema("Whiteley", "1010894"),
          Cinema("Witney", "1010883"),
          Cinema("Wolverhampton", "1010885"),
          Cinema("Yate", "1010897"),
          Cinema("Yeovil", "1010886")
      )
    )
//    "Odeon cinemas" -> Map(
//      "London cinemas" -> Seq(
//        Cinema("Odeon BFI IMAX", "6883"),
//        Cinema("Odeon Camden", "6906"),
//        Cinema("Odeon Covent Garden", "1503"),
//        Cinema("Odeon Greenwich", "6897"),
//        Cinema("Odeon Holloway", "6900"),
//        Cinema("Odeon Kensington", "1516"),
//        Cinema("Odeon Lee Valley", "6889"),
//        Cinema("Odeon Leicester Square", "1517"),
//        Cinema("Odeon Marble Arch", "1518"),
//        Cinema("Odeon Muswell Hill", "6904"),
//        Cinema("Odeon Panton Street", "1520"),
//        Cinema("Odeon Putney", "6909"),
//        Cinema("Odeon South Woodford", "6927"),
//        Cinema("Odeon Streatham", "6916"),
//        Cinema("Odeon Studios", "6777"),
//        Cinema("Odeon Surrey Quays", "6907"),
//        Cinema("Odeon Swiss Cottage", "5208"),
//        Cinema("Odeon Tottenham Court Road", "1522"),
//        Cinema("Odeon West End", "1523"),
//        Cinema("Odeon Whiteleys", "6155"),
//        Cinema("Odeon Whiteleys - The Lounge", "6850"),
//        Cinema("Odeon Wimbledon", "6925")
//      ),
//      "All other cinemas" -> Seq(
//        Cinema("Odeon Andover", "7165"),
//        Cinema("Odeon Aylesbury", "7167"),
//        Cinema("Odeon Ayr", "7235"),
//        Cinema("Odeon Banbury", "7168"),
//        Cinema("Odeon Barnet", "6879"),
//        Cinema("Odeon Basingstoke", "7169"),
//        Cinema("Odeon Bath", "7236"),
//        Cinema("Odeon Beckenham", "6881"),
//        Cinema("Odeon Belfast", "7170"),
//        Cinema("Odeon Birmingham Broadway Plaza", "7171"),
//        Cinema("Odeon Birmingham New Street", "7172"),
//        Cinema("Odeon Blackpool", "7173"),
//        Cinema("Odeon Blanchardstown", "6795"),
//        Cinema("Odeon Bournemouth", "7175"),
//        Cinema("Odeon Bracknell", "7177"),
//        Cinema("Odeon Braehead", "4826"),
//        Cinema("Odeon Bridgend", "7180"),
//        Cinema("Odeon Brighton", "7179"),
//        Cinema("Odeon Bristol", "7178"),
//        Cinema("Odeon Bromborough", "7181"),
//        Cinema("Odeon Canterbury", "7182"),
//        Cinema("Odeon Cardiff", "7183"),
//        Cinema("Odeon Cavan", "7184"),
//        Cinema("Odeon Chatham", "7185"),
//        Cinema("Odeon Chelmsford", "7186"),
//        Cinema("Odeon Colchester", "7187"),
//        Cinema("Odeon Coolock", "6796"),
//        Cinema("Odeon Coventry", "7188"),
//        Cinema("Odeon Crewe", "7189"),
//        Cinema("Odeon Darlington", "7190"),
//        Cinema("Odeon Derby", "7191"),
//        Cinema("Odeon Dorchester", "7192"),
//        Cinema("Odeon Dudley Merry Hill", "7194"),
//        Cinema("Odeon Dumfries", "7238"),
//        Cinema("Odeon Dundee", "7237"),
//        Cinema("Odeon Dunfermline", "7195"),
//        Cinema("Odeon East Kilbride", "7196"),
//        Cinema("Odeon Edinburgh Lothian Road", "7239"),
//        Cinema("Odeon Edinburgh Wester Hailes", "7242"),
//        Cinema("Odeon Epsom", "7197"),
//        Cinema("Odeon Esher", "6955"),
//        Cinema("Odeon Exeter", "7198"),
//        Cinema("Odeon Gerrards Cross", "7199"),
//        Cinema("Odeon Glasgow Springfield Quay", "7240"),
//        Cinema("Odeon Guildford", "7200"),
//        Cinema("Odeon Harrogate", "7201"),
//        Cinema("Odeon Hastings", "7202"),
//        Cinema("Odeon Hatfield", "7203"),
//        Cinema("Odeon Hereford", "7204"),
//        Cinema("Odeon Hereford", "7969"),
//        Cinema("Odeon Huddersfield", "7205"),
//        Cinema("Odeon Hull", "7206"),
//        Cinema("Odeon Kettering", "7207"),
//        Cinema("Odeon Kilmarnock", "7241"),
//        Cinema("Odeon Kingston", "6903"),
//        Cinema("Odeon Leeds-Bradford", "7176"),
//        Cinema("Odeon Leicester", "7211"),
//        Cinema("Odeon Limerick", "7156"),
//        Cinema("Odeon Lincoln", "7213"),
//        Cinema("Odeon Liverpool ONE", "7215"),
//        Cinema("Odeon Liverpool Switch Island", "7214"),
//        Cinema("Odeon Llanelli", "7217"),
//        Cinema("Odeon Loughborough", "7218"),
//        Cinema("Odeon Maidenhead", "7221"),
//        Cinema("Odeon Maidstone", "7220"),
//        Cinema("Odeon Manchester", "7222"),
//        Cinema("Odeon Mansfield", "7223"),
//        Cinema("Odeon Metrocentre", "4841"),
//        Cinema("Odeon Milton Keynes", "7227"),
//        Cinema("Odeon Naas", "7228"),
//        Cinema("Odeon Newark", "7229"),
//        Cinema("Odeon Newbridge", "6645"),
//        Cinema("Odeon Norwich", "7231"),
//        Cinema("Odeon Nuneaton", "7234"),
//        Cinema("Odeon Oxford George Street", "7243"),
//        Cinema("Odeon Oxford Magdalen Street", "7244"),
//        Cinema("Odeon Point Village", "7193"),
//        Cinema("Odeon Port Solent", "4869"),
//        Cinema("Odeon Portlaoise", "7247"),
//        Cinema("Odeon Preston", "7249"),
//        Cinema("Odeon Richmond", "6910"),
//        Cinema("Odeon Richmond Studio", "6911"),
//        Cinema("Odeon Rochdale", "7254"),
//        Cinema("Odeon Salisbury", "7255"),
//        Cinema("Odeon Sheffield", "7257"),
//        Cinema("Odeon Silverlink", "4858"),
//        Cinema("Odeon Southampton", "7260"),
//        Cinema("Odeon Southend", "7259"),
//        Cinema("Odeon Stillorgan", "7262"),
//        Cinema("Odeon Stoke", "7264"),
//        Cinema("Odeon Swadlincote", "7161"),
//        Cinema("Odeon Swansea", "7266"),
//        Cinema("Odeon Tamworth", "7268"),
//        Cinema("Odeon Taunton", "7269"),
//        Cinema("Odeon Telford", "7270"),
//        Cinema("Odeon Thurrock", "4881"),
//        Cinema("Odeon Trafford Centre", "7224"),
//        Cinema("Odeon Trowbridge", "7763"),
//        Cinema("Odeon Tunbridge Wells", "7273"),
//        Cinema("Odeon Uxbridge", "7274"),
//        Cinema("Odeon Warrington", "7275"),
//        Cinema("Odeon Waterford", "7276"),
//        Cinema("Odeon West Bromwich", "7457"),
//        Cinema("Odeon Weston-Super-Mare", "7277"),
//        Cinema("Odeon Worcester", "7278"),
//        Cinema("Odeon Wrexham", "4887")
//      )
//    ),
//    "Cinema City (Hungary) cinemas..." -> Map(
//      "Budapest cinemas" -> Seq(
//        Cinema("Cinema City Allee", "1010202"),
//        Cinema("Cinema City Aréna", "1010203"),
//        Cinema("Cinema City Duna Pláza", "1010206"),
//        Cinema("Cinema City WestEnd", "1010217"),
//        Cinema("Cinema City MOM Park", "1010218"),
//        Cinema("Palace Mammut", "1010219"),
//        Cinema("Cinema City Campona", "1010220")
//      ),
//      "All other cinemas" -> Seq(
//        Cinema("Cinema City Alba", "1010201"),
//        Cinema("Cinema City Balaton", "1010204"),
//        Cinema("Cinema City Debrecen", "1010205"),
//        Cinema("Cinema City Győr", "1010207"),
//        Cinema("Cinema City Kaposvár", "1010208"),
//        Cinema("Cinema City Miskolc", "1010209"),
//        Cinema("Cinema City Nyíregyháza", "1010210"),
//        Cinema("Cinema City Pécs", "1010211"),
//        Cinema("Cinema City Savaria", "1010212"),
//        Cinema("Cinema City Sopron", "1010213"),
//        Cinema("Cinema City Szeged", "1010214"),
//        Cinema("Cinema City Szolnok", "1010215"),
//        Cinema("Cinema City Zalaegerszeg", "1010216")
//      )
//    )
  )

}
