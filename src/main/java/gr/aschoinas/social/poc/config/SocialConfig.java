/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gr.aschoinas.social.poc.config;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.sql.DataSource;

import gr.aschoinas.social.poc.facebook.PostToWallAfterConnectInterceptor;
import gr.aschoinas.social.poc.twitter.TweetAfterConnectInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.connect.web.ReconnectFilter;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.facebook.web.DisconnectController;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.social.linkedin.connect.LinkedInConnectionFactory;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Spring Social Configuration.
 * This configuration is demonstrating the use of the simplified Spring Social configuration options from Spring Social 1.1.
 * 
 * @author Craig Walls
 */
@Configuration
@EnableSocial
public class SocialConfig extends SocialConfigurerAdapter {

	@Inject
	private DataSource dataSource;
	@PostConstruct

	private void init() {
		try {
			String[] fieldsToMap = {"id", "about", "age_range", "birthday",
					"context", "cover", "currency", "devices", "education",
					"email", "favorite_athletes", "favorite_teams",
					"first_name", "gender", "hometown", "inspirational_people",
					"installed", "install_type", "is_verified", "languages",
					"last_name", "link", "locale", "location", "meeting_for",
					"middle_name", "name", "name_format", "political",
					"quotes", "payment_pricepoints", "relationship_status",
					"religion", "security_settings", "significant_other",
					"sports", "test_group", "timezone", "third_party_id",
					"updated_time", "verified", "viewer_can_send_gift",
					"website", "work"};

			Field field = Class.forName(
					"org.springframework.social.facebook.api.UserOperations")
					.getDeclaredField("PROFILE_FIELDS");
			field.setAccessible(true);

			Field modifiers = field.getClass().getDeclaredField("modifiers");
			modifiers.setAccessible(true);
			modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			field.set(null, fieldsToMap);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	@Override
	public void addConnectionFactories(ConnectionFactoryConfigurer cfConfig, Environment env) {
		cfConfig.addConnectionFactory(new TwitterConnectionFactory(env.getProperty("twitter.appKey"), env.getProperty("twitter.appSecret")));
		cfConfig.addConnectionFactory(new FacebookConnectionFactory(env.getProperty("facebook.appKey"), env.getProperty("facebook.appSecret")));
		cfConfig.addConnectionFactory(new LinkedInConnectionFactory(env.getProperty("linkedin.appKey"), env.getProperty("linkedin.appSecret")));
	}
	
	@Override
	public UserIdSource getUserIdSource() {
		return new UserIdSource() {			
			@Override
			public String getUserId() {
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				if (authentication == null) {
					throw new IllegalStateException("Unable to get a ConnectionRepository: no user signed in");
				}
				return authentication.getName();
			}
		};
	}
	
	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		return new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
	}

	@Bean
	public ConnectController connectController(ConnectionFactoryLocator connectionFactoryLocator, ConnectionRepository connectionRepository) {
		ConnectController connectController = new ConnectController(connectionFactoryLocator, connectionRepository);
		connectController.addInterceptor(new PostToWallAfterConnectInterceptor());
		connectController.addInterceptor(new TweetAfterConnectInterceptor());
		return connectController;
	}
	
	@Bean
	public DisconnectController disconnectController(UsersConnectionRepository usersConnectionRepository, Environment environment) {
		return new DisconnectController(usersConnectionRepository, environment.getProperty("facebook.appSecret"));
	}

	@Bean
	public ReconnectFilter apiExceptionHandler(UsersConnectionRepository usersConnectionRepository, UserIdSource userIdSource) {
		return new ReconnectFilter(usersConnectionRepository, userIdSource);
	}

	@Bean
	@Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)
	public Facebook facebook(ConnectionRepository repository) {
		Connection<Facebook> connection = repository.findPrimaryConnection(Facebook.class);
		return connection != null ? connection.getApi() : null;
	}
	
	@Bean
	@Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)
	public Twitter twitter(ConnectionRepository repository) {
		Connection<Twitter> connection = repository.findPrimaryConnection(Twitter.class);
		return connection != null ? connection.getApi() : null;
	}
	
	@Bean
	@Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)
	public LinkedIn linkedin(ConnectionRepository repository) {
		Connection<LinkedIn> connection = repository.findPrimaryConnection(LinkedIn.class);
		return connection != null ? connection.getApi() : null;
	}

}
