Mandatory 3  
  
Rollene i teamet har vært noe blandet, men vi har ikke tenkt å endre på noen roller.  
Vi skal heller være mer konkret på hvem som jobber med hva. Vi har laget et Project board for å hjelpe med dette.  
For oss er Teamlead rollen hovedsakelig å ha en oversikt over hva som skjer i gruppen,  
og å bestemme hvor fokuset vårt blir fremover.  
Kundekontakt har ansvar for en oversikt på hvilke krav vi får av kunden, og verifisere at disse blir møtt.  
Ellers har vi backend og frontend roller, som da respektivt har ansvar for mekanikk, og design aspekter av prosjektet.  
  
For erfaring med prosjektmetodikk er Project boardet vi har laget på github et viktig steg.  
Vi har nå en prioriteringsliste med å oppdatere Porject board, for så å finne opgaver vi skal jobbe med fra boardet,  
og så fakisk jobbe med vår oppgave, dette blir metodikken vår fremover.  
Ellers har vi tatt i bruk Branches, som var på listen vår over forbedringer ved forige oblig.  
  
Gruppedynamikken fungerer bra.  
  
Kommunikasjonen er et forbedrinspunkt, vi har hatt noe problem med at vi jobber på samme oppgave,
så vi har et mål om å få bedre kommunikasjon i front-end og back-end teamene.
Ellers er kommunikasjonen vår bra, med tanke på at dersom noen ikke kan komme blir det gitt beskjed,
og vi har produktive samtaler når vi møtes.

For et tilbakeblikk på hva vi har gjort innenfor prosjektstruktur er jo Project board og branches to store fremskritt.  
Vi har enda noe problemer med overlapp på rollene våre, men Project Board hjelper noe her.
Vi kan enda bli flinkere med bruken av Project Board, og kommunikasjon innad i front-end og back-end teamene.
  
Forbedringspunkter vi har snakket om var å begynne å bruke branches, og å ha en struktur på møtene våre.  
Vi har allerede begynt å bruke disse endringene, men vi må enda tre til med kommunikasjons forbedringer.
  
Vår liste over krav ble en ganske ekstensiv liste, vi lagde et excel dokument som inneholder listen,  
prioriteringen, og en checkliste for når vi har oppfylt kravene. Så vi kjører tester for å verifisere, for så å markere  
på listen. Listen er filen "Requirements w priority.xlsx" som er vedlagt og oppdatert for vår status når vi leverer.

For å kjøre testene våre kjør src/test/java/inf112/skeleton/app/AppTest.java
Angående hvordan testene skal fungere er dette dokumentert i samme filen.

Etter du runner main så runnes create() ein gong for å plassere brikker og kartet til GUIen. Render() er der alle
metoder kjøres frå, utenom touchUp, touchDown og touchDrgged. touchDown er der eg sjekker om du er inne i et kort når
du skal flytte eit kort, derreter kan du flytte kortet ved hjelp av touchDragged. Deretter når du slepper kortet kalles
touchUp og kortet vil enten sitte fast i kortslotten eller gå tilbake til sin default posisjon.