package net.dankito.sitemap

import net.dankito.web.client.JavaHttpClientWebClient
import net.dankito.web.client.WebClient

object DefaultInstances {

    var sitemapXmlParser: SitemapXmlParser = SitemapXmlParser()

    var webClient: WebClient = JavaHttpClientWebClient()

//    var sitemapParser: SitemapParser = SitemapParser(webClient = webClient)

}