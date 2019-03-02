/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.cbi.searchloteriafederal;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Tiago
 */
public final class Lotofacil {

    private final String url;
    private final WebClient wc;

    public static void main(String[] args) {
        Lotofacil lotofacil = new Lotofacil();
        Map<String, List<Long>> resultados = lotofacil.getResultados();
        resultados.keySet().forEach(concurso -> System.out.println(concurso));
        resultados.forEach((k, v) -> System.out.println(v));
    }

    public Lotofacil() {
        this.url = "http://www.loterias.caixa.gov.br/wps/portal/loterias/landing/lotofacil";
        configLog();
        wc = new WebClient(BrowserVersion.CHROME);
        wc.getOptions().setJavaScriptEnabled(true);
        wc.getOptions().setCssEnabled(true);
        wc.getOptions().setDownloadImages(true);
        wc.getOptions().setPopupBlockerEnabled(false);
        wc.getOptions().setTimeout(200000);
    }

    private HtmlPage getIndexPage() {
        try {
            HtmlPage index = wc.getPage(url);
            System.out.println(index.getTitleText());
            System.out.println(index.getUrl().toString());
            return index;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Map<String, List<Long>> getResultados() {
        try {
            Map<String, List<Long>> map = new HashMap();
            List<Long> resultados = new ArrayList();
            HtmlPage index = getIndexPage();
            List<HtmlElement> lista = index.getBody().getElementsByAttribute("table", "class", "simple-table lotofacil");
            List<HtmlElement> spans = index.getBody().getElementsByAttribute("span", "class", "ng-binding");
            HtmlTable tabela = (HtmlTable) lista.get(0);
            HtmlSpan concurso = (HtmlSpan) spans.get(0);
            wc.waitForBackgroundJavaScript(9000L);
            String ccs = concurso.getTextContent().replaceAll("\\s+", " ").trim();
            List<HtmlTableCell> cells = tabela.getBodies().get(0).getElementsByAttribute("td", "ng-repeat", "dezena in resultadoLinha");
            cells.forEach(cell -> resultados.add(Long.parseLong(cell.getTextContent())));
            map.put(ccs, resultados);
            getDataProximoSorteio(index);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getDataProximoSorteio(HtmlPage index) {
        try {
            List<HtmlElement> division = index.getBody().getElementsByAttribute("div", "class", "resultado-loteria");
            List<HtmlElement> paragrafos = division.get(0).getElementsByAttribute("p", "class", "ng-binding");
            String data = paragrafos.get(0).getTextContent().trim();
            data = "Data do próximo sorteio: ".concat(data.substring((data.length() - 10), data.length()));
            System.out.println(data);
            return data;
        } catch (Exception e) {
            return "";
        }
    }

    private void configLog() {
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
        CookieHandler.setDefault(new java.net.CookieManager(null, CookiePolicy.ACCEPT_ALL));
    }

    public void test() {
        try {
            java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
            java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);

            CookieHandler.setDefault(new java.net.CookieManager(null, CookiePolicy.ACCEPT_ALL));
            String URL = "http://www.loterias.caixa.gov.br/wps/portal/loterias/landing/lotofacil";
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            HtmlPage page = webClient.getPage(URL);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.waitForBackgroundJavaScript(9000);

            System.out.println(page.getTitleText());
            System.out.println(page.getUrl().toString());

            List<HtmlElement> spans = page.getBody().getElementsByAttribute("span", "class", "ng-binding");
            HtmlSpan concurso = (HtmlSpan) spans.get(0);
            String ccs = concurso.getTextContent().replaceAll("\\s+", " ").trim();
            System.out.println(ccs);

            List<Long> resultados = new ArrayList();
            List<HtmlElement> lista = page.getBody().getElementsByAttribute("table", "class", "simple-table lotofacil");
            HtmlTable tabela = (HtmlTable) lista.get(0);
            List<HtmlTableCell> cells = tabela.getBodies().get(0).getElementsByAttribute("td", "ng-repeat", "dezena in resultadoLinha");
            cells.forEach(cell -> resultados.add(Long.parseLong(cell.getTextContent())));
            System.out.println(resultados);
        } catch (Exception e) {
        }
    }

}
