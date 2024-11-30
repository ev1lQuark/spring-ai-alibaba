/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.ai.plugin.crawler.service.impl;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.alibaba.cloud.ai.plugin.crawler.CrawlerJinaProperties;
import com.alibaba.cloud.ai.plugin.crawler.constant.CrawlerConstants;
import com.alibaba.cloud.ai.plugin.crawler.exception.CrawlerServiceException;
import com.alibaba.cloud.ai.plugin.crawler.service.AbstractCrawlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yuluo
 * @author <a href="mailto:yuluo08290126@gmail.com">yuluo</a>
 *
 * Reference: https://jina.ai/reader/
 */

public class CrawlerJinaServiceImpl extends AbstractCrawlerService {

	private static final Logger logger = LoggerFactory.getLogger(CrawlerJinaServiceImpl.class);

	private final CrawlerJinaProperties jinaProperties;

	private static final String defaultInjectPageScriptInfos =
			"""
					// Remove headers, footers, navigation elements
					\\ndocument.querySelectorAll('header, footer, nav').forEach(el => el.remove());
					\\n\\n// Or a url that returns a valid JavaScript code snippet\\n// https://example.com/script.js"
					""";


	public CrawlerJinaServiceImpl(CrawlerJinaProperties jinaProperties) {
		this.jinaProperties = jinaProperties;
	}

	@Override
	public String run(String targetUrl) {

		if (this.preCheck(targetUrl)) {
			throw new CrawlerServiceException("Target url error, please check the target URL");
		}

		try {
			URL url = URI.create(CrawlerConstants.JINA_BASE_URL).toURL();
			logger.info("Jina reader request url: {}", url);

			HttpURLConnection connection = this.initHttpURLConnection(url, this.getOptions());

			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			String requestBody = String.format("""
					{
					    "url": "%s",
					    "injectPageScript": [%s]
					}""", targetUrl, defaultInjectPageScriptInfos);
			logger.info("Jina reader request body: {}", requestBody);
			wr.writeBytes(requestBody);
			wr.flush();
			wr.close();

			return this.getResponse(connection);
		}
		catch (IOException e) {
			throw new CrawlerServiceException("Jina reader request failed: " + e.getMessage());
		}
	}

	private Map<String, String> getOptions() {

		Map<String, String> map = new HashMap<>();

		if (Objects.nonNull(jinaProperties.getLocale())) {
			map.put(CrawlerConstants.JinaHeaders.X_LOCALE, jinaProperties.getLocale());
		}
		if (Objects.nonNull(jinaProperties.getNoCache())) {
			map.put(CrawlerConstants.JinaHeaders.X_NO_CACHE, jinaProperties.getNoCache().toString());
		}
		if (Objects.nonNull(jinaProperties.getProxyUrl())) {
			map.put(CrawlerConstants.JinaHeaders.X_PROXY_URL, jinaProperties.getProxyUrl());
		}
		if (Objects.nonNull(jinaProperties.getRemoveSelector())) {
			map.put(CrawlerConstants.JinaHeaders.X_REMOVE_SELECTOR, jinaProperties.getRemoveSelector());
		}
		if (Objects.nonNull(jinaProperties.getRetainImages())) {
			map.put(CrawlerConstants.JinaHeaders.X_RETAIN_IMAGES, jinaProperties.getRetainImages());
		}
		if (Objects.nonNull(jinaProperties.getSetCookie())) {
			map.put(CrawlerConstants.JinaHeaders.X_SET_COOKIE, jinaProperties.getSetCookie());
		}
		if (Objects.nonNull(jinaProperties.getWithGeneratedAlt())) {
			map.put(CrawlerConstants.JinaHeaders.X_WITH_GENERATED_ALT, jinaProperties.getWithGeneratedAlt().toString());
		}
		if (Objects.nonNull(jinaProperties.getWithIframe())) {
			map.put(CrawlerConstants.JinaHeaders.X_WITH_IFRAME, jinaProperties.getWithIframe().toString());
		}
		if (Objects.nonNull(jinaProperties.getWithShadowDom())) {
			map.put(CrawlerConstants.JinaHeaders.X_WITH_SHADOW_DOM, jinaProperties.getWithShadowDom().toString());
		}
		if (Objects.nonNull(jinaProperties.getWithImagesSummary())) {
			map.put(CrawlerConstants.JinaHeaders.X_WITH_IMAGES_SUMMARY, jinaProperties.getWithImagesSummary()
					.toString());
		}

		return map;
	}

}
