package net.dankito.sitemap

import net.dankito.web.client.JavaHttpClientWebClient
import net.dankito.web.client.WebClient

object DefaultInstances {

    var xmlParser: SitemapXmlParser = JacksonSitemapXmlParser()

    var webClient: WebClient = JavaHttpClientWebClient()

    var sitemapParser: SitemapFetcherAndParser = SitemapFetcherAndParser(xmlParser, webClient)

}