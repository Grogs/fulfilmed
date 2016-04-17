package me.gregd.cineworld.frontend.components

import japgolly.scalajs.react.vdom.prefix_<^._

object IndexPage {

  val label = "label".reactAttr

  def apply() = {
    <.div(^.id:="indexPage",
      <.styleTag(^.`type`:="text/css",
        """
          |		#title {
          |			font-family: HelveticaNeue-Light;
          |			letter-spacing: 3px;
          |			font-size: 82px;
          |			color: white;
          |			line-height: 95px;
          |			text-shadow: 0px 2px 4px #225968;
          |			padding-top: 100px;
          |		}
          |		#blurb {
          |/*			font-family: HelveticaNeue-Light;
          |*/
          |
          |				font-family: "HelveticaNeue", "Helvetica Neue", Helvetica, Arial, "Lucida Grande", sans-serif;
          |				font-weight: 200;
          |
          |			font-size: 24px;
          |			color: #f5f5f5;
          |			line-height: 29px;
          |			padding-top: 40px;
          |			padding-bottom: 50px;
          |			/*max-width: 540px;*/
          |			/* A better way to find: */
          |		}
          |
          |		#top {
          |			background: #690B0B url(movie_reel.svg) no-repeat 45% 170%;
          |			background-size: 350px 400px;
          |			text-align: center;
          |			padding-bottom: 150px;
          |		}
          |
          |
          |
          |		select {
          |		    font-size: 24px;
          |		    border: 2px solid white;
          |		    border-radius: 10px;
          |		    color: white;
          |		    padding: 12px;
          |		    width: 490px;
          |		    -webkit-appearance: none;
          |
          |		    background:url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='50px' height='50px'><polyline points='46.139,15.518 25.166,46.49 4.193,15.519' fill='white' /></svg>") right no-repeat;
          |
          |		    background-color: #690B0B;
          |		    background-position: right 15px top 16px;
          |		    background-size: 18px 18px;
          |		}
          |		select:active {
          |		    border: 1px solid #fff;
          |		}
          |
          |		#description {
          |			font-family: HelveticaNeue;
          |			font-size: 24px;
          |			color: #323232;
          |			line-height: 29px;
          |			/* What?: */
          |			max-width: 600px;
          |			margin-right: auto;
          |			margin-left: auto;
          |		}
          |		#description > h3 {
          |			margin-bottom: 0px;
          |		}
          |		#description > p {
          |			margin-top: 0px;
          |		}
          |
          |		body {
          |			margin: 0;
          |		}
          |
        """.stripMargin),
      <.styleTag(^.`type`:="text/css",
        """
          |    #films, #top {
          |        text-align: center;
          |        -webkit-transition:all 1.0s ease-in-out;
          |        -moz-transition:all 1.0s ease-in-out;
          |        -o-transition:all 1.0s ease-in-out;
          |        transition:all 1.0s ease-in-out;
          |    }
          |    .disabled { display: none; }
          |    #films.hidden{
          |    	opacity: 0;
          |		-webkit-transform: scale(0.001, 0.001);
          |    }
          |    #top.hidden{
          |    	opacity: 0;
          |    	margin-top: -9999px;
          |    	position: absolute;
          |    	visibility: hidden;
          |    }
          |    select#odeon-cinemas, select#cinema-city-cinemas-hungary {
          |    	margin-top: 20px;
          |    }
          |
        """.stripMargin),
      <.div(^.id:="top",
        <.div(^.id:="title", "Fulfilmed"),
        <.div(^.id:="blurb", "A better way to find the best films at your local cinema"),
        <.script(^.`type`:="text/javascript", """
          function selectCinema(e) {location.href = "films.html#/cinema/"+e.value}
        """),
        <.div(
          <.select(^.id:="cinemas", ^.`class`:=".flat", ^.onChange:="selectCinema(this)",
            <.option( ^.value:="?", ^.selected:="selected", ^.disabled:="disabled", "Cineworld cinemas..."),
            <.optgroup( label:="London cinemas",
              <.option( ^.value:="45", "London - Bexleyheath"),
              <.option( ^.value:="10", "London - Chelsea"),
              <.option( ^.value:="22", "London - Enfield"),
              <.option( ^.value:="25", "London - Feltham"),
              <.option( ^.value:="26", "London - Fulham Road"),
              <.option( ^.value:="30", "London - Hammersmith"),
              <.option( ^.value:="32", "London - Haymarket"),
              <.option( ^.value:="37", "London - Ilford"),
              <.option( ^.value:="53", "London - Shaftesbury Avenue"),
              <.option( ^.value:="60", "London - Staples Corner"),
              <.option( ^.value:="79", "London - The O2, Greenwich "),
              <.option( ^.value:="65", "London - Wandsworth"),
              <.option( ^.value:="89", "London - Wembley"),
              <.option( ^.value:="66", "London - West India Quay"),
              <.option( ^.value:="70", "London - Wood Green")
            ),
            <.optgroup( label:="All other cinemas",
              <.option( ^.value:="1", "Aberdeen - Queens Links"),
              <.option( ^.value:="78", "Aberdeen - Union Square"),
              <.option( ^.value:="87", "Aldershot"),
              <.option( ^.value:="12", "Ashford"),
              <.option( ^.value:="34", "Bedford"),
              <.option( ^.value:="56", "Birmingham"),
              <.option( ^.value:="67", "Boldon Tyne and Wear"),
              <.option( ^.value:="72", "Bolton"),
              <.option( ^.value:="73", "Bradford"),
              <.option( ^.value:="2", "Braintree"),
              <.option( ^.value:="3", "Brighton"),
              <.option( ^.value:="4", "Bristol"),
              <.option( ^.value:="5", "Burton upon Trent"),
              <.option( ^.value:="6", "Bury St. Edmunds"),
              <.option( ^.value:="7", "Cambridge"),
              <.option( ^.value:="8", "Cardiff"),
              <.option( ^.value:="9", "Castleford"),
              <.option( ^.value:="11", "Cheltenham"),
              <.option( ^.value:="14", "Chesterfield"),
              <.option( ^.value:="15", "Chichester"),
              <.option( ^.value:="16", "Crawley"),
              <.option( ^.value:="17", "Didcot"),
              <.option( ^.value:="18", "Didsbury"),
              <.option( ^.value:="19", "Dundee"),
              <.option( ^.value:="20", "Eastbourne"),
              <.option( ^.value:="21", "Edinburgh"),
              <.option( ^.value:="24", "Falkirk"),
              <.option( ^.value:="88", "Glasgow - IMAX at GSC"),
              <.option( ^.value:="27", "Glasgow - Parkhead"),
              <.option( ^.value:="28", "Glasgow - Renfrew Street"),
              <.option( ^.value:="90", "Gloucester Quays"),
              <.option( ^.value:="31", "Harlow"),
              <.option( ^.value:="76", "Haverhill"),
              <.option( ^.value:="33", "High Wycombe"),
              <.option( ^.value:="35", "Hull"),
              <.option( ^.value:="36", "Huntingdon"),
              <.option( ^.value:="38", "Ipswich"),
              <.option( ^.value:="39", "Isle Of Wight"),
              <.option( ^.value:="40", "Jersey"),
              <.option( ^.value:="83", "Leigh"),
              <.option( ^.value:="41", "Liverpool"),
              <.option( ^.value:="42", "Llandudno"),
              <.option( ^.value:="43", "Luton"),
              <.option( ^.value:="44", "Middlesbrough"),
              <.option( ^.value:="46", "Milton Keynes"),
              <.option( ^.value:="47", "Newport Wales"),
              <.option( ^.value:="48", "Northampton"),
              <.option( ^.value:="49", "Nottingham"),
              <.option( ^.value:="50", "Rochester"),
              <.option( ^.value:="51", "Rugby"),
              <.option( ^.value:="52", "Runcorn"),
              <.option( ^.value:="54", "Sheffield"),
              <.option( ^.value:="55", "Shrewsbury"),
              <.option( ^.value:="57", "Solihull"),
              <.option( ^.value:="58", "Southampton"),
              <.option( ^.value:="59", "St Helens"),
              <.option( ^.value:="91", "St Neots"),
              <.option( ^.value:="61", "Stevenage"),
              <.option( ^.value:="62", "Stockport"),
              <.option( ^.value:="63", "Swindon"),
              <.option( ^.value:="92", "Telford"),
              <.option( ^.value:="64", "Wakefield"),
              <.option( ^.value:="68", "Weymouth"),
              <.option( ^.value:="77", "Witney"),
              <.option( ^.value:="69", "Wolverhampton"),
              <.option( ^.value:="71", "Yeovil")
            )
          )
        ),

        <.div(
          <.select(^.id:="odeon-cinemas", ^.`class`:=".flat", ^.onChange:="selectCinema(this)",
            <.option( ^.value:="?", ^.selected:="selected", ^.disabled:="disabled", "Odeon cinemas..."),
            <.optgroup( label:="London cinemas",
              <.option( ^.value:="6883", "Odeon BFI IMAX"),
              <.option( ^.value:="6906", "Odeon Camden"),
              <.option( ^.value:="1503", "Odeon Covent Garden"),
              <.option( ^.value:="6897", "Odeon Greenwich"),
              <.option( ^.value:="6900", "Odeon Holloway"),
              <.option( ^.value:="1516", "Odeon Kensington"),
              <.option( ^.value:="6889", "Odeon Lee Valley"),
              <.option( ^.value:="1517", "Odeon Leicester Square"),
              <.option( ^.value:="1518", "Odeon Marble Arch"),
              <.option( ^.value:="6904", "Odeon Muswell Hill"),
              <.option( ^.value:="1520", "Odeon Panton Street"),
              <.option( ^.value:="6909", "Odeon Putney"),
              <.option( ^.value:="6927", "Odeon South Woodford"),
              <.option( ^.value:="6916", "Odeon Streatham"),
              <.option( ^.value:="6777", "Odeon Studios"),
              <.option( ^.value:="6907", "Odeon Surrey Quays"),
              <.option( ^.value:="5208", "Odeon Swiss Cottage"),
              <.option( ^.value:="1522", "Odeon Tottenham Court Road"),
              <.option( ^.value:="1523", "Odeon West End"),
              <.option( ^.value:="6155", "Odeon Whiteleys"),
              <.option( ^.value:="6850", "Odeon Whiteleys - The Lounge"),
              <.option( ^.value:="6925", "Odeon Wimbledon")
    ),
            <.optgroup( label:="All other cinemas",
              <.option( ^.value:="7165", "Odeon Andover"),
              <.option( ^.value:="7167", "Odeon Aylesbury"),
              <.option( ^.value:="7235", "Odeon Ayr"),
              <.option( ^.value:="7168", "Odeon Banbury"),
              <.option( ^.value:="6879", "Odeon Barnet"),
              <.option( ^.value:="7169", "Odeon Basingstoke"),
              <.option( ^.value:="7236", "Odeon Bath"),
              <.option( ^.value:="6881", "Odeon Beckenham"),
              <.option( ^.value:="7170", "Odeon Belfast"),
              <.option( ^.value:="7171", "Odeon Birmingham Broadway Plaza"),
              <.option( ^.value:="7172", "Odeon Birmingham New Street"),
              <.option( ^.value:="7173", "Odeon Blackpool"),
              <.option( ^.value:="6795", "Odeon Blanchardstown"),
              <.option( ^.value:="7175", "Odeon Bournemouth"),
              <.option( ^.value:="7177", "Odeon Bracknell"),
              <.option( ^.value:="4826", "Odeon Braehead"),
              <.option( ^.value:="7180", "Odeon Bridgend"),
              <.option( ^.value:="7179", "Odeon Brighton"),
              <.option( ^.value:="7178", "Odeon Bristol"),
              <.option( ^.value:="7181", "Odeon Bromborough"),
              <.option( ^.value:="7182", "Odeon Canterbury"),
              <.option( ^.value:="7183", "Odeon Cardiff"),
              <.option( ^.value:="7184", "Odeon Cavan"),
              <.option( ^.value:="7185", "Odeon Chatham"),
              <.option( ^.value:="7186", "Odeon Chelmsford"),
              <.option( ^.value:="7187", "Odeon Colchester"),
              <.option( ^.value:="6796", "Odeon Coolock"),
              <.option( ^.value:="7188", "Odeon Coventry"),
              <.option( ^.value:="7189", "Odeon Crewe"),
              <.option( ^.value:="7190", "Odeon Darlington"),
              <.option( ^.value:="7191", "Odeon Derby"),
              <.option( ^.value:="7192", "Odeon Dorchester"),
              <.option( ^.value:="7194", "Odeon Dudley Merry Hill"),
              <.option( ^.value:="7238", "Odeon Dumfries"),
              <.option( ^.value:="7237", "Odeon Dundee"),
              <.option( ^.value:="7195", "Odeon Dunfermline"),
              <.option( ^.value:="7196", "Odeon East Kilbride"),
              <.option( ^.value:="7239", "Odeon Edinburgh Lothian Road"),
              <.option( ^.value:="7242", "Odeon Edinburgh Wester Hailes"),
              <.option( ^.value:="7197", "Odeon Epsom"),
              <.option( ^.value:="6955", "Odeon Esher"),
              <.option( ^.value:="7198", "Odeon Exeter"),
              <.option( ^.value:="7199", "Odeon Gerrards Cross"),
              <.option( ^.value:="7240", "Odeon Glasgow Springfield Quay"),
              <.option( ^.value:="7200", "Odeon Guildford"),
              <.option( ^.value:="7201", "Odeon Harrogate"),
              <.option( ^.value:="7202", "Odeon Hastings"),
              <.option( ^.value:="7203", "Odeon Hatfield"),
              <.option( ^.value:="7204", "Odeon Hereford"),
              <.option( ^.value:="7969", "Odeon Hereford"),
              <.option( ^.value:="7205", "Odeon Huddersfield"),
              <.option( ^.value:="7206", "Odeon Hull"),
              <.option( ^.value:="7207", "Odeon Kettering"),
              <.option( ^.value:="7241", "Odeon Kilmarnock"),
              <.option( ^.value:="6903", "Odeon Kingston"),
              <.option( ^.value:="7176", "Odeon Leeds-Bradford"),
              <.option( ^.value:="7211", "Odeon Leicester"),
              <.option( ^.value:="7156", "Odeon Limerick"),
              <.option( ^.value:="7213", "Odeon Lincoln"),
              <.option( ^.value:="7215", "Odeon Liverpool ONE"),
              <.option( ^.value:="7214", "Odeon Liverpool Switch Island"),
              <.option( ^.value:="7217", "Odeon Llanelli"),
              <.option( ^.value:="7218", "Odeon Loughborough"),
              <.option( ^.value:="7221", "Odeon Maidenhead"),
              <.option( ^.value:="7220", "Odeon Maidstone"),
              <.option( ^.value:="7222", "Odeon Manchester"),
              <.option( ^.value:="7223", "Odeon Mansfield"),
              <.option( ^.value:="4841", "Odeon Metrocentre"),
              <.option( ^.value:="7227", "Odeon Milton Keynes"),
              <.option( ^.value:="7228", "Odeon Naas"),
              <.option( ^.value:="7229", "Odeon Newark"),
              <.option( ^.value:="6645", "Odeon Newbridge"),
              <.option( ^.value:="7231", "Odeon Norwich"),
              <.option( ^.value:="7234", "Odeon Nuneaton"),
              <.option( ^.value:="7243", "Odeon Oxford George Street"),
              <.option( ^.value:="7244", "Odeon Oxford Magdalen Street"),
              <.option( ^.value:="7193", "Odeon Point Village"),
              <.option( ^.value:="4869", "Odeon Port Solent"),
              <.option( ^.value:="7247", "Odeon Portlaoise"),
              <.option( ^.value:="7249", "Odeon Preston"),
              <.option( ^.value:="6910", "Odeon Richmond"),
              <.option( ^.value:="6911", "Odeon Richmond Studio"),
              <.option( ^.value:="7254", "Odeon Rochdale"),
              <.option( ^.value:="7255", "Odeon Salisbury"),
              <.option( ^.value:="7257", "Odeon Sheffield"),
              <.option( ^.value:="4858", "Odeon Silverlink"),
              <.option( ^.value:="7260", "Odeon Southampton"),
              <.option( ^.value:="7259", "Odeon Southend"),
              <.option( ^.value:="7262", "Odeon Stillorgan"),
              <.option( ^.value:="7264", "Odeon Stoke"),
              <.option( ^.value:="7161", "Odeon Swadlincote"),
              <.option( ^.value:="7266", "Odeon Swansea"),
              <.option( ^.value:="7268", "Odeon Tamworth"),
              <.option( ^.value:="7269", "Odeon Taunton"),
              <.option( ^.value:="7270", "Odeon Telford"),
              <.option( ^.value:="4881", "Odeon Thurrock"),
              <.option( ^.value:="7224", "Odeon Trafford Centre"),
              <.option( ^.value:="7763", "Odeon Trowbridge"),
              <.option( ^.value:="7273", "Odeon Tunbridge Wells"),
              <.option( ^.value:="7274", "Odeon Uxbridge"),
              <.option( ^.value:="7275", "Odeon Warrington"),
              <.option( ^.value:="7276", "Odeon Waterford"),
              <.option( ^.value:="7457", "Odeon West Bromwich"),
              <.option( ^.value:="7277", "Odeon Weston-Super-Mare"),
              <.option( ^.value:="7278", "Odeon Worcester"),
              <.option( ^.value:="4887", "Odeon Wrexham")
            )
          )
        ),

        <.div(
          <.select(^.id:="cinema-city-cinemas-hungary", ^.`class`:=".flat", ^.onChange:="selectCinema(this)",
            <.option( ^.value:="?", ^.selected:="selected", ^.disabled:="disabled", "Cinema City (Hungary) cinemas..."),
            <.optgroup( label:="Budapest",
              <.option( ^.value:="1010202", "Cinema City Allee"),
              <.option( ^.value:="1010203", "Cinema City Aréna"),
              <.option( ^.value:="1010206", "Cinema City Duna Pláza"),
              <.option( ^.value:="1010217", "Cinema City WestEnd"),
              <.option( ^.value:="1010218", "Cinema City MOM Park"),
              <.option( ^.value:="1010219", "Palace Mammut"),
              <.option( ^.value:="1010220", "Cinema City Campona")
            ),
            <.optgroup( label:="Countryside",
              <.option( ^.value:="1010201", "Cinema City Alba"),
              <.option( ^.value:="1010204", "Cinema City Balaton"),
              <.option( ^.value:="1010205", "Cinema City Debrecen"),
              <.option( ^.value:="1010207", "Cinema City Győr"),
              <.option( ^.value:="1010208", "Cinema City Kaposvár"),
              <.option( ^.value:="1010209", "Cinema City Miskolc"),
              <.option( ^.value:="1010210", "Cinema City Nyíregyháza"),
              <.option( ^.value:="1010211", "Cinema City Pécs"),
              <.option( ^.value:="1010212", "Cinema City Savaria"),
              <.option( ^.value:="1010213", "Cinema City Sopron"),
              <.option( ^.value:="1010214", "Cinema City Szeged"),
              <.option( ^.value:="1010215", "Cinema City Szolnok"),
              <.option( ^.value:="1010216", "Cinema City Zalaegerszeg")
            )
          )
        ),


      <.div(^.id:="description",
        <.h3("What?"),
        <.p("Fulfilmed lets you view the movies airing at your local cinema. It adds some features over your Cinema's standard website; inline movie ratings, sorting/filtering by rating, mark films as watched."),

        <.h3("Why?"),
        <.p("I want to easily, at a glance, figure out which film to go see at the cinema. I can't do that with my cinema's website - I have to google/research each film individually. "),

        <.h3("What's next?"),
        <.p("Currently only Cineworld and Odeon cinemas are supported; I may add more. Beyond that, I want to receive notifications about upcoming high rated films.")

      )
    )
    )
  }

}
