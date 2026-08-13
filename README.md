# SitemapParser

SitemapParser is a Kotlin library for finding and parsing sitemaps and sitemap indices. The library focuses on reliable discovery and processing of these files without unnecessary bloat.

## Features

The library provides the following methods for discovering sitemaps:
- **robots.txt**: Extraction of sitemap URLs from a website's `robots.txt`.
- **Standard paths**: Searching for sitemaps at common locations (e.g., `/sitemap.xml`). This behavior is configurable via the `checkStandardPaths` parameter.
- **Pagination**: Automatic discovery of subsequent pages for sitemaps and sitemap indices by checking common naming patterns (e.g., `sitemap1.xml`, `sitemap2.xml`). This behavior is configurable via the `discoverNextPages` parameter.

Additional features:
- Support for standard sitemaps and sitemap indices.
- Support for sitemap extensions: News, Image, and Video.
- Processing of GZIP-compressed sitemaps.
- Implementation using Kotlin Coroutines for asynchronous processing.
- Sitemap diffing service to identify new or updated URLs.

## Integration

### Gradle (Kotlin)

```kotlin
implementation("net.dankito.sitemap:sitemap-parser:1.0.0")
```

### Maven

```xml
<dependency>
    <groupId>net.dankito.sitemap</groupId>
    <artifactId>sitemap-parser</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage Examples

### Finding and Parsing Sitemaps of a Website

```kotlin
val discoveryService = SitemapDiscoveryService()

// Automatically checks robots.txt and standard paths
val results = discoveryService.discover("https://www.example.com")

results.forEach { result ->
    when (result) {
        // a Sitemap
        is SitemapParseResult.UrlSet -> println("Found ${result.urls.size} URLs in Sitemap ${result.sourceUrl}")
        // a Sitemap Index
        is SitemapParseResult.Index -> println("Sitemap Index with ${result.referencedUrls.size} referenced Sitemaps found")
        is SitemapParseResult.Failure -> println("Error parsing ${result.sourceUrl}: ${result.message}")
    }
}
```

### Accessing Sitemap data

```kotlin
if (result is SitemapParseResult.UrlSet) {
    result.urls.forEach { url ->
        println("URL: ${url.location}, Last Modified: ${url.lastModified}")
        
        // Access extensions
        url.image?.let { println("  Image: ${it.url}") }
        url.video?.let { println("  Video: ${it.title}") }
        url.news?.let { println("  News: ${it.name}") }
    }
}
```

## License

Copyright 2026 dankito

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
