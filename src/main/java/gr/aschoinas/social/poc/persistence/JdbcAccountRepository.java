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
package gr.aschoinas.social.poc.persistence;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.inject.Inject;

import gr.aschoinas.social.poc.entity.Account;
import gr.aschoinas.social.poc.persistence.rowmapper.ConnectionDataRowMapper;
import org.codehaus.groovy.tools.shell.IO;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.connect.ConnectionData;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Repository
public class JdbcAccountRepository implements AccountRepository {

	private final JdbcTemplate jdbcTemplate;

	private final PasswordEncoder passwordEncoder;

	@Inject
	public JdbcAccountRepository(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
		this.jdbcTemplate = jdbcTemplate;
		this.passwordEncoder = passwordEncoder;
	}

	private  ResourceBundle sqlCommands;

	private String getSqlCommand(final String key) throws IOException {
		final String result = sqlCommands.getString(key);
		if (!StringUtils.hasText(result)) {
			throw new IOException(MessageFormat.format("Resource bundle key {0} not found.", key));
		}
		return result;
	}

	@Transactional
	public void createAccount(Account user) throws UsernameAlreadyInUseException {
		try {
			jdbcTemplate.update(
					getSqlCommand("create.account"),
					user.getFirstName(), user.getLastName(), user.getUsername(),
					passwordEncoder.encode(user.getPassword()));
		} catch (DuplicateKeyException e) {
			throw new UsernameAlreadyInUseException(user.getUsername());
		}catch(IOException ioe){
			//TODO:: ignore for now
		}

	}

	public Account findAccountByUsername(String username) {
		try {
			return jdbcTemplate.queryForObject(getSqlCommand("retrieve.account"),
					new RowMapper<Account>() {
						public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
							return new Account(rs.getString("username"), null, rs.getString("firstName"), rs
									.getString("lastName"));
						}
					}, username);
		}catch (IOException ioe){
			//TODO:: handle, ignore for now
			return null;
		}
	}

	@Override
	public ConnectionData getFacebookDataByUserId(String userId) {
		try {
			return jdbcTemplate.queryForObject(getSqlCommand("retrieve.facebook.connection.data"), new ConnectionDataRowMapper(), userId);
		}catch (IOException ioe){
			//TODO::: handle, ignore for now
			return null;
		}
	}

}
