package gr.aschoinas.social.poc.persistence.rowmapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.social.connect.ConnectionData;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Andreas Schoinas
 */
public class ConnectionDataRowMapper implements RowMapper<ConnectionData>{

	@Override
	public ConnectionData mapRow (ResultSet rs, int rowNum) throws SQLException {

		return new ConnectionData(rs.getString("providerId"),
				rs.getString("providerUserId"),
				rs.getString("displayName"),
				rs.getString("profileUrl"),
				rs.getString("imageUrl"),
				rs.getString("accessToken"),
				rs.getString("secret"),
				rs.getString("refreshToken"),
				rs.getLong("expireTime"));

	}

}
